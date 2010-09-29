package org.freebus.fts.pages.deviceeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.freebus.fts.common.types.ObjectPriority;
import org.freebus.fts.core.I18n;
import org.freebus.fts.dragdrop.TransferableObject;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.SubGroupToObject;

/**
 * Displays / edits details about a {@link DeviceObject device object}.
 */
public class DeviceObjectPanel extends JPanel
{
   private static final long serialVersionUID = -4579589068706937561L;

   private final JLabel lblName = new JLabel();
   private final JLabel lblType = new JLabel();
   private final DefaultTableModel groupsModel = new DefaultTableModel();
   private final JTable groupsTable = new JTable(groupsModel);
   private final JButton addGroupButton = new JButton(ImageCache.getIcon("icons/sub-group-new"));
   private final JCheckBox transButton = new JCheckBox(I18n.getMessage("DeviceObjectPanel.ButtonTransmit"));
   private final JCheckBox readButton = new JCheckBox(I18n.getMessage("DeviceObjectPanel.ButtonRead"));
   private final JCheckBox writeButton = new JCheckBox(I18n.getMessage("DeviceObjectPanel.ButtonWrite"));
   private final JCheckBox commButton = new JCheckBox(I18n.getMessage("DeviceObjectPanel.ButtonComm"));
   private final JComboBox priorityCombo = new JComboBox();

   private final DeviceObject deviceObject;

   /**
    * Create a device-object panel.
    * 
    * @param comObject - the communication object to display.
    */
   public DeviceObjectPanel(DeviceObject deviceObject)
   {
      this.deviceObject = deviceObject;

      setLayout(new GridBagLayout());
      setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));

      final Insets noInsets = new Insets(0, 0, 0, 0);
      int gridy = -1;
      
      final JLabel iconLabel = new JLabel(ImageCache.getIcon("icons/com-object"));
      iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
      add(iconLabel, new GridBagConstraints(0, ++gridy, 1, 2, 0, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.VERTICAL, new Insets(0, 2, 0, 2), 0, 0));

      add(lblName, new GridBagConstraints(1, gridy, 2, 1, 100, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, noInsets, 0, 0));

      add(transButton, new GridBagConstraints(3, gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, noInsets, 0, 0));
      transButton.addActionListener(flagsButtonListener);
      transButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.ButtonTransmitTip"));

      add(readButton, new GridBagConstraints(4, gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, noInsets, 0, 0));
      readButton.addActionListener(flagsButtonListener);
      readButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.ButtonReadTip"));

      add(writeButton, new GridBagConstraints(5, gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, noInsets, 0, 0));
      writeButton.addActionListener(flagsButtonListener);
      writeButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.ButtonWriteTip"));

      add(commButton, new GridBagConstraints(6, gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, noInsets, 0, 0));
      commButton.addActionListener(flagsButtonListener);
      commButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.ButtonCommTip"));

      add(lblType, new GridBagConstraints(1, ++gridy, 2, 1, 100, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, noInsets, 0, 0));

      add(priorityCombo, new GridBagConstraints(4, gridy, 4, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, noInsets, 0, 0));
      priorityCombo.addItem(ObjectPriority.LOW);
      priorityCombo.addItem(ObjectPriority.HIGH);
      priorityCombo.addItem(ObjectPriority.ALARM);
      priorityCombo.addItem(ObjectPriority.SYSTEM);

      add(Box.createVerticalStrut(4), new GridBagConstraints(1, ++gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, noInsets, 0, 0));

      groupsModel.addColumn("<Group>");
//      groupsTable.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
      groupsTable.getTableHeader().setVisible(false);
      add(groupsTable, new GridBagConstraints(1, ++gridy, 6, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, noInsets, 0, 0));

      add(Box.createVerticalStrut(4), new GridBagConstraints(1, ++gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, noInsets, 0, 0));

//      addGroupButton.setBorderPainted(false);
      addGroupButton.setFocusPainted(false);
      addGroupButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.AddGroupButtonTip"));
      add(addGroupButton, new GridBagConstraints(1, ++gridy, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, noInsets, 0, 0));

