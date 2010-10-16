package org.freebus.fts.backend.job;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.freebus.fts.backend.job.entity.DeviceInfo;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.internal.I18n;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramReceiver;
import org.freebus.knxcomm.telegram.Transport;

/**
 * Scan for devices on a KNX/EIB network line.
 * 
 * Use a {@link DeviceScannerJobListener} listener to get informed about the
 * progress of this job.
 */
public final class DeviceScannerJob extends ListenableJob
{
   final Map<PhysicalAddress, DeviceInfo> deviceDescriptors = new TreeMap<PhysicalAddress, DeviceInfo>();
   final Telegram dataTelegram = new Telegram();
   private TelegramReceiver receiver;
   private final int zone, line;
   private int minAddress = 0;
   private int maxAddress = 255;

   /**
    * Create a device scanner job that will scan for devices in a specific zone
    * and line.
    * 
    * @param zone - the zone address (0..15) to scan
    * @param line - the line address (0..15) to scan
    */
   public DeviceScannerJob(int zone, int line)
   {
      this.zone = zone;
      this.line = line;

      dataTelegram.setFrom(PhysicalAddress.NULL);
      dataTelegram.setPriority(Priority.SYSTEM);
      dataTelegram.setTransport(Transport.Connect);
   }

   /**
    * @return The minimum address that is scanned.
    */
   public int getMinAddress()
   {
      return minAddress;
   }

   /**
    * Set the minimum address that is scanned.
    * 
    * @param minAddress - the minimum address to set (0..255)
    * @throws IllegalArgumentException if the address is out of range.
    */
   public void setMinAddress(int minAddress)
   {
      if (minAddress < 0 || minAddress > 255)
         throw new IllegalArgumentException("address out of range 0..255");

      this.minAddress = minAddress;
   }

   /**
    * @return The maximum address that is scanned.
    */
   public int getMaxAddress()
   {
      return maxAddress;
   }

   /**
    * Set the maximum address that is scanned.
    * 
    * @param maxAddress - the maximum address to set (0..255)
    * @throws IllegalArgumentException if the address is out of range.
    */
   public void setMaxAddress(int maxAddress)
   {
      if (maxAddress < 0 || maxAddress > 255)
         throw new IllegalArgumentException("address out of range 0..255");

      this.maxAddress = maxAddress;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getLabel()
   {
      return I18n.formatMessage("DeviceScannerJob.Label", new Object[] { zone + "." + line });
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
      receiver.setDest(null);

      //
      // Step 1: scan the bus for devices
      //
      notifyListener(1, I18n.getMessage("DeviceScannerJob.Scanning"));
      deviceDescriptors.clear();

      // receiver.setApplicationType(ApplicationType.DeviceDescriptor_Response);
      // receiver.setDest(bus.getPhysicalAddress());
      receiver.clear();

      final int addressRange = maxAddress - minAddress + 1;
      for (int address = minAddress; address <= maxAddress; ++address)
      {
         Logger.getLogger(getClass()).info("Probing " + zone + "." + line + "." + address);

         dataTelegram.setDest(new PhysicalAddress(zone, line, address));

         dataTelegram.setTransport(Transport.Connect);
         bus.send(dataTelegram);
         msleep(50);

         // Freebus LPC controllers currently (2010-03) need an extra telegram
         // to be detected.
         // But this is ok as we want to read the device descriptor anyways. The
         // process could be optimated to only read the device descriptors from
         // found devices - as soon as the Freebus controller sends a Disconnect
         // after a timeout (which the LPC controller does not yet).
         dataTelegram.setTransport(Transport.Connected);
         dataTelegram.setSequence(0);
         dataTelegram.setApplication(new DeviceDescriptorRead(0));
         bus.send(dataTelegram);
         msleep(50);

         final int donePerc = (address - minAddress) * 80 / addressRange;
         notifyListener(donePerc, I18n.getMessage("DeviceScannerJob.Scanning"));
         processAnswers(100);
      }

      // Wait 6+ seconds for answers at the end.
      for (int wait = 0; wait < 120; wait += 5)
      {
         notifyListener(85 + wait / 10, I18n.getMessage("DeviceScannerJob.WaitAnswers"));
         if (!processAnswers(500) && wait > 60)
            break;
      }
   }

   /**
    * Get a device info for a physical address. If no device info exists, one is
    * created.
    * 
    * @param address - the physical address to search for.
    * @return The device info.
    */
   private DeviceInfo getDeviceInfo(PhysicalAddress address)
   {
      DeviceInfo info = deviceDescriptors.get(address);
      if (info == null)
      {
         info = new DeviceInfo(address);
         deviceDescriptors.put(address, info);
      }

      return info;
   }

   /**
    * Receive answer telegrams
    * 
    * @param waitTime - how long to wait for telegrams, in milliseconds
    * 
    * @return true if answers were processed, false if not
    */
   public boolean processAnswers(int waitTime)
   {
      boolean gotAnswers = false;

      for (final Telegram telegram : receiver.receiveMultiple(waitTime))
      {
         if (telegram.getTransport() == Transport.Disconnect)
         {
            gotAnswers = true;

            getDeviceInfo(telegram.getFrom());
         }
         else if (telegram.getApplicationType() == ApplicationType.DeviceDescriptor_Response)
         {
            gotAnswers = true;

            Logger.getLogger(getClass()).info("Found device: " + telegram.getFrom());

            final DeviceDescriptorResponse ddApp = (DeviceDescriptorResponse) telegram.getApplication();

            final DeviceInfo info = getDeviceInfo(telegram.getFrom());
            info.setDescriptor(ddApp.getDescriptor());

            fireDeviceInfo(info);
         }
      }

      return gotAnswers;
   }

   /**
    * Inform all listeners about new device informations.
    * 
    * @param info - the device information.
    */
   protected void fireDeviceInfo(DeviceInfo info)
   {
      for (JobListener listener : getListeners())
      {
         if (listener instanceof DeviceScannerJobListener)
            ((DeviceScannerJobListener) listener).deviceInfo(info);
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
}
