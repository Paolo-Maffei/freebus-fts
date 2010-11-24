package org.freebus.fts.components;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.service.job.JobQueue;
import org.freebus.fts.service.job.JobQueueListener;
import org.freebus.fts.service.job.event.JobQueueErrorEvent;
import org.freebus.fts.service.job.event.JobQueueEvent;

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
      setLayout(new GridLayout(0, 1));
      setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));

      final JLabel lblCaption = new JLabel(I18n.getMessage("JobQueueView.Caption"));
      lblCaption.setFont(lblCaption.getFont().deriveFont(Font.BOLD));
      lblCaption.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
      add(lblCaption);

      lblName = new JLabel(" ");
      add(lblName);

      prbDone = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
      add(prbDone);

      lblMessage = new JLabel(" ");
      add(lblMessage);

      setMinimumSize(getSize());
   }

   /**
    * Callback: a job-queue event occurred.
    */
   @Override
   public void jobQueueEvent(JobQueueEvent event)
   {
      if (event.job == null)
         lblName.setText(I18n.getMessage("JobQueueView.NoJob"));
      else lblName.setText(event.job.getLabel());

      prbDone.setValue(event.progress);
      lblMessage.setText(event.message == null ? " " : event.message);
   }

@Override
public void jobQueueErrorEvent(final JobQueueErrorEvent event) {

	
	
}
}
