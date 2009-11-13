package org.freebus.fts.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * A basic dialog. With a contents area that can be used for custom contents, and
 * a button area for buttons.
 */
public class Dialog
{
   private final Shell shell = new Shell(Display.getDefault(), SWT.DIALOG_TRIM|SWT.APPLICATION_MODAL);
   protected final Group contents = new Group(shell, SWT.SHADOW_NONE);
   protected final Composite buttonBox = new Composite(shell, SWT.FLAT);

   /**
    * Create a dialog window.
    */
   public Dialog(String title)
   {
      super();

      shell.setLayout(new FormLayout());
      shell.setText(title);
      shell.setSize(640, 400);

      FormData formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      contents.setLayoutData(formData);

      formData = new FormData();
      formData.top = new FormAttachment(contents, 1);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      formData.height = 50;
      buttonBox.setLayoutData(formData);

      RowLayout rowLayout = new RowLayout();
      rowLayout.fill = true;
      rowLayout.pack = false;
      rowLayout.spacing = 4;
      rowLayout.marginLeft = 8;
      rowLayout.marginRight = 8;
      rowLayout.marginTop = 4;
      rowLayout.marginBottom = 4;
      rowLayout.type = SWT.HORIZONTAL;
      buttonBox.setLayout(rowLayout);
   }

   /**
    * Open the dialog.
    */
   public void open()
   {
      shell.layout();
      shell.pack();

      shell.open();
   }

   /**
    * Close the dialog.
    */
   public void close()
   {
      shell.close();
   }

   /**
    * @return the display of the dialog's shell.
    */
   public Display getDisplay()
   {
      return shell.getDisplay();
   }
}
