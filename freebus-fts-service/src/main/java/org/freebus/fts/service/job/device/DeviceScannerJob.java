package org.freebus.fts.service.job.device;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.service.internal.I18n;
import org.freebus.fts.service.job.JobListener;
import org.freebus.fts.service.job.ListenableJob;
import org.freebus.fts.service.job.entity.DeviceInfo;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.MemoryRead;
import org.freebus.knxcomm.application.MemoryResponse;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramReceiver;
import org.freebus.knxcomm.telegram.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scan for devices on a KNX/EIB network line.
 *
 * Use a {@link DeviceScannerJobListener} listener to get informed about the
 * progress of this job.
 */
public final class DeviceScannerJob extends ListenableJob
{
   private final static Logger LOGGER = LoggerFactory.getLogger(DeviceScannerJob.class);

   final Map<PhysicalAddress, DeviceInfo> deviceInfos = new TreeMap<PhysicalAddress, DeviceInfo>();
   final Telegram dataTelegram = new Telegram();
   final Telegram memReadTelegram = new Telegram();
   final Telegram ackTelegram = new Telegram();
   private BusInterface bus;
   private TelegramReceiver receiver;
   private final int area, line;
   private int minAddress = 0;
   private int maxAddress = 255;

   /**
    * Create a device scanner job that will scan for devices in a specific area
    * and line.
    *
    * @param area - the area address (0..15) to scan
    * @param line - the line address (0..15) to scan
    */
   public DeviceScannerJob(int area, int line)
   {
      this.area = area;
      this.line = line;

      dataTelegram.setFrom(PhysicalAddress.NULL);
      dataTelegram.setPriority(Priority.SYSTEM);
      dataTelegram.setTransport(Transport.Connect);

      memReadTelegram.setFrom(PhysicalAddress.NULL);
      memReadTelegram.setPriority(Priority.SYSTEM);
      memReadTelegram.setTransport(Transport.Connected);
      memReadTelegram.setApplication(new MemoryRead(0x104, 4));
      memReadTelegram.setSequence(1);

      ackTelegram.setFrom(PhysicalAddress.NULL);
      ackTelegram.setPriority(Priority.SYSTEM);
      ackTelegram.setTransport(Transport.ConnectedAck);
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
      return I18n.formatMessage("DeviceScannerJob.Label", area + "." + line);
   }

   /**
    * {@inheritDoc}
    *
    * @throws IOException
    */
   @Override
   public void main(BusInterface bus) throws IOException
   {
      this.bus = bus;

      receiver = new TelegramReceiver(bus);
      receiver.setDest(null);

      //
      // Step 1: scan the bus for devices
      //
      notifyListener(1, I18n.getMessage("DeviceScannerJob.Scanning"));
      deviceInfos.clear();

      // receiver.setApplicationType(ApplicationType.DeviceDescriptor_Response);
      // receiver.setDest(bus.getPhysicalAddress());
      receiver.clear();

      final int addressRange = maxAddress - minAddress + 1;
      for (int address = minAddress; address <= maxAddress && isActive(); ++address)
      {
         LOGGER.info("Probing " + area + "." + line + "." + address);

         dataTelegram.setDest(new PhysicalAddress(area, line, address));

         dataTelegram.setTransport(Transport.Connect);
         bus.send(dataTelegram);
         msleep(100); // wait a little between connect and the first request
                      // telegram

         // Freebus LPC controllers currently (2010/03) need an extra telegram
         // to be detected. We want to read the device descriptor anyways, so it
         // does not hurt that much. The process could be optimated to only read
         // the device descriptors from found devices - as soon as the Freebus
         // controller sends a Disconnect after a timeout (which the LPC
         // controller does not yet).
         dataTelegram.setTransport(Transport.Connected);
         dataTelegram.setSequence(0);
         dataTelegram.setApplication(new DeviceDescriptorRead(0));
         bus.send(dataTelegram);

         final int donePerc = (address - minAddress) * 80 / addressRange;
         notifyListener(donePerc, I18n.getMessage("DeviceScannerJob.Scanning"));
         processAnswers(150);
      }

      // Wait some seconds for answers at the end. Default: 6
      final int waitSec = 3;
      for (int wait = 0; wait < waitSec * 20 && isActive(); wait += 5)
      {
         notifyListener(85 + wait / 10, I18n.getMessage("DeviceScannerJob.WaitAnswers"));
         if (!processAnswers(500) && wait > 60)
            break;
      }
   }

   /**
    * Get all device infos that were gathered up to now.
    *
    * @return A collection with all device infos.
    */
   public Collection<DeviceInfo> getDeviceInfos()
   {
      return deviceInfos.values();
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
      DeviceInfo info = deviceInfos.get(address);
      if (info == null)
      {
         LOGGER.info("Found device: " + address);

         info = new DeviceInfo(address);
         deviceInfos.put(address, info);
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

            final DeviceDescriptorResponse ddApp = (DeviceDescriptorResponse) telegram.getApplication();

            final DeviceInfo info = getDeviceInfo(telegram.getFrom());
            final boolean newlyFound = info.getDescriptor() == null;

            info.setDescriptor(ddApp.getDescriptor());
            fireDeviceInfo(info);

            synchronized (ackTelegram)
            {
               ackTelegram.setSequence(telegram.getSequence());
               ackTelegram.setDest(telegram.getFrom());
               try
               {
                  bus.send(ackTelegram);
               }
               catch (IOException e)
               {
                  LOGGER.warn("failed to send ACK telegram", e);
               }
               msleep(50);
            }

            if (newlyFound)
            {
               synchronized (memReadTelegram)
               {
                  memReadTelegram.setDest(telegram.getFrom());
                  try
                  {
                     bus.send(memReadTelegram);
                  }
                  catch (IOException e)
                  {
                     LOGGER.warn("failed to send mem-read telegram", e);
                  }
               }
               msleep(50);
            }
         }
         else if (telegram.getApplicationType() == ApplicationType.Memory_Response)
         {
            gotAnswers = true;
            // LOGGER.info("+++ Device memory from " + telegram.getFrom() + ": "
            // + telegram.getApplication());

            final byte[] mem = ((MemoryResponse) telegram.getApplication()).getData();

            final DeviceInfo info = getDeviceInfo(telegram.getFrom());
            info.setManufacturerId(mem[0] & 255);
            info.setDeviceType(((mem[1] & 255) << 8) | (mem[2] & 255));

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
