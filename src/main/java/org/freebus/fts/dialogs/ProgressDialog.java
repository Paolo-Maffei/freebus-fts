package org.freebus.fts.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Display;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ListenableWorker;
import org.freebus.fts.utils.TaskListener;

/**
 * A dialog with a progress bar, a general text, and a text
 * about the current step.
 */
public class ProgressDialog extends Dialog implements TaskListener
{
   private final Label lblDesc = new Label(contents, SWT.FLAT);
   private final ProgressBar progressBar = new ProgressBar(contents, SWT.HORIZONTAL|SWT.SMOOTH);
   private final Label lblStep = new Label(contents, SWT.FLAT);
   private final Button button;
   private Thread thread;

   /**
    * Create a new progress dialog.
    */
   public ProgressDialog(String title, String description)
   {
      super(title);

      RowLayout rowLayout = new RowLayout();
      rowLayout.fill = true;
      rowLayout.pack = true;
      rowLayout.spacing = 4;
      rowLayout.marginLeft = 4;
      rowLayout.marginRight = 4;
      rowLayout.marginTop = 4;
      rowLayout.marginBottom = 4;
      rowLayout.type = SWT.VERTICAL;
      contents.setLayout(rowLayout);

      lblDesc.setText(description);
      lblDesc.pack();

      progressBar.setMinimum(0);
      progressBar.setMaximum(100);

      button = new Button(buttonBox, SWT.PUSH);
      button.setText(I18n.getMessage("ProgressDialog_Cancel"));

      lblStep.setText(" ");
      lblStep.pack();

      open();
   }

   /**
    * Calls {@link ListenableWorker#run} in a new thread on the worker and shows
    * the progress.
    */
   public void run(final ListenableWorker worker)
   {
      worker.addListener(this);

      final Runnable run = new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               worker.run();

               Display.getDefault().syncExec(new Runnable()
               {
                  @Override
                  public void run()
                  {
                     close();
                     thread = null;
                  }
               });
            }
            catch (Exception e)
            {
               new ExceptionDialog(e);
            }
         }
      };

      thread = new Thread(run);
      thread.start();
   }

   @Override
   public void progress(int done, String message)
   {
      progressBar.setSelection(done);
      lblStep.setText(message);
      lblStep.pack();
   }
}
