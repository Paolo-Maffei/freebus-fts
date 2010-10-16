package org.freebus.fts.backend.job;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.IndividualAddressRead;
import org.freebus.knxcomm.application.IndividualAddressWrite;
import org.freebus.knxcomm.application.Restart;
import org.freebus.knxcomm.internal.I18n;
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
      DataConnection con;
      receiver = new TelegramReceiver(bus);

      //
      // Step 1: test if the address is already in use, and scan the bus for
      // devices that are in programming mode
      //
      notifyListener(10, I18n.getMessage("SetPhysicalAddressJob.Scanning"));
      final long startScan = System.currentTimeMillis();

      dataTelegram.setDest(GroupAddress.BROADCAST);
      dataTelegram.setApplication(new IndividualAddressRead());

      receiver.setApplicationType(ApplicationType.IndividualAddress_Response);
      receiver.setDest(GroupAddress.BROADCAST);
      receiver.clear();

      bus.send(dataTelegram);

      con = bus.connect(newAddress, Priority.SYSTEM);
      try
      {
         final Application reply = con.query(new DeviceDescriptorRead(0));
         if (reply != null)
         {
            throw new JobFailedException(I18n.formatMessage("SetPhysicalAddressJob.AddressInUse",
                  new Object[] { newAddress }));
         }
      }
      catch (TimeoutException e)
      {
         // This is the normal case - the physical address that we want to
         // program is unused.
         con.dispose();
      }
      finally
      {
         con.close();
      }

      final long elapsed = System.currentTimeMillis() - startScan;

      //
      // Step 2: wait up to 3 seconds for answers
      //
      Set<PhysicalAddress> found = new HashSet<PhysicalAddress>();
      for (int i = 1 + (int) (elapsed / 1000); i <= 6 && found.size() < 2; ++i)
      {
         for (Telegram telegram : receiver.receiveMultiple(500))
            found.add(telegram.getFrom());
         notifyListener(i * 10, null);
      }

      // Verify that exactly one device is in programming mode
      if (found.size() < 1)
         throw new JobFailedException(I18n.getMessage("SetPhysicalAddressJob.NoDevice"));
      if (found.size() > 1)
         throw new JobFailedException(I18n.getMessage("SetPhysicalAddressJob.MultipleDevices"));

      //
      // Step 3: set the physical address
      //
      notifyListener(60, I18n.getMessage("SetPhysicalAddressJob.Programming"));

      dataTelegram.setApplication(new IndividualAddressWrite(newAddress));

      receiver.setApplicationType(null);
      receiver.setConfirmations(true);
      receiver.clear();

      bus.send(dataTelegram);
      receiver.receive(500);

      //
      // Step 4: restart the device to clear the programming mode
      //
      msleep(250);
      notifyListener(80, I18n.getMessage("SetPhysicalAddressJob.Restart"));
      con = bus.connect(newAddress, Priority.SYSTEM);
      try
      {
         con.sendUnconfirmed(new Restart());
         msleep(250);
      }
      finally
      {
         con.dispose();
      }

      // (At least) the Freebus LPC controller seems to require this unconnected
      // restart to reset properly (06/2010)
      dataTelegram.setDest(newAddress);
      bus.send(dataTelegram);
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
}
