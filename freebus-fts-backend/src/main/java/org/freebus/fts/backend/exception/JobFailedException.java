package org.freebus.fts.backend.exception;


/**
 * A job failed for some reason.
 */
public class JobFailedException extends Exception
{
   private static final long serialVersionUID = 1366726246074409563L;

   public JobFailedException()
   {
   }

   public JobFailedException(String message)
   {
      super(message);
   }

   public JobFailedException(Throwable cause)
   {
      super(cause);
   }

   public JobFailedException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
