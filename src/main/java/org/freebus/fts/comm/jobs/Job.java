package org.freebus.fts.comm.jobs;

import java.io.IOException;

import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.utils.TaskListener;

/**
 * Interface for jobs that work with the EIB bus, doing something
 * that involves a sequence of telegrams, like programming a device.
 */
public interface Job
{
   /**
    * Do the work, using the given {@link BusInterface} bus-interface.
    * @throws IOException 
    */
   public void run(BusInterface bus) throws IOException;

   /**
    * @return a human readable label with a short explanation of the job.
    */
   public String getLabel();

   /**
    * Add a listener to the job that gets informed about the progress.
    */
   public void addListener(TaskListener listener);

   /**
    * Remove a listener.
    */
   public void removeListener(TaskListener listener);
}
