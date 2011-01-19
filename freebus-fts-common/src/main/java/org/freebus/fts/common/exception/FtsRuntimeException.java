package org.freebus.fts.common.exception;

/**
 * A {@link RuntimeException} of FTS.
 */
public class FtsRuntimeException extends RuntimeException
{
   private static final long serialVersionUID = 7341874561716525815L;

   public FtsRuntimeException()
   {
      super();
   }

   public FtsRuntimeException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public FtsRuntimeException(String message)
   {
      super(message);
   }

   public FtsRuntimeException(Throwable cause)
   {
      super(cause);
   }
}
