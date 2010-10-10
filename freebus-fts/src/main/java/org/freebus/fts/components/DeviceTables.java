package org.freebus.fts.components;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.freebus.fts.I18n;
import org.freebus.fts.backend.KNXDeviceAdapter;
import org.freebus.fts.backend.memory.AssociationTableEntry;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.elements.utils.TableUtils;

/**
 * Displays the tables of a device: the group addresses, the communication objects,
 * the association table.
 */
public class DeviceTables extends JPanel
{
   private static final long serialVersionUID = 3586461856829202482L;

   private DefaultTableModel groupsModel = new DefaultTableModel();
   private final JTable groupsTable = new JTable(groupsModel);
   private final JScrollPane groupsScrollPane = new JScrollPane(groupsTable);

   private DefaultTableModel associationsModel = new DefaultTableModel();
   private final JTable associationsTable = new JTable(associationsModel);
   private final JScrollPane associationsScrollPane = new JScrollPane(associationsTable);

   private DefaultTableModel objectsModel = new DefaultTableModel();
   private final JTable objectsTable = new JTable(objectsModel);
   private final JScrollPane objectsScrollPane = new JScrollPane(objectsTable);

   private KNXDeviceAdapter deviceAdapter;

   /**
    * Create a device tables object.
    */
   public DeviceTables()
   {
      super();
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

      groupsModel.addColumn(I18n.getMessage("DeviceTables.ColNumber"));
      groupsModel.addColumn(I18n.getMessage("DeviceTables.ColGroup"));
      groupsScrollPane.setBorder(BorderFactory.createTitledBorder(I18n.getMessage("DeviceTables.GroupsTable")));
      add(groupsScrollPane);

      associationsTable.setBorder(BorderFactory.createTitledBorder(I18n.getMessage("DeviceTables.AssociationsTable")));
      associationsModel.addColumn(I18n.getMessage("DeviceTables.ColGroupId"));
      associationsModel.addColumn(I18n.getMessage("DeviceTables.ColComObjectId"));
      associationsScrollPane.setBorder(BorderFactory.createTitledBorder(I18n.getMessage("DeviceTables.AssociationsTable")));
      add(associationsScrollPane);

      objectsModel.addColumn(I18n.getMessage("DeviceTables.ColNumber"));
      objectsModel.addColumn(I18n.getMessage("DeviceTables.ColComObjectType"));
      objectsModel.addColumn(I18n.getMessage("DeviceTables.ColComObjectFlags"));
      objectsModel.addColumn(I18n.getMessage("DeviceTables.ColComObjectPrio"));
      objectsScrollPane.setBorder(BorderFactory.createTitledBorder(I18n.getMessage("DeviceTables.ObjectsTable")));
      add(objectsScrollPane);
   }

   /**
    * Set the device adapter of the device to display. Calls {@link #updateContents()}.
    * 
    * @param deviceAdapter - the KNX device adapter to set.
    */
   public void setDeviceAdapter(KNXDeviceAdapter deviceAdapter)
   {
      this.deviceAdapter = deviceAdapter;
      updateContents();
   }

   /**
    * Update the contents of the panel. Call when the device has been changed.
    */
   public void updateContents()
   {
      if (deviceAdapter == null)
      {
         groupsModel.setNumRows(0);
         associationsModel.setNumRows(0);
         objectsModel.setNumRows(0);
         return;
      }

      deviceAdapter.deviceChanged();

      final GroupAddress[] groups = deviceAdapter.getGroupAddresses();
      groupsModel.setNumRows(groups.length);
      for (int i = 0; i < groups.length; ++i)
      {
         groupsModel.setValueAt(i, i, 0);
         groupsModel.setValueAt(groups[i], i, 1);
      }

      final AssociationTableEntry[] associations = deviceAdapter.getAssociationTable();
      associationsModel.setNumRows(associations.length);
      for (int i = 0; i < associations.length; ++i)
      {
         groupsModel.setValueAt(associations[i].getConnectionIndex(), i, 0);
         groupsModel.setValueAt(associations[i].getDeviceObjectIndex(), i, 1);
      }

      final ObjectDescriptor[] descriptors = deviceAdapter.getObjectDescriptors();
      objectsModel.setNumRows(descriptors.length);
      for (int i = 0; i < descriptors.length; ++i)
      {
         objectsModel.setValueAt(i, i, 0);
         objectsModel.setValueAt(descriptors[i].getType().toString(), i, 1);
         objectsModel.setValueAt("", i, 2);
         objectsModel.setValueAt(descriptors[i].getPriority().toString(), i, 3);
      }

      TableUtils.pack(groupsTable, 2);
      TableUtils.pack(associationsTable, 2);
      TableUtils.pack(objectsTable, 2);
   }
}
