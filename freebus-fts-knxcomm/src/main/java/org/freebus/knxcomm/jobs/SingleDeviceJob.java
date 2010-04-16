package org.freebus.knxcomm.jobs;

import org.freebus.fts.common.address.Address;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * Abstract base class for EIB jobs that work with a single device.
 */
public abstract class SingleDeviceJob extends ListenableJob implements Job, TelegramListener
{
   private final Address targetAddress;

   /**
    * Create a job object that will work with the device with the given address.
    */
   protected SingleDeviceJob(Address targetAddress)
   {
      this.targetAddress = targetAddress;
   }

   /**
    * @return the address of the target device.
    */
   public Address getTargetAddress()
   {
      return targetAddress;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void init(BusInterface bus)
   {
      bus.addListener(this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void cleanup(BusInterface bus)
   {
      bus.removeListener(this);
   }

   /**
    * {@inheritDoc}
    */
   public void telegramReceived(Telegram telegram)
   {
   }

   /**
    * {@inheritDoc}
    */
   public void telegramSent(Telegram telegram)
   {
   }
}
