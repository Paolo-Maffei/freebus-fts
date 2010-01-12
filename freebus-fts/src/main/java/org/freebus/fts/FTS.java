package org.freebus.fts;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;
import org.freebus.fts.common.Environment;
import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SampleProjectFactory;

/**
 * The main application class.
 */
public final class FTS implements Runnable
{
   // Use {@link Config#getInstance} to access the global configuration object
   @SuppressWarnings("unused")
   private static final Config globalConfig;

   static
   {
      BasicConfigurator.configure();
      Environment.setAppName("fts");
      globalConfig = new Config();
   }

   /**
    * Open the main window.
    */
   public void run()
   {
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
