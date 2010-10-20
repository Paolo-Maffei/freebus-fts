package org.freebus.fts.components;

import java.awt.Color;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freebus.fts.I18n;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceProgramming;

/**
 * Display the {@link DeviceProgramming} object of a device.
 */
public class DeviceProgrammingPanel extends JPanel
{
   private static final long serialVersionUID = -5561642620901132614L;

   private final Color colorNever = new Color(128, 128, 128);
   private final Color colorValid = new Color(80, 160, 80);
   private final Color colorInvalid = new Color(225, 180, 128);

   private Device device;

   private final JLabel lastModifiedLabel = new JLabel();
   private final JLabel lastUploadLabel = new JLabel();
   private final JLabel communicationLabel = new JLabel();
   private final JLabel parametersLabel = new JLabel();
   private final JLabel physicalAddressLabel = new JLabel();
   private final JLabel programLabel = new JLabel();

   /**
    * Create a device programming details panel.
    */
   public DeviceProgrammingPanel()
   {
      super(new GridLayout(8, 2));

      addTitled(I18n.getMessage("DeviceProgrammingPanel.PhysicalAddress"), physicalAddressLabel);
      addTitled(I18n.getMessage("DeviceProgrammingPanel.Program"), programLabel);
      addTitled(I18n.getMessage("DeviceProgrammingPanel.Parameters"), parametersLabel);
      addTitled(I18n.getMessage("DeviceProgrammingPanel.Communication"), communicationLabel);
      add(Box.createVerticalStrut(0));
      add(Box.createVerticalStrut(0));
      addTitled(I18n.getMessage("DeviceProgrammingPanel.LastModified"), lastModifiedLabel);
      addTitled(I18n.getMessage("DeviceProgrammingPanel.LastUpload"), lastUploadLabel);
   }

   /**
    * Add a component with a title to the panel.
    * 
    * @param title - the title to add.
    * @param component - the component to add.
    */
   private void addTitled(String title, JComponent component)
   {
      final JLabel lbl = new JLabel(title);
      lbl.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
      add(lbl);

      component.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
      add(component);
   }

   /**
    * Set the device to be displayed.
    */
   public void setDevice(Device device)
   {
      this.device = device;
      updateContents();
   }

   /**
    * @return The device to be displayed.
    */
   public Device getDevice()
   {
      return device;
   }

   /**
    * Update the contents of the panel.
    */
   public void updateContents()
   {
      if (device == null || !device.hasProgramming())
      {
         final String neverStr = I18n.getMessage("DeviceProgrammingPanel.Unknown");
         lastModifiedLabel.setText(neverStr);
         lastModifiedLabel.setForeground(colorNever);
         lastUploadLabel.setText(neverStr);
         lastUploadLabel.setForeground(colorNever);

         updateLabel(null, communicationLabel);
         updateLabel(null, parametersLabel);
         updateLabel(null, physicalAddressLabel);
         updateLabel(null, programLabel);

         return;
      }

      final DeviceProgramming progr = device.getProgramming();
      final DateFormat dateFmt = DateFormat.getDateTimeInstance();

      Date date = progr.getLastModified();
      lastModifiedLabel.setText(date != null ? dateFmt.format(date) : I18n.getMessage("DeviceProgrammingPanel.Unknown"));
      lastModifiedLabel.setForeground(date != null ? getForeground() : colorNever);

      date = progr.getLastUpload();
      lastUploadLabel.setText(date != null ? dateFmt.format(date) : I18n.getMessage("DeviceProgrammingPanel.Unknown"));
      lastUploadLabel.setForeground(date != null ? getForeground() : colorNever);

      updateLabel(progr.isCommunicationValid(), communicationLabel);
      updateLabel(progr.isParametersValid(), parametersLabel);
      updateLabel(progr.isPhysicalAddressValid(), physicalAddressLabel);
      updateLabel(progr.isProgramValid(), programLabel);
   }

   /**
    * Update a is-valid label.
    * 
    * @param valid - the valid flag.
    * @param label - the label to update.
    */
   private void updateLabel(Boolean valid, JLabel label)
   {
      if (valid == null)
      {
         label.setForeground(colorNever);
         label.setText(I18n.getMessage("DeviceProgrammingPanel.Unknown"));
      }
      else if (valid)
      {
         label.setForeground(colorValid);
         label.setText(I18n.getMessage("DeviceProgrammingPanel.Valid"));
      }
      else
      {
         label.setForeground(colorInvalid);
         label.setText(I18n.getMessage("DeviceProgrammingPanel.Invalid"));
      }
   }
}
