package org.freebus.fts.eib.jobs;

import java.util.concurrent.CopyOnWriteArrayList;

import org.freebus.fts.utils.TaskListener;

public class ListenableJob
{
   private final CopyOnWriteArrayList<TaskListener> listeners = new CopyOnWriteArrayList<TaskListener>();

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
      for (TaskListener listener: listeners)
         listener.progress(done, message);
   }

   /**
    * {@inheritDoc}
    */
   public final void addListener(TaskListener listener)
   {
      listeners.add(listener);
   }

   /**
    * {@inheritDoc}
    */
   public final void removeListener(TaskListener listener)
   {
      listeners.remove(listener);
   }
}
