package org.freebus.fts.jobs;

import java.util.concurrent.CopyOnWriteArrayList;

public class ListenableJob
{
   private final CopyOnWriteArrayList<JobListener> listeners = new CopyOnWriteArrayList<JobListener>();

   public ListenableJob()
   {
      super();
   }

   /**
    * Inform all listeners about the current progress.
    *
    * @param done - how much of the work is done, in percent.
    * @param message - a detailed message about the progress. Can be null.
    */
   protected void notifyListener(int done, String message)
   {
      for (JobListener listener: listeners)
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
}
