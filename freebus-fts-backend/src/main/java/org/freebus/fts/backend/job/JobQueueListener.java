package org.freebus.fts.backend.job;

import org.freebus.fts.backend.job.event.JobQueueErrorEvent;
import org.freebus.fts.backend.job.event.JobQueueEvent;


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
   
   
   /**
    * A job-queue error event happened.
    * 
    * @param event - details about the Error.
    */
   public void jobQueueErrorEvent(JobQueueErrorEvent event);
}
