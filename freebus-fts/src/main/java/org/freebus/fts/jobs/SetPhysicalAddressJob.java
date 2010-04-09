package org.freebus.fts.jobs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.core.I18n;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.IndividualAddressRead;
import org.freebus.knxcomm.application.IndividualAddressWrite;
import org.freebus.knxcomm.application.Restart;
import org.freebus.knxcomm.jobs.JobFailedException;
import org.freebus.knxcomm.jobs.SingleDeviceJob;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

/**
 * Set the physical address of the device on the EIB bus that is in programming
 * mode.
 */
public final class SetPhysicalAddressJob extends SingleDeviceJob
{
   private final PhysicalAddress newAddress;
   private final String label;
   final Telegram dataTelegram = new Telegram();
   final List<Telegram> telegrams = new LinkedList<Telegram>();
   private ApplicationType applicationExpected;
   private final Semaphore semaphore = new Semaphore(0);

   public SetPhysicalAddressJob(PhysicalAddress newAddress)
   {
      super(GroupAddress.BROADCAST);
      this.newAddress = newAddress;

      dataTelegram.setFrom(PhysicalAddress.NULL);
      dataTelegram.setPriority(Priority.SYSTEM);
      dataTelegram.setTransport(Transport.Individual);

      label = I18n.formatMessage("SetPhysicalAddressJob.Label", new Object[] { newAddress.toString() });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getLabel()
   {
      return label;
   }

   /**
    * {@inheritDoc}
    *
    * @throws IOException
    * @throws InterruptedException
    */
   @Override
   public void main(BusInterface bus) throws IOException, InterruptedException
   {
      //
      // Step 1: scan the bus for devices that are in programming mode
      //
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob.Scanning"));
      dataTelegram.setDest(GroupAddress.BROADCAST);
      dataTelegram.setApplication(new IndividualAddressRead());
      telegrams.clear();
      applicationExpected = ApplicationType.IndividualAddress_Response;
      bus.send(dataTelegram);

      // Wait 3 seconds for devices that answer our telegram
      for (int i = 1; i < 30 && telegrams.size() < 2; ++i)
      {
         msleep(100);
         notifyListener(i, null);
      }

      // Verify that exactly one device answered
      applicationExpected = null;
      msleep(100);
      final int num = telegrams.size();
      if (num < 1) throw new JobFailedException(I18n.getMessage("SetPhysicalAddressJob.NoDevice"));
      if (num > 1) throw new JobFailedException(I18n.getMessage("SetPhysicalAddressJob.MultipleDevices"));

      //
      // Step 2: set the physical address
      //
      notifyListener(60, I18n.getMessage("SetPhysicalAddressJob.Programming"));
      dataTelegram.setApplication(new IndividualAddressWrite(newAddress));
      applicationExpected = null;
      telegrams.clear();
      bus.send(dataTelegram);
      msleep(500);

      //
      // Step 3: verify the programmed address
      //
//      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob.Verify"));
//      dataTelegram.setApplication(ApplicationType.Memory_Read);
//      dataTelegram.setDest(newAddress);
//      dataTelegram.setData(new int[] { 1, 0, 0 });
//      applicationExpected = null;
//      semaphore.drainPermits();
//      telegrams.clear();
//      bus.send(dataTelegram);
//      waitForAnswer(ApplicationType.Memory_Response);

      //
      // Step 4: restart the device to clear the programming mode
      //
      notifyListener(80, I18n.getMessage("SetPhysicalAddressJob.Restart"));
      dataTelegram.setApplication(new Restart());
      dataTelegram.setTransport(Transport.Connect);
      dataTelegram.setSequence(1);
      dataTelegram.setDest(newAddress);
      bus.send(dataTelegram);
      msleep(500);

      dataTelegram.setApplication(new Restart());
      dataTelegram.setTransport(Transport.Connected);
      // TODO: is the data byte required?
//      dataTelegram.setData(new int[] { 1 });
      dataTelegram.setSequence(2);
      bus.send(dataTelegram);
      msleep(500);
   }

   /**
    * Wait for a telegram of the given type to arrive. Throws an {@link IOException} if
    * the telegram does not arrive within the timeout (3 seconds).
    *
    * @param appExpected - the type of the telegram application that is waited for.
    * @throws IOException - if the answer telegram does not arrive within the timeout.
    */
   public void waitForAnswer(ApplicationType appExpected) throws IOException
   {
      applicationExpected = appExpected;

      try
      {
         if (!semaphore.tryAcquire(3000, TimeUnit.MILLISECONDS))
            throw new IOException(I18n.getMessage("SetPhysicalAddressJob.ErrTimeout"));
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Sleep some milliseconds.
    */
   protected void msleep(int milliseconds)
   {
      try
      {
         Thread.sleep(milliseconds);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      if (applicationExpected != null && telegram.getApplicationType() == applicationExpected)
      {
         telegrams.add(telegram);
         Logger.getLogger(getClass()).debug("Received answer: " + telegram);
         semaphore.release();
      }
   }
}
