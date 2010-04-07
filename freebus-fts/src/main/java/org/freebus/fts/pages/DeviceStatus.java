package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.jobs.JobStep;
import org.freebus.fts.jobs.JobStepStatusEvent;
import org.freebus.fts.jobs.JobStepStatusListner;
import org.freebus.fts.jobs.JobSteps;
import org.freebus.fts.jobs.ReadDeviceStatusJob;

public class DeviceStatus extends AbstractPage
{
   private JSplitPane jSplitPane = null;
   private JPanel jPanel = null;
   private JPanel jPanel1 = null;
   private JButton jButton = null;
   private JLabel jLabel = null;
   private JTextField jTextField = null;

   private static final long serialVersionUID = 6904031890115105296L;

   public DeviceStatus()
   {
      setLayout(null);
      BorderLayout borderLayout = new BorderLayout();
      setLayout(borderLayout);
      add(getJSplitPane(), BorderLayout.CENTER);

   }

   @Override
   public void setObject(Object o)
   {
      // TODO Auto-generated method stub

   }

   private JTextField getJTextField()
   {
      if (jTextField == null)
      {
         jTextField = new JTextField();
         jTextField.setBounds(new Rectangle(137, 6, 102, 20));
      }
      return jTextField;
   }

   private JLabel getJLabel()
   {
      jLabel = new JLabel();
      jLabel.setBounds(new Rectangle(22, 8, 107, 16));
      jLabel.setText("PhysicalAddress");
      return jLabel;
   }

   private JButton getJButton()
   {
      if (jButton == null)
      {
         jButton = new JButton();
         jButton.setBounds(new Rectangle(56, 44, 82, 21));
         jButton.setText("Read");
         jButton.addMouseListener(new java.awt.event.MouseAdapter()
         {
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
               startjob();
            }
         });
      }
      return jButton;
   }

   private JSplitPane getJSplitPane()
   {
      if (jSplitPane == null)
      {
         jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getJPanel(), getJPanel1());
         jSplitPane.setDividerLocation(80);

      }
      return jSplitPane;
   }

   private JPanel getJPanel()
   {

      if (jPanel == null)
      {
         jPanel = new JPanel();
         jPanel.setLayout(null);
         jPanel.add(getJButton());
         jPanel.add(getJLabel(), null);
         jPanel.add(getJTextField(), null);
         jPanel.add(getJButton(), null);

      }
      return jPanel;
   }

   /**
    * This method initializes jPanel1
    * 
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel1()
   {
      if (jPanel1 == null)
      {
         jPanel1 = new JPanel();
         jPanel1.setLayout(null);

      }
      return jPanel1;
   }

   private void startjob()
   {
      PhysicalAddress physicalAddress = new PhysicalAddress(3, 3, 7);

      ReadDeviceStatusJob readDeviceStatusJob = new ReadDeviceStatusJob(physicalAddress);
      readDeviceStatusJob.addMyListener(new JobStepStatusListner()
      {



         @Override
         public void JobStepStatus(JobStepStatusEvent e)
         {
         // TODO finish method

            for (JobStep a:  e.getJobSteps()){
           System.out.println("PageMsg: data resied " + a.getJobStepStatus().toString() +" " +a.toString() + " : "+a.getResivedApplication().toString());
            }
         }

      });
      try
      {
         readDeviceStatusJob.ReadStatus(physicalAddress);
      }
      catch (Exception e1)
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }

   }
}
