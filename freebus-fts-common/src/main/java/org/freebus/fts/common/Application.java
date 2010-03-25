package org.freebus.fts.common;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * A Swing application main class. Inherit this class in your application and
 * you get common tasks done automatically.
 */
public abstract class Application
{
   private final static Semaphore closeWait = new Semaphore(0);
   private static boolean restart = true;
   private static WeakReference<JFrame> mainWindowRef;

   /**
    * Restart the application by setting the restart flag and sending a close
    * event to the main window. Only works if the main window has been set.
    *
    * @see {@link #setMainWindow(JFrame)}
    */
   public static void restart()
   {
      restart = true;
      closeMainWindow();
   }

   /**
    * Terminate the application. Only works if the main window has been set.
    *
    * @see {@link #setMainWindow(JFrame)}
    */
   public static void exit()
   {
      restart = false;
      closeMainWindow();
   }

   /**
    * Close the main window. Does nothing if the main window was not set.
    *
    * @see {@link #setMainWindow(JFrame)}
    */
   public static void closeMainWindow()
   {
      if (mainWindowRef != null)
      {
         final JFrame mainWindow = mainWindowRef.get();
         if (mainWindow != null)
            mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
      }
   }

   /**
    * Launch the application. This method will not return.
    */
   public final static void launch(final Class<? extends Application> appClass, final String[] args)
   {
      Application app = null;

      try
      {
         Environment.setAppName("fts");

         while (restart)
         {
            restart = false;

            app = appClass.newInstance();
            app.waitEventsDone();

            app.initialize(args);
            app.startup();
            app.waitEventsDone();

            app.ready();

            Logger.getLogger(appClass).debug("FTS startup done");
            closeWait.acquire();
         }
      }
      catch (Exception e)
      {
         if (app != null)
            app.fatalException(e);
         else e.printStackTrace();

         System.exit(1);
      }

      System.exit(0);
   }

   /**
    * Set the main window. Setting the main window will exit or restart the
    * application when the main window is disposed.
    *
    * Use
    * <code>mainWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);</code> to
    * set your main window to dispose when the close button is clicked.
    */
   public void setMainWindow(final JFrame mainWindow)
   {
      mainWindowRef = new WeakReference<JFrame>(mainWindow);

      mainWindow.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosed(WindowEvent e)
         {
            shutdown();
            closeWait.release();
         }
      });
   }

   /**
    * Non-GUI application initialization.
    * <p>
    * This method is called by the static {@code launch} method, subclasses must
    * override it.
    *
    * @param args - the application's command line arguments
    */
   protected void initialize(String[] args)
   {
   }

   /**
    * Application startup. Responsible for starting the application; for
    * creating and showing the initial GUI.
    * <p>
    * This method is called by the static {@code launch} method, subclasses must
    * override it.
    */
   protected abstract void startup();

   /**
    * Startup finished, the application is ready to run. Called after all
    * pending Swing events have been processed.
    * <p>
    * It's usually important for an application to start up as quickly as
    * possible. Applications can override this method to do some additional
    * start up work, after the GUI is up and ready to use.
    * <p>
    * This method is called by the static {@code launch} method, subclasses must
    * override it.
    */
   protected void ready()
   {
   }

   /**
    * Called when the application {@link #exit exits} or {@link #restart
    * restarts}. (Currently?) called when the main window is disposed.
    *
    * @see {#setMainWindow(JFrame)}
    */
   protected void shutdown()
   {
   }

   /**
    * Wait until all Swing events are processed
    */
   protected final void waitEventsDone()
   {
      try
      {
         SwingUtilities.invokeAndWait(new Runnable()
         {
            @Override
            public void run()
            {
            }
         });
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Called when an exception is caught from {@link #launch()}. The default
    * implementation prints the stack trace to the console.
    *
    * The application will terminate after this method in any case.
    */
   protected void fatalException(Exception e)
   {
      e.printStackTrace();
   }
}
