package org.freebus.fts.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ImageCache;
import org.freebus.fts.utils.SimpleSelectionListener;

/**
 * A dialog that shows an exception.
 */
public final class ExceptionDialog extends Dialog
{
   private final Exception exception;
   private ExceptionDetailsDialog details;

   /**
    * Create an exception dialog. Also prints a stack trace to the console.
    */
   public ExceptionDialog(Exception e)
   {
      super(I18n.getMessage("ExceptionDialog_Title"));
      this.exception = e;

      exception.printStackTrace();

      final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
      rowLayout.marginTop = 8;
      rowLayout.marginBottom = 8;
      rowLayout.marginLeft = 8;
      rowLayout.marginRight = 8;
      rowLayout.spacing = 8;
      contents.setLayout(rowLayout);

      Button btn;
      Label lbl;

      lbl = new Label(contents, SWT.FLAT);
      lbl.setImage(ImageCache.getImage("large_icons/messagebox_critical"));
      lbl.pack();

      lbl = new Label(contents, SWT.FLAT | SWT.WRAP);
      lbl.setText(I18n.getMessage("ExceptionDialog_Text").replace("%1", exception.getMessage()));

      btn = new Button(buttonBox, SWT.PUSH);
      btn.setText(I18n.getMessage("ExceptionDialog_Details"));
      btn.addSelectionListener(new SimpleSelectionListener()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
            details = new ExceptionDetailsDialog(exception);
            details.center();
            details.open();
         }
      });

      btn = new Button(buttonBox, SWT.PUSH);
      btn.setText(I18n.getMessage("Close_Button"));
      btn.addSelectionListener(new SimpleSelectionListener()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
            if (details != null) details.dispose();
            dispose();
         }
      });

      center();
      open();
   }
}
