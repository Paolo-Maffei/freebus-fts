package org.freebus.knxcomm.jobs;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.freebus.knxcomm.BusInterface;

/**
 * Abstract base class for jobs that allow job listeners.
 * <p>
 * The {@link #run(BusInterface)} method is implemented with a try / catch block
 * and calls {@link #init()}, {@link #main(BusInterface)}, and
 * {@link #cleanup()} to do the work.
 */
public abstract class ListenableJob implements Job
{
   private final CopyOnWriteArrayList<JobListener> listeners = new CopyOnWriteArrayList<JobListener>();

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
    * Do the work. Called by {@link #run(BusInterface) run}.
    *
    * @param bus - the bus interface to use
    *
    * @throws IOException
    */
   public abstract void main(BusInterface bus) throws IOException;

   /**
    * Do cleanup. The default implementation is empty. Called by
    * {@link #run(BusInterface) run} after {@link #main(BusInterface)}, even if
    * an exception is thrown in {@link #main(BusInterface) main} - but not if an
    * exception is thrown in {@link #init()}.
    *
    * @param bus - the bus interface to use
    */
   public void cleanup(BusInterface bus)
   {
   }

   /**
    * Calls {@link #init()}, then {@link #main(BusInterface)} to do the real
    * work, and calls {@link #cleanup()}, even if an exception is thrown in
    * {@link #main(BusInterface)} - but not if an exception is thrown in
    * {@link #init()}.
    *
    * @throws IOException
    */
   @Override
   public final void run(BusInterface bus) throws IOException
   {
      init(bus);

      try
      {
         main(bus);
      }
      catch (Exception e)
      {
         throw new IOException(e);
      }
      finally
      {
         cleanup(bus);
      }
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
   public final void addListener(JobListener listener)
   {
      listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
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
}
