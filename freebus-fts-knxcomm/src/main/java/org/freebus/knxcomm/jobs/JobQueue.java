package org.freebus.knxcomm.jobs;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.internal.I18n;

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
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }

               Job job = jobs.poll();
               runJob(job);

               if (jobs.isEmpty())
                  notifyListeners(new JobQueueEvent(null));

            }
         }
      });

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
    */
   public void add(Job job)
   {
      if (thread == null)
         throw new IllegalAccessError("the job-queue object is disposed");
      jobs.add(job);
      semaphore.release();
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
         final BusInterface busInterface = BusInterfaceFactory.getBusInterface();

         if (busInterface != null)
            job.run(busInterface);

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
    * {@inheritDoc}
    */
   @Override
   public void progress(int done, String message)
   {
      if (currentJob != null)
         notifyListeners(new JobQueueEvent(currentJob, done, message));
   }
}
