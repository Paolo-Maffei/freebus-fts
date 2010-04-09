package org.freebus.knxcomm.jobs;

/**
 * Interface for listeners that get informed when a job is processed.
 */
public interface JobListener
{
   /**
    * Called to inform the listener about the progress of a job.
    *
    * @param done - How far the job is done, in percent.
    * @param message - A message about what is currently done.
    */
   public void progress(int done, String message);
}
