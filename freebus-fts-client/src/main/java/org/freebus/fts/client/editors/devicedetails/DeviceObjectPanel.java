package org.freebus.fts.client.editors.devicedetails;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.dragdrop.TransferableObject;
import org.freebus.fts.client.views.LogicalView;
import org.freebus.fts.common.types.ObjectPriority;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.SubGroupToObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays / edits details about a {@link DeviceObject device object}.
 */
public class DeviceObjectPanel extends JPanel
{
   private static final Logger LOGGER = LoggerFactory.getLogger(DeviceObjectPanel.class);
   private static final long serialVersionUID = -4579589068706937561L;

   private final JLabel lblName = new JLabel();
   private final JLabel lblType = new JLabel();
   private final DefaultTableModel groupsModel = new DefaultTableModel();
   private final JTable groupsTable = new JTable(groupsModel)
      {
         public boolean isCellEditable(int rowIndex, int colIndex)
         {
            if (colIndex == 0)
              return false;
            return true;
         }
      };
   private final JButton addGroupButton = new JButton(ImageCache.getIcon("icons/sub-group-new"));
   private final JCheckBox transButton = new JCheckBox(I18n.getMessage("DeviceObjectPanel.ButtonTransmit"));
   private final JCheckBox readButton = new JCheckBox(I18n.getMessage("DeviceObjectPanel.ButtonRead"));
   private final JCheckBox writeButton = new JCheckBox(I18n.getMessage("DeviceObjectPanel.ButtonWrite"));
   private final JCheckBox commButton = new JCheckBox(I18n.getMessage("DeviceObjectPanel.ButtonComm"));
   private final JComboBox priorityCombo = new JComboBox();

   private final DeviceObject deviceObject;


   class ButtonRenderer extends JButton implements TableCellRenderer
   {

      public ButtonRenderer()
      {
         setOpaque(true);
      }

