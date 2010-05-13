package org.freebus.knxcomm.jobs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.application.ApplicationType;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor0;
import org.freebus.knxcomm.internal.I18n;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramReceiver;
import org.freebus.knxcomm.telegram.Transport;

/**
 * Scan for devices on a KNX/EIB network line.
 */
public final class DeviceScannerJob extends ListenableJob
{
   final Map<PhysicalAddress, DeviceDescriptor0> deviceDescriptors = new HashMap<PhysicalAddress, DeviceDescriptor0>();
   final Telegram dataTelegram = new Telegram();
   private TelegramReceiver receiver;
   private final int zone, line;

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
    * {@inheritDoc}
    */
   @Override
   public String getLabel()
   {
      // TODO Auto-generated method stub
      return null;
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

//      receiver.setApplicationType(ApplicationType.DeviceDescriptor_Response);
      //receiver.setDest(bus.getPhysicalAddress());
      receiver.clear();

      for (int deviceId = 0; deviceId <= 255; ++deviceId)
      {
         Logger.getLogger(getClass()).info("Probing " + zone + "." + line + "." + deviceId);

         dataTelegram.setDest(new PhysicalAddress(zone, line, deviceId));

         dataTelegram.setTransport(Transport.Connect);
         bus.send(dataTelegram);
         msleep(50);

         // Freebus Controller need this to be detected:
         dataTelegram.setTransport(Transport.Connected);
         dataTelegram.setSequence(0);
         dataTelegram.setApplication(new DeviceDescriptorRead(0));
         bus.send(dataTelegram);
         msleep(20);

         notifyListener((deviceId * 80) >> 8, I18n.getMessage("DeviceScannerJob.Scanning"));
         processAnswers(50);
      }

      // Wait 6+ seconds for answers at the end.
      for (int wait = 0; wait < 120; wait += 5)
      {
         notifyListener(85 + wait/10, I18n.getMessage("DeviceScannerJob.WaitAnswers"));
         if (!processAnswers(500) && wait > 60)
            break;
      }
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
         }
         else if (telegram.getApplicationType() == ApplicationType.DeviceDescriptor_Response)
         {
            gotAnswers = true;

            Logger.getLogger(getClass()).info("Found device: " + telegram.getFrom());
            notifyListenerDeviceFound(telegram.getFrom());

            final DeviceDescriptorResponse ddApp = (DeviceDescriptorResponse) telegram.getApplication();
            final int ddType = ddApp.getDescriptorType();

            if (ddType == 0)
            {
               deviceDescriptors.put(telegram.getFrom(), (DeviceDescriptor0) ddApp.getDescriptor());
            }
         }
      }

      return gotAnswers;
   }

   /**
    * Inform all listeners that are {@link DeviceScannerJobListener} about a
    * found device.
    *
    * @param addr - the address of the found device.
    */
   protected void notifyListenerDeviceFound(PhysicalAddress addr)
   {
      for (JobListener listener : getListeners())
      {
         if (listener instanceof DeviceScannerJobListener)
            ((DeviceScannerJobListener) listener).deviceFound(addr);
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
