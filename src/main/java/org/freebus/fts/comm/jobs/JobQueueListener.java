package org.freebus.fts.comm.jobs;


/**
 * Interface for listeners that get informed upon events of the {@link JobQueue}
 * job-queue.
 */
public interface JobQueueListener
{
   /**
    * A job-queue event happened.
    * 
    * @param event - details about the event.
    */
   public void jobEvent(JobQueueEvent event);
}
