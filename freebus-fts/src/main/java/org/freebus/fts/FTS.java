package org.freebus.fts;

import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import liquibase.lock.LockHandler;

import org.apache.log4j.Logger;
import org.freebus.fts.common.Environment;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.ProjectControllerImpl;
import org.freebus.fts.elements.StartupIndicator;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.services.LookAndFeelManager;
import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.internal.JarLoader;
import org.freebus.knxcomm.types.LinkMode;
import org.jdesktop.application.Application;
import org.jdesktop.application.SessionStorage;

/**
 * The FTS application class. This class is responsible for starting the
 * application and shutting it down. GUI related stuff is handled by the
 * {@link MainWindow main window} class.
 * <p>
 * This class also contains the {@link #main} of FTS.
 */
public final class FTS extends Application
{
   private Properties manifestProps;
   private StartupIndicator startupIndicator;
   private LookAndFeelManager lafLoader;
   private Config config;
   private MainWindow mainWin;
   private int exitCode = 0;

   /**
    * @return the instance of the FTS application.
    */
   public static FTS getInstance()
   {
      return (FTS) Application.getInstance();
   }

   /**
    * Returns the name of the application. May include version and/or revision
    * information.
    * 
    * @return the name of the application.
    */
   public String getName()
   {
      return "Freebus FTS";
   }

   /**
    * @return the manifest's properties of the main JAR of FTS.
    */
   public Properties getManifestProperties()
   {
      if (manifestProps == null)
      {
         manifestProps = new Properties();

         try
         {
            final String classContainer = getClass().getProtectionDomain().getCodeSource().getLocation().toString();

            if (classContainer.toLowerCase().endsWith(".jar"))
            {
               final URL manifestUrl = new URL("jar:" + classContainer + "!/META-INF/MANIFEST.MF");
               manifestProps.load(manifestUrl.openStream());
            }
            else
            {
               Logger.getLogger(getClass()).info("FTS is not started from a jar. Manifest information is unavailable.");
            }
         }
         catch (IOException e)
         {
            Dialogs.formatExceptionMessage(e, "Failed to load the manifest from the FTS jar");
         }
      }

      for (Object key : manifestProps.keySet())
         System.err.println(key + "=" + manifestProps.getProperty((String) key));

      return manifestProps;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void initialize(String[] args)
   {
      Environment.setAppName("fts");
      Logger.getLogger(getClass()).debug("FTS initialize");

      BusInterfaceFactory.setDefaultLinkMode(LinkMode.BusMonitor);
      getContext().getResourceManager().getResourceMap(getClass());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void startup()
   {
      getManifestProperties();

      try
      {
         Logger.getLogger(getClass()).debug("FTS startup");
         startupIndicator = new StartupIndicator();

         // The startup steps
         startupConfig(10);
         startupPlugins(20);
         startupLookAndFeel(30);
         startupUpgradeDatabase(40);
         startupConnectDatabase(70);
         startupMainWindow(80);
         startupPostMainWindowCreate(90);

         // Final steps
         startupIndicator.setProgress(100, "");
         mainWin.setVisible(true);
         startupIndicator.close();
         startupIndicator = null;
         config.save();
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("FTS.ErrStartup"));
         Runtime.getRuntime().exit(1);
      }
   }

   /**
    * Load the configuration. Called from {@link #startup()}.
    * 
    * @param progress - the initial value of the progress indicator.
    */
   private void startupConfig(int progress)
   {
      startupIndicator.setProgress(progress, I18n.getMessage("FTS.StartupConfig"));
      config = new Config();
   }

   /**
    * Load the plugins. Called from {@link #startup()}.
    * 
    * @param progress - the initial value of the progress indicator.
    */
   private void startupPlugins(int progress)
   {
      startupIndicator.setProgress(progress, I18n.getMessage("FTS.StartupPlugins"));

      loadPlugins(Environment.getAppDir() + "/plugins");
      loadPlugins("plugins");
   }

   /**
    * Load the plugins. Called from {@link #startup()}.
    * 
    * @param progress - the initial value of the progress indicator.
    */
   private void startupLookAndFeel(int progress)
   {
      startupIndicator.setProgress(progress, I18n.getMessage("FTS.StartupLookAndFeel"));

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
   }

   /**
    * Upgrade the database, if required. Called from {@link #startup()}.
    * 
    * @param progress - the initial value of the progress indicator.
    * 
    * @throws Exception
    */
   private void startupUpgradeDatabase(int progress) throws Exception
   {
      startupIndicator.setProgress(progress, I18n.getMessage("FTS.StartupUpgradeDatabase"));

      Connection con;
      ConnectionDetails conDetails = null;
      boolean dbConfigExisted = true;

      try
      {
         conDetails = new ConnectionDetails();
         dbConfigExisted = conDetails.fromConfig(config);

         con = DatabaseResources.createConnection(conDetails);

         if (!dbConfigExisted)
            conDetails.toConfig(config);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("FTS.ErrConnectDatabase"));

         int ret = JOptionPane.showConfirmDialog(null,
               "<html><body width=\"300\">" + I18n.getMessage("FTS.ConnectDatabaseProblem") + "</body></html>",
               I18n.getMessage("Dialogs.Warning_Title"), JOptionPane.YES_NO_OPTION);

         if (ret != JOptionPane.YES_OPTION)
            Runtime.getRuntime().exit(2);

         conDetails = new ConnectionDetails();
         con = DatabaseResources.createConnection(conDetails);
         conDetails.toConfig(config);
      }

      Liquibase liq;
      try
      {
         liq = DatabaseResources.createMigrator("db-changes/changelog-0.1.xml", con);
         liq.validate();
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("FTS.ErrUpgradeDatabaseSkip"));
         return;
      }

