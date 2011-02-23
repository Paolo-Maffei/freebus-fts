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
//   private final static AbstractApplicationContext APPLICATION_CONTEXT;

   static
   {
      Environment.setAppName("fts");
//      APPLICATION_CONTEXT = new ClassPathXmlApplicationContext("classpath:META-INF/applicationContext.xml");
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
//      APPLICATION_CONTEXT.getBean(Application.class);
      (new ApplicationFactory()).createApplication();
   }
}
