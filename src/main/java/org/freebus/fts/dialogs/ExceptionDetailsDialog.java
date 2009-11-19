package org.freebus.fts.dialogs;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.SimpleSelectionListener;

/**
 * A dialog that shows details about an exception.
 */
public final class ExceptionDetailsDialog extends Dialog
{
   /**
    * Create an exception details dialog.
    */
   public ExceptionDetailsDialog(final Exception e)
   {
      super(I18n.getMessage("ExceptionDetailsDialog_Title"));

      contents.setLayout(new FillLayout());

      ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
      e.printStackTrace(new PrintStream(out));
      
      Text txt = new Text(contents, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
      txt.setText(out.toString());

      Button btn = new Button(buttonBox, SWT.PUSH);
      btn.setEnabled(false);
      btn.setText(I18n.getMessage("SaveDots_Button"));
      btn.addSelectionListener(new SimpleSelectionListener()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
         }
      });

      btn = new Button(buttonBox, SWT.PUSH);
      btn.setText(I18n.getMessage("Close_Button"));
      btn.addSelectionListener(new SimpleSelectionListener()
      {
         @Override
         public void widgetSelected(SelectionEvent e)
         {
            close();
         }
      });

      center();
      open();
   }

   /**
    * Show the detailed stack trace.
    */
   protected void showDetails()
   {
      // TODO
   }
}