      int tries = 10;
      final LockHandler liqLockHandler = LockHandler.getInstance(liq.getDatabase());
      for (; tries > 0; --tries)
      {
         try
         {
            liqLockHandler.acquireLock();
            liqLockHandler.releaseLock();
            break;
         }
         catch (LockException e)
         {
            if (tries >= 10)
               startupIndicator.setProgress(++progress, I18n.getMessage("FTS.StartupUpgradeDatabaseWaitLock"));
            Thread.sleep(500);
         }
      }

      if (tries <= 0)
      {
         int ret = JOptionPane.showConfirmDialog(null,
               "<html><body width=\"300\">" + I18n.getMessage("FTS.UpgradeDatabaseForceLock") + "</body></html>",
               I18n.getMessage("Dialogs.Warning_Title"), JOptionPane.YES_NO_OPTION);

         if (ret != JOptionPane.YES_OPTION)
            Runtime.getRuntime().exit(2);

         liq.forceReleaseLocks();
      }

      try
      {
         liq.update(null);
      }
      catch (LiquibaseException e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("FTS.ErrUpgradeDatabase"));

         if (!dbConfigExisted)
            Runtime.getRuntime().exit(1);

         int ret = JOptionPane.showConfirmDialog(null,
               "<html><body width=\"300\">" + I18n.getMessage("FTS.UpgradeDatabaseWipe") + "</body></html>",
               I18n.getMessage("Dialogs.Error_Title"), JOptionPane.YES_NO_OPTION);

         if (ret != JOptionPane.YES_OPTION)
            Runtime.getRuntime().exit(3);

         startupIndicator.setProgress(progress + 5, I18n.getMessage("FTS.StartupUpgradeDatabaseDrop"));
         Logger.getLogger(getClass()).info("Dropping all database tables");

