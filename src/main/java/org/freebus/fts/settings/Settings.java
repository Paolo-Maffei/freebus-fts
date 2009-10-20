package org.freebus.fts.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.freebus.fts.Config;
import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.SimpleSelectionListener;

/**
 * The settings dialog.
 */
public class Settings
{
   private final Shell shell = new Shell(Display.getDefault());
   private final Group contents = new Group(shell, SWT.FLAT);
   private final Group portSelectorGroup = new Group(contents, SWT.BORDER|SWT.SHADOW_ETCHED_IN);
   private final PortSelector portSelector = new PortSelector(portSelectorGroup, SWT.FLAT);
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
      shell.setSize(640, 400);

      Button btn;
      FormData formData;
      FillLayout fillLayout;

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
      contents.setLayout(new FormLayout());
      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      portSelectorGroup.setLayoutData(formData);

      Composite btnBox = new Composite(shell, SWT.FLAT);
      fillLayout = new FillLayout();
      fillLayout.type = SWT.HORIZONTAL;
      fillLayout.spacing = 8;
      fillLayout.marginWidth = 20;
      fillLayout.marginHeight = 8;
      btnBox.setLayout(fillLayout);
      formData = new FormData();
      formData.bottom = new FormAttachment(100);
      formData.right = new FormAttachment(100);
      btnBox.setLayoutData(formData);

      btn = new Button(btnBox, SWT.DEFAULT);
      btn.setText(I18n.getMessage("Save_Button"));
      btn.addSelectionListener(new OnSave());
      btn.pack();

      btn = new Button(btnBox, SWT.DEFAULT);
      btn.setText(I18n.getMessage("Cancel_Button"));
      btn.addSelectionListener(new OnCancel());
      btn.pack();

      shell.open();
   }

   /**
    * Event listener for: save button pressed
    */
   private class OnSave extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         portSelector.apply();
         Config.getConfig().save();
         BusInterface.disposeInstance();

         instance = null;
         shell.close();
         shell.dispose();        
      }
   }

   /**
    * Event listener for: cancel button pressed
    */
   private class OnCancel extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         instance = null;
         shell.close();
         shell.dispose();
      }
   }
}
