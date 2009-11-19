package org.freebus.fts.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.freebus.fts.Config;
import org.freebus.fts.comm.BusInterfaceFactory;
import org.freebus.fts.gui.MainWindow;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.SimpleSelectionListener;

/**
 * The settings dialog.
 */
public class Settings
{
   private final Shell shell = new Shell(MainWindow.getInstance().getShell(), SWT.DIALOG_TRIM|SWT.SYSTEM_MODAL);
   private final Group contents = new Group(shell, SWT.SHADOW_NONE);
   private final Group portSelectorGroup = new Group(contents, SWT.SHADOW_NONE);
   private final Group productsDbGroup = new Group(contents, SWT.SHADOW_NONE);
   private final PortSelector portSelector = new PortSelector(portSelectorGroup, SWT.FLAT);
   private final DatabaseSettings dbsProductsDb = new DatabaseSettings(productsDbGroup, SWT.READ_ONLY);
   private static Settings instance = null;

   /**
    * Open the settings dialog.
    */
   public static void openDialog()
   {
      if (instance==null) instance = new Settings();
      else instance.shell.setMinimized(false);
   }

   /**
    * Create a settings dialog.
    */
   protected Settings()
   {
      shell.setText(I18n.getMessage("Settings_Title"));
      shell.setLayout(new FormLayout());
      shell.setSize(500, 400);

      Button btn;
      FormData formData;
      FillLayout fillLayout;

      contents.setLayout(new FormLayout());

      formData = new FormData();
      formData.top = new FormAttachment(2);
      formData.left = new FormAttachment(2);
      formData.right = new FormAttachment(98);
      contents.setLayoutData(formData);

      portSelectorGroup.setText(I18n.getMessage("Settings_Port_Selector"));
      fillLayout = new FillLayout();
      fillLayout.marginWidth = 4;
      fillLayout.marginHeight = 4;
      portSelectorGroup.setLayout(fillLayout);
      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      portSelectorGroup.setLayoutData(formData);

      productsDbGroup.setText(I18n.getMessage("Settings_ProductsDb"));
      fillLayout = new FillLayout();
      fillLayout.marginWidth = 4;
      fillLayout.marginHeight = 4;
      productsDbGroup.setLayout(fillLayout);
      formData = new FormData();
      formData.top = new FormAttachment(portSelectorGroup, 1);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      productsDbGroup.setLayoutData(formData);

      //
      // Dialog buttons
      //
      Composite btnBox = new Composite(shell, SWT.FLAT);

      RowLayout btnBoxLayout = new RowLayout();
      btnBoxLayout.type = SWT.HORIZONTAL;
      btnBoxLayout.spacing = 8;
      btnBoxLayout.marginWidth = 20;
      btnBoxLayout.marginHeight = 8;
      btnBoxLayout.fill = false;
      btnBoxLayout.pack = false;
      btnBox.setLayout(btnBoxLayout);
      formData = new FormData();
      formData.bottom = new FormAttachment(100);
      formData.right = new FormAttachment(100);
      btnBox.setLayoutData(formData);

      btn = new Button(btnBox, SWT.PUSH);
      btn.setText(I18n.getMessage("Save_Button"));
      btn.addSelectionListener(new SimpleSelectionListener()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
            save();
         }  
      });
      RowData rowData = new RowData();
      rowData.width = 120;
      btn.setLayoutData(rowData);

      btn = new Button(btnBox, SWT.PUSH);
      btn.setText(I18n.getMessage("Cancel_Button"));
      btn.addSelectionListener(new SimpleSelectionListener()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
            cancel();
         }  
      });
      btn.pack();

      portSelectorGroup.layout();
      productsDbGroup.layout();

      shell.layout();
      shell.open();
   }

   /**
    * Save button pressed
    */
   protected void save()
   {
      boolean restartRequired = false;

      portSelector.apply();
      if (dbsProductsDb.apply()) restartRequired = true;

      Config.getInstance().save();
      BusInterfaceFactory.disposeDefaultInterface();

      instance = null;

      if (restartRequired)
      {
         MessageBox mbox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
         mbox.setMessage(I18n.getMessage("Settings_Restart_required"));
         int ret = mbox.open();
         if (ret == SWT.YES) MainWindow.getInstance().setRestart(true);
      }

      shell.close();
      shell.dispose();
   }

   /**
    * Cancel button pressed
    */
   protected void cancel()
   {
      instance = null;

      shell.close();
      shell.dispose();
   }
}
