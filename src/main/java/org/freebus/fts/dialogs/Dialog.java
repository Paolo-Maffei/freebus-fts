package org.freebus.fts.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.freebus.fts.gui.MainWindow;

/**
 * A basic dialog. With a contents area that can be used for custom contents,
 * and a button area for buttons.
 */
public class Dialog
{
   private final Shell shell;

   /**
    * The contents area of the dialog. This widget is created in the constructor
    * of {@link Dialog}.
    */
   protected final Group contents;

   /**
    * The buttons area of the dialog. This widget is created in the constructor
    * of {@link Dialog}.
    */
   protected final Composite buttonBox;

   /**
    * Create a dialog window.
    */
   public Dialog(String title, int style)
   {
      super();

      FormData formData;

      shell = new Shell(MainWindow.getInstance().getShell(), style);
      shell.setLayout(new FormLayout());
      shell.setText(title);
      shell.setSize(640, 400);

      buttonBox = new Composite(shell, SWT.FLAT);
      formData = new FormData();
      formData.bottom = new FormAttachment(100);
      formData.right = new FormAttachment(100);
      buttonBox.setLayoutData(formData);

      contents = new Group(shell, SWT.SHADOW_NONE);
      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.bottom = new FormAttachment(buttonBox, 1);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      contents.setLayoutData(formData);

      RowLayout rowLayout = new RowLayout();
      rowLayout.fill = true;
      rowLayout.pack = false;
      rowLayout.spacing = 8;
      rowLayout.marginLeft = 8;
      rowLayout.marginRight = 8;
      rowLayout.marginTop = 4;
      rowLayout.marginBottom = 4;
      rowLayout.type = SWT.HORIZONTAL;
      buttonBox.setLayout(rowLayout);
   }

   /**
    * Create a dialog window with default style: {@link SWT#DIALOG_TRIM} and
    * {@link SWT#APPLICATION_MODAL}.
    */
   public Dialog(String title)
   {
      this(title, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
   }

   /**
    * Open the dialog.
    */
   public void open()
   {
      buttonBox.layout();
      contents.layout();
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
    * Close and dispose the dialog.
    */
   public void dispose()
   {
      if (!shell.isDisposed())
      {
         shell.close();
         shell.dispose();
      }
   }
   
   /**
    * @return true if the dialog is disposed.
    */
   public boolean isDisposed()
   {
      return shell.isDisposed();
   }

   /**
    * @return the size of the dialog.
    */
   public Point getSize()
   {
      return shell.getSize();
   }

   /**
    * Set the size of the dialog.
    */
   public void setSize(int width, int height)
   {
      shell.setSize(width, height);
   }

   /**
    * Set the size of the dialog.
    */
   public void setSize(Point size)
   {
      shell.setSize(size);
   }

   /**
    * Position the dialog in the center of the screen.
    */
   public void center()
   {
      final Rectangle dr = shell.getDisplay().getBounds();
      final Rectangle sr = shell.getBounds();

      shell.setLocation((dr.width - sr.width) >> 1, (dr.height - sr.height) >> 1);
   }

   /**
    * @return the display of the dialog's shell.
    */
   public Display getDisplay()
   {
      return shell.getDisplay();
   }
}
