package org.freebus.fts.common.log;

import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.freebus.fts.common.Environment;

/**
 * Helper class that sets up the logging.
 *
 * Call {@link #setup()} to setup the logging system.
 */
public final class Logging
{
   private static boolean setupDone = false;

   /**
    * Setup the logging system and install a java.util.log to Log4J log
    * converter. Call this method once, e.g. in your main.
    */
   public synchronized static void setup()
   {
      if (setupDone)
         return;

      setupDone = true;

      final URL logProps = ClassLoader.getSystemResource("log4j.properties");

      if (logProps != null)
      {
         // ensure that "app.dir" property is initialized, for Log4J's FileAppender
         Environment.getAppDir();

         PropertyConfigurator.configure(logProps);
      }
      else
      {
         BasicConfigurator.configure();
      }

      activateLogBridge(Level.ALL);
   }

   /**
    * Activate the logging bridge from java.util.logging to log4j. All
    * java.util.logging log handlers are removed.
    *
    * Called by {@link #setup()}.
    */
   public static void activateLogBridge(Level logLevel)
   {
      java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");

      // remove old handlers
      for (Handler handler : rootLogger.getHandlers())
         rootLogger.removeHandler(handler);

      // add our own
      final Handler activeHandler = new LogToLog4jHandler();
      activeHandler.setLevel(logLevel);
      rootLogger.addHandler(activeHandler);
      rootLogger.setLevel(Level.INFO);
   }

   /*
    * No need to create objects from this class.
    */
   private Logging()
   {
   }
}
