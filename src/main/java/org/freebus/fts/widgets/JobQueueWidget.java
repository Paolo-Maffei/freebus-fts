package org.freebus.fts.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.freebus.fts.comm.jobs.Job;
import org.freebus.fts.comm.jobs.JobQueue;
import org.freebus.fts.comm.jobs.JobQueueEvent;
import org.freebus.fts.comm.jobs.JobQueueListener;

/**
 * A widget that shows the state of the {@link JobQueue}.
 */
public final class JobQueueWidget extends Composite implements JobQueueListener
{
   private final Label lblName, lblMessage;
   private final ProgressBar prbDone;

   /**
    * Create a new job-queue widget.
    */
   public JobQueueWidget(Composite parent, int style)
   {
      super(parent, style);

      final RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
      rowLayout.spacing = 4;
      rowLayout.fill = true;
      setLayout(rowLayout);

      lblName = new Label(this, SWT.FLAT);
      lblName.setText("<Job Name>");

      prbDone = new ProgressBar(this, SWT.HORIZONTAL|SWT.SMOOTH);
      prbDone.setMinimum(0);
      prbDone.setMaximum(100);

      lblMessage = new Label(this, SWT.FLAT);
      lblMessage.setText(" ");

      layout();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void jobEvent(JobQueueEvent event)
   {
      final Job job = event.job;
      if (job == null)
      {
         setVisible(false);
         return;
      }

      lblName.setText(job.getLabel());
      prbDone.setSelection(event.progress);
      if (event.message != null) lblMessage.setText(event.message);
   }
}
