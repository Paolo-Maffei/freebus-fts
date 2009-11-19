package org.freebus.fts;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.freebus.fts.comm.BusInterfaceFactory;
import org.freebus.fts.comm.jobs.JobQueue;
import org.freebus.fts.db.Database;
import org.freebus.fts.db.DatabaseResources;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.utils.DeleteDir;
import org.freebus.fts.utils.I18n;

public final class Main
{
   /**
    * Set to true prior to exiting the main window to restart the application.
    */
   public static boolean restart = true;

   /**
    * Start the application.
    */
   public static void main(String[] args)
   {
      final Display display = new Display();
      MainWindow mainWin = null;
      Database db = null;

      while (restart)
      {
         restart = false;
         try
         {
            final Config cfg = Config.getInstance();
            final String appDir = cfg.getAppDir();

            // Remove old (Derby) database directory
            final File oldDb = new File(appDir + "/products.db");
            if (oldDb.exists()) DeleteDir.deleteDir(oldDb);

//            // Create (HSQL) database instance
//            Database.setDefault(new Database(appDir + '/' + cfg.getProductsDb(), true));
//
////            // Ensure that the database contents exists and is up to date
//            final SchemaConfig schemaConfig = new SchemaConfig();
//            schemaConfig.dropAllTables(); // for testing

//            try
//            {
//               schemaConfig.update();
//            }
//            catch (SQLException e)
//            {
//               throw new Exception("Database installation/upgrade failed", e);
//            }
            Database.setDefault(null);

            // Create JPA entity manager
            DatabaseResources.getEntityManagerFactory();

            mainWin = new MainWindow();
            mainWin.open();
            mainWin.run();
            restart = mainWin.isRestart();
         }
         catch (Exception e)
         {
            e.printStackTrace();

            final Shell shell = new Shell(display, SWT.TITLE | SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
            MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR | SWT.YES | SWT.NO);
            mbox.setText(I18n.getMessage("MsgBox_Title_Error"));
            mbox.setMessage(I18n.getMessage("Fatal_Application_Error_Restart").replace("%1", e.getLocalizedMessage()));
            if (mbox.open() == SWT.YES) restart = true;
            shell.dispose();
         }
         finally
         {
            JobQueue.getDefaultJobQueue().dispose();

            BusInterfaceFactory.disposeDefaultInterface();
            if (mainWin != null) mainWin.dispose();
            mainWin = null;

            if (db != null)
            {
               try
               {
                  final Connection con = db.getDefaultConnection();
                  con.createStatement().execute("shutdown");
                  con.commit();
               }
               catch (SQLException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
            }

            DatabaseResources.close();
         }
      }
   }
}
