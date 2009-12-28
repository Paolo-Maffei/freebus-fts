package org.freebus.fts.components;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.freebus.fts.jobs.JobQueue;
import org.freebus.fts.jobs.JobQueueEvent;
import org.freebus.fts.jobs.JobQueueListener;

/**
 * A widget that displays the state of the {@link JobQueue}.
 */
public class JobQueueView extends JPanel implements JobQueueListener
{
   private static final long serialVersionUID = -5380552551637788389L;
   
   private final JLabel lblName, lblMessage;
   private final JProgressBar prbDone;

   /**
    * Create a job-queue widget that displays the state of the {@link JobQueue}.
    */
   public JobQueueView()
   {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      lblName = new JLabel();
      add(lblName);

      prbDone = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
      add(prbDone);

      lblMessage = new JLabel();
      add(lblMessage);
   }
   
   /**
    * Callback: a job-queue event occurred.
    */
   @Override
   public void jobQueueEvent(JobQueueEvent event)
   {
      lblName.setText(event.job.getLabel());
      prbDone.setValue(event.progress);
      lblMessage.setText(event.message);
   }
}
