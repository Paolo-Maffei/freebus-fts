package org.freebus.fts.jobs;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.core.I18n;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.IndividualAddressRead;
import org.freebus.knxcomm.application.IndividualAddressWrite;
import org.freebus.knxcomm.application.Restart;
import org.freebus.knxcomm.jobs.JobFailedException;
import org.freebus.knxcomm.jobs.ListenableJob;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramReceiver;
import org.freebus.knxcomm.telegram.Transport;

/**
 * Set the physical address of the device on the EIB bus that is in programming
 * mode.
 */
public final class SetPhysicalAddressJob extends ListenableJob
{
   private TelegramReceiver receiver;
   private final PhysicalAddress newAddress;
   private final String label;
   final Telegram dataTelegram = new Telegram();

   public SetPhysicalAddressJob(PhysicalAddress newAddress)
   {
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
    */
   @Override
   public void main(BusInterface bus) throws IOException
   {
      receiver = new TelegramReceiver(bus);

      //
      // Step 0: test if the address is already in use
      //

      // TODO

      //
      // Step 1: scan the bus for devices that are in programming mode
      //
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob.Scanning"));

      dataTelegram.setDest(GroupAddress.BROADCAST);
      dataTelegram.setApplication(new IndividualAddressRead());

      receiver.setApplicationType(ApplicationType.IndividualAddress_Response);
      receiver.setDest(GroupAddress.BROADCAST);
      receiver.clear();

      bus.send(dataTelegram);

      // Wait 3 seconds for devices that answer our telegram
      Set<PhysicalAddress> found = new HashSet<PhysicalAddress>();
      for (int i = 1; i <= 6 && found.size() < 2; ++i)
      {
         for (Telegram telegram: receiver.receiveMultiple(500))
            found.add(telegram.getFrom());
         notifyListener(i * 10, null);
      }

      // Verify that exactly one device answered
      if (found.size() < 1)
         throw new JobFailedException(I18n.getMessage("SetPhysicalAddressJob.NoDevice"));
      if (found.size() > 1)
         throw new JobFailedException(I18n.getMessage("SetPhysicalAddressJob.MultipleDevices"));

      //
      // Step 2: set the physical address
      //
      notifyListener(60, I18n.getMessage("SetPhysicalAddressJob.Programming"));

      dataTelegram.setApplication(new IndividualAddressWrite(newAddress));

      receiver.setApplicationType(null);
      receiver.setConfirmations(true);
      receiver.clear();

      bus.send(dataTelegram);
      receiver.receive(500);

      //
      // Step 3: restart the device to clear the programming mode
      //
      notifyListener(80, I18n.getMessage("SetPhysicalAddressJob.Restart"));

      dataTelegram.setApplication(new Restart());
      bus.send(dataTelegram);
      receiver.receive(500);

      final DataConnection con = bus.connect(newAddress);
      try
      {
         con.send(new Restart());
         con.receive(500);
      }
      finally
      {
         con.close();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void cleanup(BusInterface bus)
   {
      if (receiver != null)
         receiver.close();
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
}
