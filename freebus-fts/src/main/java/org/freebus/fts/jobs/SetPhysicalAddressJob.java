package org.freebus.fts.jobs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.freebus.fts.core.I18n;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.telegram.Application;
import org.freebus.knxcomm.telegram.GroupAddress;
import org.freebus.knxcomm.telegram.PhysicalAddress;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;

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

   public SetPhysicalAddressJob(PhysicalAddress newAddress)
   {
      super(GroupAddress.BROADCAST);
      this.newAddress = newAddress;

      dataTelegram.setFrom(PhysicalAddress.NULL);
      dataTelegram.setDest(GroupAddress.BROADCAST);
      dataTelegram.setPriority(Priority.SYSTEM);

      label = I18n.getMessage("SetPhysicalAddressJob_Label").replace("%1", newAddress.toString());
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
      //
      // Step 1: scan the bus for devices that are in programming mode
      //
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob_Scanning"));
      dataTelegram.setApplication(Application.IndividualAddress_Read);
      dataTelegram.setData(new int[] { 0x01 });
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
      if (num < 1) throw new JobFailedException(I18n.getMessage("SetPhysicalAddressJob_NoDevice"));
      if (num > 1) throw new JobFailedException(I18n.getMessage("SetPhysicalAddressJob_MultipleDevices"));

      //
      // Step 2: set the physical address
      //
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob_Programming"));
      dataTelegram.setApplication(Application.IndividualAddress_Write);
      dataTelegram.setData(newAddress.getBytes());
      bus.send(dataTelegram);
      msleep(500);

      //
      // Step 3: verify the programmed address
      //
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob_Verify"));
      dataTelegram.setApplication(Application.Memory_Read);
      dataTelegram.setDest(newAddress);
      telegrams.clear();
      applicationExpected = Application.Memory_Response;
      bus.send(dataTelegram);
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
         telegrams.add(telegram);
   }
}