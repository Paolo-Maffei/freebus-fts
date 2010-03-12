package org.freebus.fts.jobs;

import java.io.IOException;

import org.freebus.fts.common.address.Address;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * Abstract base class for EIB jobs that work with a single device.
 */
public abstract class SingleDeviceJob extends ListenableJob implements Job, TelegramListener
{
   protected BusInterface bus;
   private final Address targetAddress;

   /**
    * Create a job object that will work with the device with the given address.
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
    * 
    * @throws IOException
    */
   @Override
   public final void run(BusInterface bus) throws IOException
   {
      this.bus = bus;
      bus.addListener(this);

      init();
      try
      {
         main(bus);
      }
      catch (InterruptedException e)
      {
         throw new IOException(e);
      }
      finally
      {
         cleanup();
      }

      bus.removeListener(this);
      this.bus = null;
   }

   /**
    * Do the work. Called by {@link #run}. Must be overridden in subclasses.
    * 
    * @throws IOException
    */
   public abstract void main(BusInterface bus) throws IOException, InterruptedException;

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
