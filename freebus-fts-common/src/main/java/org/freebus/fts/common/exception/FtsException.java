package org.freebus.fts.common.exception;

/**
 * A generic FTS exception.
 */
public class FtsException extends Exception
{
   private static final long serialVersionUID = 2499564968009057010L;

   public FtsException()
   {
   }

   public FtsException(String message)
   {
      super(message);
   }

   public FtsException(Throwable cause)
   {
      super(cause);
   }

   public FtsException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
