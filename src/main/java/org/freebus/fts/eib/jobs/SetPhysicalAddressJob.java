package org.freebus.fts.eib.jobs;

import java.io.IOException;

import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.eib.Application;
import org.freebus.fts.eib.GroupAddress;
import org.freebus.fts.eib.PhysicalAddress;
import org.freebus.fts.eib.Priority;
import org.freebus.fts.eib.Telegram;
import org.freebus.fts.emi.L_Data;
import org.freebus.fts.utils.I18n;

/**
 * Set the physical address of the device on the EIB bus that is in programming
 * mode.
 */
public final class SetPhysicalAddressJob extends SingleDeviceJob
{
   private final PhysicalAddress newAddress;
   private final L_Data.req dataMsg = new L_Data.req();
   final Telegram dataTelegram = dataMsg.getTelegram();

   public SetPhysicalAddressJob(PhysicalAddress newAddress)
   {
      super(GroupAddress.BROADCAST);
      this.newAddress = newAddress;

      dataTelegram.setFrom(PhysicalAddress.NULL);
      dataTelegram.setDest(GroupAddress.BROADCAST);
      dataTelegram.setPriority(Priority.SYSTEM);
   }

   /**
    * {@inheritDoc}
    * @throws IOException 
    */
   @Override
   public void main(BusInterface busInterface) throws IOException
   {
      // Step 1: scan the bus for devices that are in programming mode
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob_Scanning"));
      dataTelegram.setApplication(Application.IndividualAddress_Read);
      busInterface.write(dataMsg);

      for (int i = 1; i < 30; ++i)
      {
         msleep(100);
         notifyListener(i, null);
      }

      // Step 2: set the physical address
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob_Programming"));
      sendSetAddress();
      msleep(250);

      // Step 3: verify the programmed address
      notifyListener(1, I18n.getMessage("SetPhysicalAddressJob_Verify"));
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
    * Send set-address telegram
    * @throws IOException 
    */
   private void sendSetAddress() throws IOException
   {
      final int addr = newAddress.getAddr();

      dataTelegram.setApplication(Application.IndividualAddress_Write);
      dataTelegram.setData(new int[] { (addr >> 8) & 0xff, addr & 0xff });

      busInterface.write(dataMsg);
   }
}
