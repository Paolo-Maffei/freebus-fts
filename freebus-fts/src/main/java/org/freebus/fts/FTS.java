package org.freebus.fts;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.LookAndFeelManager;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.knxcomm.internal.JarLoader;

/**
 * The main application class.
 */
public final class FTS implements Runnable
{
   // Use {@link Config#getInstance} to access the global configuration object
   protected static final Config globalConfig;

   private static LookAndFeelManager lafLoader;

   static
   {
      Environment.setAppName("fts");
      globalConfig = new Config();

      loadPlugins(Environment.getAppDir() + "/plugins");
      loadPlugins("plugins");
   }

   /**
    * Open the main window.
    */
   public void run()
   {
      LookAndFeelManager.setDefaultLookAndFeel();

      if (lafLoader != null)
      {
         lafLoader.install();
         lafLoader = null;
      }

      MainWindow mainWin = new MainWindow();
      ProjectManager.setProject(SampleProjectFactory.newProject());

      init();
      SwingUtilities.updateComponentTreeUI(mainWin);

      mainWin.setVisible(true);
   }

   /**
    * Initialize global components.
    */
   public void init()
   {
      final Config cfg = Config.getInstance();

      final String lfName = cfg.getLookAndFeelName();
      if (!lfName.isEmpty())
      {
         try
         {
            UIManager.setLookAndFeel(lfName);
         }
         catch (Exception e)
         {
            Dialogs.showExceptionDialog(e, I18n.formatMessage("FTS.ErrChangeLookAndFeel", new Object[] { lfName }));
            cfg.setLookAndFeelName("");
         }
      }

      try
      {
         DatabaseResources.close();
         DatabaseResources.setEntityManagerFactory(DatabaseResources.createDefaultEntityManagerFactory());
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("FTS.ErrCreateDefaultEntityManagerFactory"));
      }
   }

   /**
    * Cleanup global components.
    */
   public void cleanup()
   {
   }

   /**
    * Load all JARs from the plugins directory pluginsDir. The method silently
    * returns if the plugins directory does not exist.
    */
   static public void loadPlugins(String pluginsDir)
   {
      final Logger logger = Logger.getLogger(FTS.class);

      final File dir = new File(pluginsDir);
      if (!dir.exists())
         return;

      final String[] fileNames = dir.list();
      Arrays.sort(fileNames);

      for (String fileName : fileNames)
      {
         if (fileName.startsWith(".") || !fileName.endsWith(".jar"))
            continue;

         final String filePath = pluginsDir + File.separatorChar + fileName;

         try
         {
            logger.info("Loading plugin " + filePath);
            JarLoader.loadJar(new String[] { filePath });

            final JarFile jarFile = new JarFile(filePath);
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
               final JarEntry entry = entries.nextElement();
               final String entryName = entry.getName();

               if (entryName.endsWith("LookAndFeel.class") && entryName.indexOf('$') < 0)
               {
                  if (lafLoader == null)
                     lafLoader = new LookAndFeelManager();

                  lafLoader.add(entryName.replace(File.separatorChar, '.').replace(".class", ""));
               }
            }
         }
         catch (Exception e)
         {
            Dialogs.showExceptionDialog(e, I18n.formatMessage("FTS.ErrLoadingPlugin", new String[] { filePath }));
         }
      }
   }

   /**
    * Start the FTS application.
    */
   public static void main(String[] args)
   {
      Runnable fts = new FTS();
      try
      {
         SwingUtilities.invokeAndWait(fts);
      }
      catch (InvocationTargetException ex)
      {
         ex.printStackTrace();
      }
      catch (InterruptedException ex)
      {
         ex.printStackTrace();
      }
      finally
      {
         Config.getInstance().save();
      }
   }
}
