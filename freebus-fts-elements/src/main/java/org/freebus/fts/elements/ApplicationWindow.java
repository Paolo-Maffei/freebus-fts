package org.freebus.fts.elements;

import javax.swing.JFrame;

/**
 * Abstract base class for singleton application windows.
 * <p>
 * Used e.g. by the {@link Dialogs standard dialogs} to determine
 * the application window.
 */
public abstract class ApplicationWindow extends JFrame
{
   private static final long serialVersionUID = 2412814330257460348L;

   private static ApplicationWindow instance;

   /**
    * @return the global {@link MainWindow} instance.
    */
   public static ApplicationWindow getInstance()
   {
      return instance;
   }

   /**
    * Set the global {@link ApplicationWindow} instance.
    * 
    * @param instance - the instance to set.
    * 
    */
   public static void setInstance(ApplicationWindow instance)
   {
      ApplicationWindow.instance = instance;
   }
}
