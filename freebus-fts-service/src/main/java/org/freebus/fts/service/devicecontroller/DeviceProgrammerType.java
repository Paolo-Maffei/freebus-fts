package org.freebus.fts.service.devicecontroller;

/**
 * Types of device programmers.
 */
public enum DeviceProgrammerType
{
   /**
    * For programming the communication objects and -tables of a device.
    */
   COMMUNICATIONS,

   /**
    * For programming the parameters of a device.
    */
   PARAMETERS,

   /**
    * For programming the physical address of a device.
    */
   PHYSICAL_ADDRESS,

   /**
    * For programming the application program of a device.
    */
   PROGRAM;
}
