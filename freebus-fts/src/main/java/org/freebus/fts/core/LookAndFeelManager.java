package org.freebus.fts.core;

import java.util.HashSet;
import java.util.Set;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.freebus.fts.FTS;

/**
 * A helper class that gets the class names for a bunch of custom look&feels.
 * The look&feels can then be loaded and installed.
 */
public final class LookAndFeelManager
{
   private final Set<Class<? extends LookAndFeel>> lafClasses = new HashSet<Class<? extends LookAndFeel>>();

   /**
    * Set the default look and feel.
    */
   static public void setDefaultLookAndFeel()
   {
      for (String lafName: new String[] { "com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
                                          "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" })
      {
         try
         {
            UIManager.setLookAndFeel(lafName);
            break;
         }
         catch (Exception e)
         {
            // Never mind if this does not work
         }
      }
   }

   /**
    * Add a look&feel by it's class-name.
    *
    * @throws ClassNotFoundException if the look&feel class cannot be loaded
    */
   public void add(String lookAndFeelClassName) throws ClassNotFoundException
   {
      @SuppressWarnings("unchecked")
      final Class<? extends LookAndFeel> lafClass =
         (Class<? extends LookAndFeel>) FTS.class.getClassLoader().loadClass(lookAndFeelClassName);

      lafClasses.add(lafClass);
   }

   /**
    * Install all look&feel classes that were previously added with {@link #addLookAndFeel(String)}.
    * Must be called from within Swing's UI thread.
    */
   public void install()
   {
      final Logger logger = Logger.getLogger(FTS.class);

      for (Class<? extends LookAndFeel> lafClass: lafClasses)
      {
         try
         {
            logger.info("Installing look&feel plugin: " + lafClass.getSimpleName());
            UIManager.installLookAndFeel(lafClass.newInstance().getName(), lafClass.getCanonicalName());
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }
}
