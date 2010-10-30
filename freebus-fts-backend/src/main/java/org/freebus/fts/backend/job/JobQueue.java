package org.freebus.fts.backend.job;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.freebus.fts.backend.exception.JobFailedException;
import org.freebus.fts.backend.internal.I18n;
import org.freebus.fts.backend.job.event.JobQueueErrorEvent;
import org.freebus.fts.backend.job.event.JobQueueEvent;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.types.LinkMode;

/**
 * The global job-queue executes the enqueued {@link Job}s one after another.
 */
public class JobQueue implements JobListener
{
   private static JobQueue defaultJobQueue;
   private final Queue<Job> jobs = new ConcurrentLinkedQueue<Job>();
   private final CopyOnWriteArrayList<JobQueueListener> listeners = new CopyOnWriteArrayList<JobQueueListener>();
   private final Semaphore semaphore = new Semaphore(0);
   private boolean running = true;
   private boolean active;
   private Job currentJob;
   private Thread thread;

   /**
    * @return the default job queue.
    */
   public synchronized static JobQueue getDefaultJobQueue()
   {
      if (defaultJobQueue == null)
         defaultJobQueue = new JobQueue();
      return defaultJobQueue;
   }

   /**
    * Create a job-queue object. Use {@link JobQueue#getDefaultJobQueue} to
    * access the default job-queue.
    */
   public JobQueue()
   {
      thread = new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            while (running)
            {
               try
               {
                  semaphore.acquire();
               }
               catch (InterruptedException e)
               {
                  Logger.getLogger(getClass()).warn(e);
               }

               if (!active)
                  active();

               Job job = jobs.poll();
               runJob(job);

               if (jobs.isEmpty())
                  idle();
            }
         }
      });
      thread.setName("Job-Queue");
      thread.start();
   }

   /**
    * Stop the execution thread and free all allocated resources.
    */
   public void dispose()
   {
      running = false;

      if (thread != null)
      {
         thread.interrupt();
         thread = null;
      }

      if (this == defaultJobQueue)
         defaultJobQueue = null;
   }

   /**
    * Add a job to the end of the queue.
    * 
    * @param job - the job to add.
    */
   public void add(Job job)
   {
      if (thread == null)
         throw new IllegalAccessError("the job-queue object is disposed");
      jobs.add(job);
      semaphore.release();
   }

   /**
    * Cancel a job. Removes the job from the job queue. The job is
    * {@link Job#cancel() canceled}. If canceling a running job
    * succeeds depends on the specific job implementation.
    * 
    * @param job - the job to cancel.
    */
   public void cancel(Job job)
   {
      jobs.remove(job);
      job.cancel();
   }

   /**
    * @return true if the job-queue is empty.
    */
   public boolean isEmpty()
   {
      return jobs.isEmpty();
   }

   /**
    * Add a listener.
    */
   public void addListener(JobQueueListener listener)
   {
      if (thread == null)
         throw new IllegalAccessError("the job-queue object is disposed");
      listeners.add(listener);
   }

   /**
    * Remove a listener.
    */
   public void removeListener(JobQueueListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * Inform the listeners about an event.
    */
   public void notifyListeners(JobQueueEvent event)
   {
      for (JobQueueListener listener : listeners)
         listener.jobQueueEvent(event);
   }

   /**
    * Inform the listeners about an event.
    */
   public void notifyListeners(JobQueueErrorEvent event)
   {
      for (JobQueueListener listener : listeners)
         listener.jobQueueErrorEvent(event);
   }

   /**
    * Execute the given job.
    * 
    * @throws JobFailedException
    */
   protected void runJob(Job job)
   {
      final JobQueueEvent masterEvent = new JobQueueEvent(job, 0, "");
      notifyListeners(masterEvent);

      currentJob = job;
      job.addListener(this);

      try
      {
         final BusInterface bus = BusInterfaceFactory.getBusInterface();

         if (bus != null)
            job.run(bus);

         masterEvent.progress = 100;
         notifyListeners(masterEvent);
      }
      catch (Exception e)
      {
         notifyListeners(new JobQueueErrorEvent(job, I18n.getMessage("JobQueue.JobFailed"), e));

      }
      finally
      {
         job.removeListener(this);
         currentJob = null;
      }
   }

   /**
    * Called when the job queue becomes active.
    */
   private void active()
   {
      if (active)
         return;

      active = true;
      setLinkMode(LinkMode.LinkLayer);
      Logger.getLogger(getClass()).debug("job queue active");
   }

   /**
    * Called when the job queue becomes idle.
    */
   private void idle()
   {
      if (!active)
         return;

      try
      {
         Thread.sleep(500);
      }
      catch (InterruptedException e)
      {
      }

      if (!active)
         return;

      active = false;
      notifyListeners(new JobQueueEvent(null));
      setLinkMode(LinkMode.BusMonitor);
      Logger.getLogger(getClass()).debug("job queue idle");
   }

   /**
    * Switch the bus interface to a specific link mode. Does nothing if the bus
    * interface is undefined.
    * 
    * @param mode - the bus link mode to activate.
    */
   private void setLinkMode(LinkMode mode)
   {
      final BusInterface bus = BusInterfaceFactory.getBusInterface();
      if (bus != null)
      {
         try
         {
            bus.setLinkMode(mode);
         }
         catch (IOException e)
         {
            Logger.getLogger(getClass()).error(e);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void progress(int done, String message)
   {
      if (currentJob != null)
         notifyListeners(new JobQueueEvent(currentJob, done, message));
   }
}
