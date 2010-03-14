package org.freebus.fts;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.EntityManagerFactory;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.freebus.fts.common.Application;
import org.freebus.fts.common.Environment;
import org.freebus.fts.components.SplashScreen;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.LookAndFeelManager;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.fts.utils.BusInterfaceService;
import org.freebus.knxcomm.internal.JarLoader;

/**
 * The FTS application class
 */
public final class FTS extends Application
{

   private SplashScreen splash = new SplashScreen();
   private LookAndFeelManager lafLoader;
   private Config config;
   private MainWindow mainWin;

   /**
    * {@inheritDoc}
    */
   @Override
   protected void initialize(String[] args)
   {
      Environment.setAppName("fts");
      Logger.getLogger(getClass()).debug("FTS initialize");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void startup()
   {
      Logger.getLogger(getClass()).debug("FTS startup");
      splash.setVisible(true);

      //
      // Load the configuration
      //
      splash.setProgress(10, I18n.getMessage("FTS.InitConfig"));
      config = new Config();

      //
      // Load custom plugins
      //
      splash.setProgress(20, I18n.getMessage("FTS.InitPlugins"));
      loadPlugins(Environment.getAppDir() + "/plugins");
      loadPlugins("plugins");

      //
      // Activate the look & feel
      //
      splash.setProgress(30, I18n.getMessage("FTS.InitLookAndFeel"));
      if (lafLoader != null)
      {
         lafLoader.install();
         lafLoader = null;
      }
      final String lafName = config.getLookAndFeelName();
      if (!lafName.isEmpty())
      {
         try
         {
            UIManager.setLookAndFeel(lafName);
         }
         catch (Exception e)
         {
            Dialogs.showExceptionDialog(e, I18n.formatMessage("FTS.ErrChangeLookAndFeel", new Object[] { lafName }));
            config.setLookAndFeelName("");
            LookAndFeelManager.setDefaultLookAndFeel();
         }
      }

      //
      // Upgrade the database, if required
      //
      splash.setProgress(40, I18n.getMessage("FTS.InitUpgradeDatabase"));
      // TODO: DatabaseResources.createMigrator().upgrade();

      //
      // Open the database connection
      //
      splash.setProgress(60, I18n.getMessage("FTS.InitDatabase"));
      DatabaseResources.close();
      final EntityManagerFactory emf = DatabaseResources.createDefaultEntityManagerFactory();
      DatabaseResources.setEntityManagerFactory(emf);
      splash.setProgress(70);
      emf.createEntityManager();

      //
      // Open the main window
      //
      splash.setProgress(90, I18n.getMessage("FTS.InitMainWindow"));
      mainWin = new MainWindow();
      setMainWindow(mainWin);

      //
      // All done. Show the main window and close the splash screen
      //
      splash.setProgress(100, "");
      mainWin.setVisible(true);

      splash.setVisible(false);
      splash.dispose();
      splash = null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void ready()
   {
      Logger.getLogger(getClass()).debug("FTS ready");

      //
      // Create a sample project and show it in the main window
      //
      ProjectManager.setProject(SampleProjectFactory.newProject());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void shutdown()
   {
      Logger.getLogger(getClass()).debug("FTS shutdown");

      if (BusInterfaceService.busInterfaceOpened())
         BusInterfaceService.closeBusInterface();

      mainWin = null;

      config.save();
      DatabaseResources.close();
   }

   /**
    * Load all JARs from the plugins directory <code>pluginsDir</code>. The
    * method silently returns if the plugins directory does not exist.
    */
   private void loadPlugins(String pluginsDir)
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
    * Start the FTS application
    *
    * @throws InterruptedException
    */
   public static void main(String[] args) throws InterruptedException
   {
      Application.launch(FTS.class, args);
   }
}
