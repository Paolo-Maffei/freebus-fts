package org.freebus.fts.backend.devicecontroller;

import org.freebus.knxcomm.BusInterface;

/**
 * Interface for device programmers that program something in a physical KNX
 * device.
 */
public interface DeviceProgrammer
{
   /**
    * Program the device, using the bus interface <code>iface</code>.
    * 
    * @param iface - the bus interface to use.
    */
   void program(BusInterface iface);
}
