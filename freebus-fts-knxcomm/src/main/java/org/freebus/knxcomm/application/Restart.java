package org.freebus.knxcomm.application;


/**
 * Send a restart / reset to a device.
 */
public class Restart extends GenericApplication
{
   /**
    * Create an object with default values.
    */
   public Restart()
   {
      super(ApplicationType.Restart);
   }
}
