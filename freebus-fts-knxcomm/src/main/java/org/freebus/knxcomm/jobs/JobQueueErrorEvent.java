package org.freebus.knxcomm.jobs;

/**
 * An event that gets sent by the {@link JobQueue} when an error about
 * a job happens.
 */
public class JobQueueErrorEvent
{
   /**
    * The job that is currently processed.
    */
   public Job job;

   /**
    * The error message of the job
    */
   public String message;

   /**
    * An optional exception, if an exception happened. May be null.
    */
   public Exception exception;

   /**
    * Create a job queue error event. With message about the exception.
    *
    * @param job - the job that the event is about
    * @param message - a message that describes the error
    * @param exception - an exception for further details about the error
    */
   public JobQueueErrorEvent(Job job, String message, Exception exception)
   {
      this.job = job;
      this.message = message;
      this.exception = exception;
   }

   /**
    * Create a job queue error event. With message about the exception.
    *
    * @param job - the job that the event is about
    * @param message - a message that describes the error
    */
   public JobQueueErrorEvent(Job job, String message)
   {
      this.job = job;
      this.message = message;
      this.exception = null;
   }
}
