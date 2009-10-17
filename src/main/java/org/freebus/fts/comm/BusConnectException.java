package org.freebus.fts.comm;

/**
 * The exception indicates that the connection to the EIB bus interface
 * failed for some reason.
 */
public final class BusConnectException extends Exception
{
   private static final long serialVersionUID = -427540522334652158L;

   /**
    * Create a EIB bus-interface connect-failed exception.
    */
   public BusConnectException(String message)
   {
      super(message);
   }

   /**
    * Create a EIB bus-interface connect-failed exception.
    */
   public BusConnectException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
