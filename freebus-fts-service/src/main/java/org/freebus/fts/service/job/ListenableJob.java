package org.freebus.fts.service.job;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import org.freebus.fts.service.exception.JobFailedException;
import org.freebus.knxcomm.BusInterface;

/**
 * Abstract base class for jobs that allow job listeners.
 * <p>
 * The {@link #run(BusInterface)} method is implemented with a try / catch block
 * and calls {@link #init(BusInterface)}, {@link #main(BusInterface)}, and
 * {@link #cleanup(BusInterface)} to do the work.
 */
public abstract class ListenableJob implements Job
{
   private final CopyOnWriteArrayList<JobListener> listeners = new CopyOnWriteArrayList<JobListener>();
   private boolean active;

   /**
    * Do the initialization. The default implementation is empty. Called by
    * {@link #run(BusInterface) run}.
    * 
    * @param bus - the bus interface to use
    */
   public void init(BusInterface bus)
   {
   }

   /**
    * Do the work. Called by {@link #run(BusInterface) run}. Implementations
    * shall test regularly within main() if the job is still {@link #isActive()
    * active} and terminate main() if the job is not active anymore.
    * 
    * @param bus - the bus interface to use
    * 
    * @throws IOException
    * @throws TimeoutException
    * @throws JobFailedException
    */
   public abstract void main(BusInterface bus) throws IOException, TimeoutException, JobFailedException;

   /**
    * Do cleanup. The default implementation is empty. Called by
    * {@link #run(BusInterface) run} after {@link #main(BusInterface)}, even if
    * an exception is thrown in {@link #main(BusInterface) main} - but not if an
    * exception is thrown in {@link #init(BusInterface)}.
    * 
    * @param bus - the bus interface to use
    */
   public void cleanup(BusInterface bus)
   {
   }

   /**
    * Calls {@link #init(BusInterface)}, then {@link #main(BusInterface)} to do
    * the real work, and calls {@link #cleanup(BusInterface)}, even if an
    * exception is thrown in {@link #main(BusInterface)} - but not if an
    * exception is thrown in {@link #init(BusInterface)}.
    * 
    * @throws JobFailedException
    */
   @Override
   public final void run(BusInterface bus) throws JobFailedException
   {
      boolean initDone = false;

      try
      {
         active = true;

         init(bus);
         initDone = true;

         main(bus);
      }
      catch (IOException e)
      {
         notifyListener(100, null);
         throw new JobFailedException(e);
      }
      catch (TimeoutException e)
      {
         notifyListener(100, null);
         throw new JobFailedException(e);
      }
      finally
      {
         if (initDone)
            cleanup(bus);

         active = false;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void cancel()
   {
      active = false;
   }

   /**
    * Test if the job is active. {@link #cancel()} clears the
    * active flag.
    */
   public final boolean isActive()
   {
      return active;
   }

   /**
    * Inform all listeners about the current progress.
    * 
    * @param done - how much of the work is done, in percent.
    * @param message - a detailed message about the progress. Can be null.
    */
   protected void notifyListener(int done, String message)
   {
      for (JobListener listener : listeners)
         listener.progress(done, message);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void addListener(JobListener listener)
   {
      listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void removeListener(JobListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * @return all listeners
    */
   protected CopyOnWriteArrayList<JobListener> getListeners()
   {
      return listeners;
   }

   /**
    * Sleep some milliseconds.
    */
   protected final void msleep(int milliseconds)
   {
      try
      {
         Thread.sleep(milliseconds);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }
}
