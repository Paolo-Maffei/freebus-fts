package org.freebus.fts.eib.jobs;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.comm.BusInterfaceFactory;
import org.freebus.fts.utils.TaskListener;

/**
 * The global job-queue executes the enqueued {@link Job}s one after another.
 */
public final class JobQueue implements TaskListener
{
   private static final JobQueue defaultJobQueue = new JobQueue();

   private final Queue<Job> jobs = new ConcurrentLinkedQueue<Job>();
   private final CopyOnWriteArrayList<JobQueueListener> listeners = new CopyOnWriteArrayList<JobQueueListener>();
   private final Semaphore semaphore = new Semaphore(0);
   private boolean running = true;
   private Job currentJob;
   private Thread thread;

   /**
    * @return the default job queue.
    */
   public static JobQueue getDefaultJobQueue()
   {
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
                  Job job = jobs.poll();
                  runJob(job);
               }
               catch (InterruptedException e)
               {
                  if (running) e.printStackTrace();
               }
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
      thread.interrupt();
      thread = null;
   }

   /**
    * Add a job to the end of the queue.
    */
   public void add(Job job)
   {
      if (thread == null) throw new IllegalAccessError("the job-queue object is disposed");
      jobs.add(job);
      semaphore.release();
   }

   /**
    * Add a listener.
    */
   public void addListener(JobQueueListener listener)
   {
      if (thread == null) throw new IllegalAccessError("the job-queue object is disposed");
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
         listener.jobEvent(event);
   }

   /**
    * Execute the given job.
    */
   protected void runJob(Job job)
   {
      final JobQueueEvent masterEvent = new JobQueueEvent(job, 0, "");
      notifyListeners(masterEvent);

      currentJob = job;
      job.addListener(this);

      try
      {
         final BusInterface busInterface = BusInterfaceFactory.getDefaultInstance();
         if (!busInterface.isOpen()) busInterface.open();

         job.run(busInterface);
         
         masterEvent.progress = 100;
         notifyListeners(masterEvent);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      job.removeListener(this);
      currentJob = null;
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