      public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)
      {
         if (isSelected)
         {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
         }
         else
         {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
         }
         setText((value == null) ? "" : value.toString());
         setIcon(ImageCache.getIcon("icons/delete"));
         setToolTipText(I18n.getMessage("DeviceObjectPanel.DeleteGroup"));
         return this;
      }
   }


   class ButtonEditor extends DefaultCellEditor
   {

      protected JButton button;
      private String label;
      private boolean isPushed;

      public ButtonEditor(JCheckBox checkBox) {
         super(checkBox);
         button = new JButton();
         button.setOpaque(true);
         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               fireEditingStopped();
            }
         });
      }

      public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column)
      {
         if (isSelected)
         {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
         }
         else
         {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
         }
         label = (value == null) ? "" : value.toString();
         button.setText(label);
         button.setIcon(ImageCache.getIcon("icons/delete"));
         isPushed = true;
         return button;
      }

      public Object getCellEditorValue()
      {
         return new String(label);
      }

      public boolean stopCellEditing()
      {
         isPushed = false;
         return super.stopCellEditing();
      }

      protected void fireEditingStopped()
      {
         super.fireEditingStopped();
         if (isPushed)
            removeSubGroup(groupsTable.getSelectedRow(), true);
            // JOptionPane.showMessageDialog(button, "Ouch: " + groupsTable.getSelectedRow());
         isPushed = false;
      }
   }


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
      add(iconLabel, new GridBagConstraints(0, ++gridy, 1, 2, 0, 1, GridBagConstraints.NORTHEAST,
            GridBagConstraints.VERTICAL, new Insets(0, 2, 0, 2), 0, 0));

      add(lblName, new GridBagConstraints(1, gridy, 2, 1, 100, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, noInsets, 0, 0));

      add(transButton, new GridBagConstraints(3, gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            noInsets, 0, 0));
      transButton.addActionListener(flagsButtonListener);
      transButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.ButtonTransmitTip"));

      add(readButton, new GridBagConstraints(4, gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            noInsets, 0, 0));
      readButton.addActionListener(flagsButtonListener);
      readButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.ButtonReadTip"));

      add(writeButton, new GridBagConstraints(5, gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            noInsets, 0, 0));
      writeButton.addActionListener(flagsButtonListener);
      writeButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.ButtonWriteTip"));

      add(commButton, new GridBagConstraints(6, gridy, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
            noInsets, 0, 0));
      commButton.addActionListener(flagsButtonListener);
      commButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.ButtonCommTip"));

      add(lblType, new GridBagConstraints(1, ++gridy, 2, 1, 100, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, noInsets, 0, 0));

      add(priorityCombo, new GridBagConstraints(4, gridy, 4, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, noInsets, 0, 0));
      priorityCombo.addItem(ObjectPriority.LOW);
      priorityCombo.addItem(ObjectPriority.HIGH);
      priorityCombo.addItem(ObjectPriority.ALARM);
      priorityCombo.addItem(ObjectPriority.SYSTEM);

      add(Box.createVerticalStrut(4), new GridBagConstraints(1, ++gridy, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.NONE, noInsets, 0, 0));

      groupsModel.addColumn("<Group>");
      groupsModel.addColumn("<Button>");
      // groupsTable.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
      groupsTable.getTableHeader().setVisible(false);
      groupsTable.setRowSelectionAllowed(false);
      groupsTable.getColumn("<Button>").setCellRenderer(new ButtonRenderer());
      groupsTable.getColumn("<Button>").setCellEditor(new ButtonEditor(new JCheckBox()));
      groupsTable.getColumn("<Button>").setPreferredWidth(30);
      groupsTable.getColumn("<Button>").setMaxWidth(30);
      groupsTable.getColumn("<Button>").setWidth(30);

      add(groupsTable, new GridBagConstraints(1, ++gridy, 6, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, noInsets, 0, 0));

      add(Box.createVerticalStrut(4), new GridBagConstraints(1, ++gridy, 1, 1, 1, 1, GridBagConstraints.WEST,
            GridBagConstraints.NONE, noInsets, 0, 0));

      // addGroupButton.setBorderPainted(false);
      addGroupButton.setFocusPainted(false);
      addGroupButton.setToolTipText(I18n.getMessage("DeviceObjectPanel.AddGroupButtonTip"));
      addGroupButton.addActionListener(addGroupListener);
      add(addGroupButton, new GridBagConstraints(1, ++gridy, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
            GridBagConstraints.NONE, noInsets, 0, 0));

      // add(new JPanel(), new GridBagConstraints(3, gridy, 1, 1, 100, 1,
      // GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, noInsets,
      // 0, 0));

      setDropTarget(new DropTarget(this, new DropTargetAdapter()
      {
         @Override
         public void drop(DropTargetDropEvent dtde)
         {
            if (dtde.isDataFlavorSupported(TransferableObject.objectFlavor))
            {
               try
               {
                  @SuppressWarnings("unchecked")
                  final List<Object> objs = (List<Object>) dtde.getTransferable().getTransferData(
                        TransferableObject.objectFlavor);
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
         public void dragEnter(DropTargetDragEvent dtde)
         {
            if (!dtde.isDataFlavorSupported(TransferableObject.objectFlavor))
               dtde.rejectDrag();
         }
      }));

      updateContents();
   }

   /**
    * Action listener for the add-group button.
    */
   private final ActionListener addGroupListener = new ActionListener()
   {
      @Override
      public void actionPerformed(ActionEvent e)
      {
         final LogicalView logicalView = (LogicalView) MainWindow.getInstance().findPanel(LogicalView.class);
         if (logicalView == null)
            return;

         final Object obj = logicalView.getSelectedObject();
         if (obj instanceof SubGroup)
         {
            addSubGroup((SubGroup) obj, true);
         }
      }
   };

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
            deviceObject.setTransEnabled(transButton.isSelected());
         else if (btn == readButton)
            deviceObject.setReadEnabled(readButton.isSelected());
         else if (btn == writeButton)
            deviceObject.setReadEnabled(writeButton.isSelected());
         else if (btn == commButton)
            deviceObject.setReadEnabled(commButton.isSelected());
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
            if (addSubGroup((SubGroup) obj, false))
            {
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
    * Add a sub-group to the device object.
    * 
    * @param subGroup - the sub-group to add
    * @param update - shall the device object be updated?
    * @return True if the sub-group was added, false if there was an error.
    */
   private boolean addSubGroup(final SubGroup subGroup, boolean update)
   {
      if (deviceObject.contains(subGroup))
      {
         LOGGER.warn(I18n.formatMessage("DeviceObjectPanel.GroupAlreadyAdded", subGroup.getGroupAddress().toString()));
         return false;
      }

      groupsModelAdd(deviceObject.add(subGroup));
      ProjectManager.fireComponentModified(subGroup);

      if (update)
      {
         groupsModel.fireTableDataChanged();
         ProjectManager.fireComponentModified(deviceObject);
      }

      return true;
   }

   /**
    * Remove a sub-group from the device object.
    *
    * @param index - the index of the sub-group to remove
    * @param update - shall the device object be updated?
    * @return True if the sub-group was removed, false if there was an error.
    */
   private boolean removeSubGroup(int index, boolean update)
   {
      SubGroupToObject sgo = (SubGroupToObject) deviceObject.getSubGroupToObjects().toArray()[index];
      SubGroup subGroup = sgo.getSubGroup();
      sgo.dispose();
      groupsModel.removeRow(index);
      ProjectManager.fireComponentRemoved(sgo);
      ProjectManager.fireComponentModified(subGroup);

      if (update)
      {
         groupsModel.fireTableDataChanged();
         ProjectManager.fireComponentModified(deviceObject);
      }

      return true;
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
      groupsModel.addRow(new Object[] { sgo.getSubGroup(), "" });
   }

   /**
    * Update the contents of the panel.
    */
   public void updateContents()
   {
      final CommunicationObject comObject = deviceObject.getComObject();

      lblName.setText(comObject.getName() + " - " + comObject.getFunction());
      lblType.setText(comObject.getType().getName());

      final String debugTip = "debug: COM-" + comObject.getNumber() + " #" + comObject.getUniqueNumber();
      lblName.setToolTipText(debugTip);
      lblType.setToolTipText(debugTip);

      transButton.setSelected(deviceObject.isTransEnabled());
      readButton.setSelected(deviceObject.isReadEnabled());
      writeButton.setSelected(deviceObject.isWriteEnabled());
      commButton.setSelected(deviceObject.isCommEnabled());
      priorityCombo.setSelectedItem(deviceObject.getType());

      for (int i = groupsModel.getRowCount() - 1; i >= 0; --i)
         groupsModel.removeRow(i);

      for (final SubGroupToObject sgo : deviceObject.getSubGroupToObjects())
         groupsModelAdd(sgo);

      groupsModel.fireTableDataChanged();
   }
}
