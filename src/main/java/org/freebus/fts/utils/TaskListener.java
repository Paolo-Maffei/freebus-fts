package org.freebus.fts.utils;

/**
 * Interface for classes that listen on long running tasks.
 */
public interface TaskListener
{
   /**
    * Gets called when there is progress.
    * 
    * @param done - amount of work that is done, in percent.
    * @param message - an optional message about what is being done at the
    *           moment. May be null.
    */
   public void progress(int done, String message);
}
