package org.freebus.knxcomm;

import java.io.IOException;

/**
 * The exception indicates that the connection to the EIB bus interface
 * failed for some reason.
 */
public final class KNXConnectException extends IOException
{
   private static final long serialVersionUID = -427540522334652158L;

   /**
    * Create a EIB bus-interface connect-failed exception.
    */
   public KNXConnectException(String message)
   {
      super(message);
   }

   /**
    * Create a EIB bus-interface connect-failed exception.
    */
   public KNXConnectException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
