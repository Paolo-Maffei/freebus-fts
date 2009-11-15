package org.freebus.fts.eib.jobs;

import java.io.IOException;

/**
 * A job failed for some reason.
 */
public class JobFailedException extends IOException
{
   private static final long serialVersionUID = 141586033318369876L;

   public JobFailedException(String message)
   {
      super(message);
   }

   public JobFailedException()
   {
      super();
   }
}
