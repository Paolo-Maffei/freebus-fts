package org.freebus.fts.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.freebus.fts.Config;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.SimpleSelectionListener;

/**
 * The settings dialog.
 */
public class Settings
{
   private final Shell shell = new Shell(Display.getDefault());
   private final Group contents = new Group(shell, SWT.BORDER);
   private final PortSelector portSelector = new PortSelector(contents, SWT.FLAT);
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
      shell.setSize(640, 600);

      Button btn;
      FormData formData;

      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      contents.setLayoutData(formData);

      contents.setLayout(new FormLayout());
      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      portSelector.setLayoutData(formData);

      Composite btnBox = new Composite(shell, SWT.BORDER);
      btnBox.setLayout(new RowLayout(SWT.HORIZONTAL | SWT.RIGHT_TO_LEFT));
      formData = new FormData();
      formData.bottom = new FormAttachment(100);
      formData.right = new FormAttachment(100);
      btnBox.setLayoutData(formData);

      btn = new Button(btnBox, SWT.DEFAULT);
      btn.setText(I18n.getMessage("Cancel_Button"));
      btn.addSelectionListener(new OnCancel());

      btn = new Button(btnBox, SWT.DEFAULT);
      btn.setText(I18n.getMessage("Save_Button"));
      btn.addSelectionListener(new OnSave());

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
