package org.freebus.fts.elements;

import javax.swing.JFrame;

/**
 * Abstract base class for singleton application windows.
 * <p>
 * Used e.g. by the {@link Dialogs standard dialogs} to determine the
 * application window.
 */
public abstract class AbstractApplicationWindow extends JFrame
{
   private static final long serialVersionUID = 2412814330257460348L;

   private static AbstractApplicationWindow instance;

   /**
    * @return the global {@link MainWindow} instance.
    */
   public static AbstractApplicationWindow getInstance()
   {
      return instance;
   }

   /**
    * Set the global {@link AbstractApplicationWindow} instance.
    * 
    * @param instance - the instance to set.
    * 
    */
   public static void setInstance(AbstractApplicationWindow instance)
   {
      AbstractApplicationWindow.instance = instance;
   }
}