//      add(new JPanel(), new GridBagConstraints(3, gridy, 1, 1, 100, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, noInsets, 0, 0));

      setDropTarget(new DropTarget(this, new DropTargetListener()
      {
         @Override
         public void dropActionChanged(DropTargetDragEvent dtde)
         {
         }
         
         @Override
         public void drop(DropTargetDropEvent dtde)
         {
            if (dtde.isDataFlavorSupported(TransferableObject.objectFlavor))
            {
               try
               {
                  @SuppressWarnings("unchecked")
                  final List<Object> objs = (List<Object>) dtde.getTransferable().getTransferData(TransferableObject.objectFlavor);
                  if (objectsDropped(objs) <= 0)
                     dtde.rejectDrop();
               }
               catch (Exception e)
               {
                  Dialogs.showExceptionDialog(e, I18n.getMessage("DeviceObjectPanel.DropFailed"));
                  e.printStackTrace();
               }
            }
            else dtde.rejectDrop();
            
         }
         
         @Override
         public void dragOver(DropTargetDragEvent dtde)
         {
         }
         
         @Override
         public void dragExit(DropTargetEvent dte)
         {
         }
         
         @Override
         public void dragEnter(DropTargetDragEvent dtde)
         {
            if (!dtde.isDataFlavorSupported(TransferableObject.objectFlavor))
               dtde.rejectDrag();
         }
      }));

      updateContents();
   }

   /**
    * Action listener that sets the communication flags of the device object.
    */
   private final ActionListener flagsButtonListener = new ActionListener()
   {
      @Override
      public void actionPerformed(ActionEvent e)
      {
         final Object btn = e.getSource();

         if (btn == transButton)
            deviceObject.setTrans(transButton.isSelected());
         else if (btn == readButton)
            deviceObject.setRead(readButton.isSelected());
         else if (btn == writeButton)
            deviceObject.setRead(writeButton.isSelected());
         else if (btn == commButton)
            deviceObject.setRead(commButton.isSelected());
      }
   };

   /**
    * Handle drop of a list of objects
    * 
    * @param objs - The objects that were dropped.
    * @return The number of objects dropped.
    */
   public int objectsDropped(final List<Object> objs)
   {
      boolean modified = false;
      int num = 0;

      for (Object obj : objs)
      {
         if (obj instanceof SubGroup)
         {
            final SubGroup subGroup = (SubGroup) obj;

            if (deviceObject.contains(subGroup))
            {
               Logger.getLogger(getClass()).warn(I18n.formatMessage("DeviceObjectPanel.GroupAlreadyAdded", new Object[] { subGroup.getGroupAddress() }));
            }
            else
            {
               groupsModelAdd(deviceObject.add(subGroup));
               ProjectManager.fireComponentModified(subGroup);
               modified = true;
               ++num;
            }
         }
      }

      if (modified)
      {
         groupsModel.fireTableDataChanged();
         ProjectManager.fireComponentModified(deviceObject);
      }

      return num;
   }
   
   /**
    * @return The communication object that is displayed.
    */
   public DeviceObject getDeviceObject()
   {
      return deviceObject;
   }

   /**
    * Add a subgroup-to-object to the groups model
    */
   private void groupsModelAdd(SubGroupToObject sgo)
   {
      groupsModel.addRow(new Object[] { sgo.getSubGroup() });
   }

   /**
    * Update the contents of the panel.
    */
   public void updateContents()
   {
      final CommunicationObject comObject = deviceObject.getComObject();

      lblName.setText(comObject.getName() + " - " + comObject.getFunction());
      lblType.setText(comObject.getObjectType().getName());

      final String debugTip = "debug: COM-" + comObject.getNumber() + " #" + comObject.getUniqueNumber();
      lblName.setToolTipText(debugTip);
      lblType.setToolTipText(debugTip);

      transButton.setSelected(deviceObject.isTrans());
      readButton.setSelected(deviceObject.isRead());
      writeButton.setSelected(deviceObject.isWrite());
      commButton.setSelected(deviceObject.isComm());
      priorityCombo.setSelectedItem(deviceObject.getObjectType());

      for (int i = groupsModel.getRowCount() - 1; i >= 0; --i)
         groupsModel.removeRow(i);

      for (final SubGroupToObject sgo : deviceObject.getSubGroupToObjects())
         groupsModelAdd(sgo);

      groupsModel.fireTableDataChanged();
   }
}
