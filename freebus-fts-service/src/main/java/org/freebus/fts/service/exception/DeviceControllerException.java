package org.freebus.fts.service.exception;

import org.freebus.fts.service.devicecontroller.DeviceController;

/**
 * An exception that occurs in {@link DeviceController}s.
 */
public class DeviceControllerException extends Exception
{
   private static final long serialVersionUID = 2244163751680127142L;

   public DeviceControllerException()
   {
   }

   public DeviceControllerException(String message)
   {
      super(message);
   }

   public DeviceControllerException(Throwable cause)
   {
      super(cause);
   }

   public DeviceControllerException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
