package org.freebus.fts;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.freebus.fts.comm.BusInterfaceFactory;
import org.freebus.fts.db.Database;
import org.freebus.fts.db.SchemaConfig;
import org.freebus.fts.gui.MainWindow;
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

      while (restart)
      {
         restart = false;
         try
         {
            final Config cfg = Config.getInstance();
            final String appDir = cfg.getAppDir();
            System.setProperty("derby.system.home", appDir);

            Database db = new Database(appDir + "/products.db", true);
            Database.setDefault(db);

            final SchemaConfig schemaConfig = new SchemaConfig();
            schemaConfig.dropAllTables();

            if (schemaConfig.getVersion() > schemaConfig.getInstalledVersion())
            {
               System.out.println("Need to upgrade the database");
               try
               {
                  schemaConfig.update();
               }
               catch (SQLException e)
               {
                  throw new Exception("Database installation/upgrade failed", e);
               }
            }

            final MainWindow mainWin = new MainWindow();
            mainWin.open();
            mainWin.run();

            mainWin.dispose();
            BusInterfaceFactory.disposeDefaultInstance();
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
      }
   }
}
