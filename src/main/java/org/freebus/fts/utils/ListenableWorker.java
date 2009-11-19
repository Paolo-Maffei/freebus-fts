package org.freebus.fts.utils;

import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.widgets.Display;

/**
 * Abstract base class for worker classes that can have listeners. The methods
 * of this class are thread-safe.
 */
public abstract class ListenableWorker
{
   private final CopyOnWriteArrayList<TaskListener> listeners = new CopyOnWriteArrayList<TaskListener>();
   private int totalSteps = 100;
   private int lastSteps = -1;
   private String lastMessage;

   /**
    * The main work method.
    * @throws Exception 
    */
   public abstract void run() throws Exception;

   /**
    * Add a listener.
    */
   public void addListener(TaskListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Remove a listener.
    */
   public void removeListener(TaskListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * Set the total number of progress steps.
    * Default is 100, for percent. 
    */
   protected void setTotalSteps(int totalSteps)
   {
      this.totalSteps = totalSteps;
      lastMessage = "";
      lastSteps = -1;
   }

   /**
    * Inform the listeners about the progress by calling the listeners' progress
    * method {@link TaksListener#progress}. The listeners are called from within
    * the GUI thread.
    * 
    * @param stepsDone - amount of work that is done.
    * @param message - an optional message about what is being done at the
    *           moment. The last message is used if null.
    */
   protected void progress(int stepsDone, String message)
   {
      final int donePerc = stepsDone * 100 / totalSteps;
      final String msg = message == null ? lastMessage : message;
      lastMessage = msg;
      
      System.out.printf("*** %d%% done: %s\n", donePerc, msg);

      if (listeners.isEmpty() || lastSteps == stepsDone) return;

      Display.getDefault().syncExec(new Runnable()
      {
         @Override
         public void run()
         {
            for (final TaskListener listener : listeners)
            {
               listener.progress(donePerc, msg);
            }
         }
      });
   }
}
