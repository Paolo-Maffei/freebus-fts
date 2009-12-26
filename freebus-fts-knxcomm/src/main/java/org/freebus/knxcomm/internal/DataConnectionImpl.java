package org.freebus.knxcomm.internal;

import java.io.IOException;

import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.DataConnection;
import org.freebus.knxcomm.telegram.PhysicalAddress;

/**
 * A direct connection to a device on the KNX/EIB bus.
 */
public class DataConnectionImpl implements DataConnection
{
   /**
    * The state of the connection.
    */
   enum State
   {
      /**
       * The connection is closed.
       */
      CLOSED,

      /**
       * The connection is open.
       */
      OPEN_IDLE,

      /**
       * The connection is open, a T_ACK from the remote device is awaited.  
       */
      OPEN_WAIT,

      /**
       * The connection is being established, a connection request was sent,
       * an IACK is awaited.
       */
      CONNECTING;
   }

   private State state = State.CLOSED;
   private final PhysicalAddress addr;
   private final BusInterface busInterface;

   /**
    * Create a connection to the device with the given physical address.
    * Use {@link BusInterface#connect} to get a connection.
    *
    * @param address - the physical address to which the connection will happen.
    */
   public DataConnectionImpl(PhysicalAddress addr, BusInterface busInterface)
   {
      this.addr = addr;
      this.busInterface = busInterface;
   }

   /**
    * {@inheritDoc}
    */
   public void open() throws IOException
   {
      
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @return the physical address of the target device.
    */
   public PhysicalAddress getAddress()
   {
      return addr;
   }

   /**
    * @return the bus-interface.
    */
   public BusInterface getBusInterface()
   {
      return busInterface;
   }

   /**
    * @return the internal state of the connection.
    */
   public State getState()
   {
      return state;
   }
}
