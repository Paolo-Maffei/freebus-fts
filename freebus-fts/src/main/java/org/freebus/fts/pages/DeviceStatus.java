package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.event.MouseAdapter;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;


import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.jobs.ReadDeviceStatusJob;
import org.freebus.knxcomm.application.ADCResponse;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.MemoryResponse;
import org.freebus.knxcomm.jobs.JobStep;
import org.freebus.knxcomm.jobs.JobStepStatusEvent;
import org.freebus.knxcomm.jobs.JobStepStatusListner;

public class DeviceStatus extends AbstractPage {
	private JSplitPane jSplitPane = null;
	private JPanel jPanel = null;
	private JPanel JPanelOutput = null;
	private JButton jButton = null;
	private JLabel jLabel = null;
	private JTextField jTextField = null;

	private static final long serialVersionUID = 6904031890115105296L;

	public DeviceStatus() {
		setLayout(null);
		BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		add(getJSplitPane(), BorderLayout.CENTER);

	}

	@Override
	public void setObject(Object o) {
		// TODO Auto-generated method stub

	}

	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new Rectangle(137, 6, 102, 20));
		}
		return jTextField;
	}

	private JLabel getJLabel() {
		jLabel = new JLabel();
		jLabel.setBounds(new Rectangle(22, 8, 107, 16));
		jLabel.setText("PhysicalAddress");
		return jLabel;
	}

	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(56, 44, 82, 21));
			jButton.setText("Read");
			jButton.addMouseListener(new MouseAdapter() {
				@Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

					startjob(PhysicalAddress.valueOf( jTextField.getText()));
				}
			});
		}
		return jButton;
	}

	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getJPanel(),
					getJPanelOutput());
			jSplitPane.setDividerLocation(80);

		}
		return jSplitPane;
	}

	private JPanel getJPanel() {

		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(getJButton());
			jPanel.add(getJLabel(), null);
			jPanel.add(getJTextField(), null);
			jPanel.add(getJButton(), null);

		}
		return jPanel;
	}


	private JPanel getJPanelOutput() {
		if (JPanelOutput == null) {
			JPanelOutput = new JPanel();

		}
		return JPanelOutput;
	}

	private void startjob(PhysicalAddress physicalAddress) {


		ReadDeviceStatusJob readDeviceStatusJob = new ReadDeviceStatusJob(
				physicalAddress);
		readDeviceStatusJob.addMyListener(new JobStepStatusListner() {



			@Override
			public void JobStepStatus(JobStepStatusEvent e) {
				// TODO finish method
				int[] data;
				String s = "";
				String msg, allmsg="";
				for (JobStep a : e.getJobSteps()) {
					s = "";
					Application application = a.getResivedApplication();

					if (application instanceof MemoryResponse) {
						MemoryResponse b = (MemoryResponse) application;
						data = b.getData();
						for (int i : data)
							s = s + String.format(" %02X", i);
					}

					if (application instanceof ADCResponse) {
						ADCResponse b = (ADCResponse) application;
						int x = b.getValue();
						s = (new Integer(x)).toString();
					}

					msg = "PageMsg data received: "
							+ a.getJobStepStatus().toString()
							+ " for request :" + a.toString() + " : Data " + s;
					//System.out.println(msg);
					allmsg = allmsg + msg + "\n";
				}
				JPanelOutput.add(getTextArea(allmsg));
				System.out.print(allmsg);
			}

		});
		try {
			readDeviceStatusJob.ReadStatus(physicalAddress);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private TextArea getTextArea(String text){
		TextArea textArea = new TextArea();
		textArea.setText(text);
		return textArea;
	}


}