         try
         {
            // Don't ask me why, but Liquibase (1.9.5) sometimes needs some
            // tries until it can
            // wipe all tables and constraints.
            tries = 10;
            while (tries > 0)
            {
               try
               {
                  liq.dropAll();
                  break;
               }
               catch (LiquibaseException le)
               {
                  Logger.getLogger(getClass()).warn(le);
               }
               --tries;
            }

            if (tries <= 0)
            {
               // This last try will be catched below if it still fails and an
               // error dialog
               // will be shown
               liq.dropAll();
            }

            startupIndicator.setProgress(progress + 10, I18n.getMessage("FTS.StartupUpgradeDatabase"));
            liq.update(null);
         }
         catch (LiquibaseException e1)
         {
            Dialogs.showExceptionDialog(e,
                  I18n.formatMessage("FTS.ErrUpgradeDatabaseFailed", new Object[] { Environment.getAppDir() }));
            Runtime.getRuntime().exit(1);
         }
      }
      finally
      {
         if (!con.isClosed())
            con.close();
      }
   }

   /**
    * Connect to the database. Called from {@link #startup()}.
    * 
    * @param progress - the initial value of the progress indicator.
    */
   private void startupConnectDatabase(int progress)
   {
      startupIndicator.setProgress(progress, I18n.getMessage("FTS.StartupConnectDatabase"));

      try
      {
         DatabaseResources.close();
         final EntityManagerFactory emf = DatabaseResources.createDefaultEntityManagerFactory();
         DatabaseResources.setEntityManagerFactory(emf);

         startupIndicator.setProgress(progress + 5);
         emf.createEntityManager();
      }
      catch (PersistenceException e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("FTS.ErrConnectDatabase"));

         int ret = JOptionPane.showConfirmDialog(null,
               "<html><body width=\"300\">" + I18n.getMessage("FTS.ConnectDatabaseProblem") + "</body></html>",
               I18n.getMessage("Dialogs.Warning_Title"), JOptionPane.YES_NO_OPTION);

         if (ret != JOptionPane.YES_OPTION)
            Runtime.getRuntime().exit(2);

         DatabaseResources.close();

         final ConnectionDetails conDetails = new ConnectionDetails();
         final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("default", conDetails);
         DatabaseResources.setEntityManagerFactory(emf);

         startupIndicator.setProgress(progress + 2);
         emf.createEntityManager();
      }
   }

   /**
    * Create the main window. Called from {@link #startup()}.
    * 
    * @param progress - the initial value of the progress indicator.
    */
   private void startupMainWindow(int progress)
   {
      startupIndicator.setProgress(90, I18n.getMessage("FTS.StartupMainWindow"));

      mainWin = new MainWindow();
      mainWin.setName("mainWindow-0");
      mainWin.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosed(WindowEvent e)
         {
            exit();
         }
      });

      try
      {
         getContext().getSessionStorage().restore(mainWin, "session.xml");
      }
      catch (IOException e1)
      {
         e1.printStackTrace();
      }
   }

   /**
    * Startup tasks that have to happen after the main window is created.
    * 
    * @param progress - the initial value of the progress indicator.
    */
   private void startupPostMainWindowCreate(int progress)
   {
      ProjectManager.setController(new ProjectControllerImpl());
   }

   /**
    * Called after the startup() method has returned and there are no more
    * events on the system event queue. When this method is called, the
    * application's GUI is ready to use.
    */
   @Override
   protected void ready()
   {
      mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      if (!readyLoadProject())
         readyCreateSampleProject();

      mainWin.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   /**
    * Load the project that was open on the last session.
    * 
    * @return True if the project could be loaded
    */
   protected boolean readyLoadProject()
   {
      return false;
   }

   /**
    * Create a sample project.
    */
   protected void readyCreateSampleProject()
   {
      try
      {
         Logger.getLogger(getClass()).info("Creating an example project");
         ProjectManager.setProject(SampleProjectFactory.newProject());
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("FTS.ErrCreatingSampleProject"));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void shutdown()
   {
      Logger.getLogger(getClass()).debug("FTS shutdown");

      //
      // Save the state of the main window
      //
      if (mainWin != null)
      {
         mainWin.setVisible(false);

         final SessionStorage sessStore = getContext().getSessionStorage();
         try
         {
            sessStore.save(mainWin, "session.xml");
         }
         catch (IOException e)
         {
            Dialogs.showExceptionDialog(e, I18n.getMessage("FTS.ErrSavingSession"));
         }
      }

      //
      // Close the bus interface
      //
      if (BusInterfaceFactory.busInterfaceOpened())
         BusInterfaceFactory.closeBusInterface();

      DatabaseResources.close();
      config.save();
   }

   /**
    * Restart the application. This terminates the application with return-code
    * 120. The restart has to be handled by the script that started the
    * application.
    * 
    * @see Application#exit()
    */
   public void restart()
   {
      exitCode = 120;
      exit();
   }

   /**
    * Terminates the application with return-code 120 if {@link #restart()} was
    * called, or with return-code 0 in all other cases.
    * <p>
    * {@inheritDoc}
    */
   @Override
   protected void end()
   {
      Runtime.getRuntime().exit(exitCode);
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
