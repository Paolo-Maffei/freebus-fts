package org.freebus.fts.comm.jobs;


import java.io.IOException;

import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.comm.TelegramListener;
import org.freebus.fts.eib.Address;
import org.freebus.fts.eib.Telegram;

/**
 * Abstract base class for an EIB job that works with a single device.
 */
public abstract class SingleDeviceJob extends ListenableJob implements Job, TelegramListener
{
   protected BusInterface bus;
   private final Address targetAddress;

   /**
    * Create a job object that will work with the device with the given
    * address.
    */
   SingleDeviceJob(Address targetAddress)
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
    * @throws IOException 
    */
   @Override
   public final void run(BusInterface bus) throws IOException
   {
      this.bus = bus;
      bus.addListener(this);

      init();
      main(bus);
      cleanup();

      bus.removeListener(this);
      this.bus = null;
   }

   /**
    * Do the work. Called by {@link #run}.
    * Must be overridden in subclasses.
    * @throws IOException 
    */
   public abstract void main(BusInterface bus) throws IOException;

   /**
    * Initialization. Called by {@link #run}.
    */
   protected void init()
   {
   }

   /**
    * Cleanup. Called by {@link #run}.
    */
   protected void cleanup()
   {
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
