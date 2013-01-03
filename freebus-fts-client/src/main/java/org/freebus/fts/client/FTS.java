package org.freebus.fts.client;

import org.freebus.fts.client.application.Application;
import org.freebus.fts.client.application.ApplicationFactory;
import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.common.Environment;

/**
 * The FTS application class. This class is responsible for starting the
 * application and shutting it down. GUI related stuff is handled by the
 * {@link MainWindow main window} class.
 * <p>
 * This class also contains the {@link #main} of FTS.
 */
public final class FTS
{
   static
   {
      Environment.setAppName("fts");
   }

   /**
    * @return the instance of the FTS application.
    */
   public static Application getInstance()
   {
      return Application.getInstance();
   }

   /**
    * Start the application
    */
   public static void main(String[] args)
   {
      ApplicationFactory.args = args;
      (new ApplicationFactory()).createApplication();
   }
}
