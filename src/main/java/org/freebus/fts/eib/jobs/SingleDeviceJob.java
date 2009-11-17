package org.freebus.fts.eib.jobs;


import java.io.IOException;

import org.freebus.fts.comm.KNXConnection;
import org.freebus.fts.comm.EmiFrameListener;
import org.freebus.fts.eib.Address;
import org.freebus.fts.emi.EmiMessage;

/**
 * Abstract base class for an EIB job that works with a single device.
 */
public abstract class SingleDeviceJob extends ListenableJob implements Job, EmiFrameListener
{
   protected KNXConnection knxConnection;
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
   public final void run(KNXConnection knxConnection) throws IOException
   {
      this.knxConnection = knxConnection;
      knxConnection.addListener(this);

      init();
      main(knxConnection);
      cleanup();

      knxConnection.removeListener(this);
      this.knxConnection = null;
   }

   /**
    * Do the work. Called by {@link #run}.
    * Must be overridden in subclasses.
    * @throws IOException 
    */
   public abstract void main(KNXConnection knxConnection) throws IOException;

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
   public void messageReceived(EmiMessage message)
   {
   }

   /**
    * {@inheritDoc}
    */
   public void messageSent(EmiMessage message)
   {
   }
}
