package org.freebus.knxcomm.jobs;


/**
 * An event that gets sent by the {@link JobQueue} when something about
 * a job happens.
 */
public final class JobQueueEvent
{
   /**
    * The job that is currently processed.
    */
   public Job job;

   /**
    * How far the job is completed, in percent.
    */
   public int progress;

   /**
    * An optional message about the current step. May be null.
    */
   public String message;

   /**
    * Create an event object.
    */
   public JobQueueEvent(Job job, int progress, String message)
   {
      this.job = job;
      this.progress = progress;
      this.message = message;
   }

   /**
    * Create an event object without a message.
    */
   public JobQueueEvent(Job job, int progress)
   {
      this(job, progress, null);
   }

   /**
    * Create an event object without a message and progress 0%
    */
   public JobQueueEvent(Job job)
   {
      this(job, 0, null);
   }
}
