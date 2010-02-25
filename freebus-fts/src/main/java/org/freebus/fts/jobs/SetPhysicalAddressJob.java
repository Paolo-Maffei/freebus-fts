package org.freebus.fts.jobs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.freebus.fts.core.I18n;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.telegram.Application;
import org.freebus.knxcomm.telegram.GroupAddress;
import org.freebus.knxcomm.telegram.PhysicalAddress;
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
   private Application applicationExpected;
   private final Semaphore semaphore = new Semaphore(0);

   public SetPhysicalAddressJob(PhysicalAddress newAddress)
   {
      super(GroupAddress.BROADCAST);
      this.newAddress = newAddress;

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
      dataTelegram.setFrom(PhysicalAddress.NULL);
      dataTelegram.setPriority(Priority.SYSTEM);

      //
      // Step 1: scan the bus for devices that are in programming mode
      //
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob.Scanning"));
      dataTelegram.setApplication(Application.IndividualAddress_Read);
      dataTelegram.setDest(GroupAddress.BROADCAST);
      dataTelegram.setData(new int[] { (int)(Math.random() * 256) & 0xff });
      telegrams.clear();
      applicationExpected = Application.IndividualAddress_Response;
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
      dataTelegram.setApplication(Application.IndividualAddress_Write);
      dataTelegram.setData(newAddress.getBytes());
      applicationExpected = null;
      telegrams.clear();
      bus.send(dataTelegram);
      msleep(3000);

      //
      // Step 3: verify the programmed address
      //
//      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob.Verify"));
//      dataTelegram.setApplication(Application.Memory_Read);
//      dataTelegram.setDest(newAddress);
//      dataTelegram.setData(new int[] { 1, 0, 0 });
//      applicationExpected = null;
//      semaphore.drainPermits();
//      telegrams.clear();
//      bus.send(dataTelegram);
//      waitForAnswer(Application.Memory_Response);

      //
      // Step 4: restart the device to clear the programming mode
      //
      notifyListener(80, I18n.getMessage("SetPhysicalAddressJob.Restart"));
      dataTelegram.setApplication(Application.Restart);
      dataTelegram.setDest(newAddress);
      dataTelegram.setData(null);
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
   public void waitForAnswer(Application appExpected) throws IOException
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
      if (applicationExpected != null && telegram.getApplication() == applicationExpected)
      {
         telegrams.add(telegram);
         Logger.getLogger(getClass()).debug("received answer: " + telegram);
         semaphore.release();
      }
   }
}
