package org.freebus.fts.jobs;


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
   public void jobQueueEvent(JobQueueEvent event);
}
