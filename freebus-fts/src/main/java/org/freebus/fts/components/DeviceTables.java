package org.freebus.fts.components;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.freebus.fts.I18n;
import org.freebus.fts.backend.DeviceController;
import org.freebus.fts.backend.memory.AssociationTableEntry;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.elements.components.ReadOnlyTable;
import org.freebus.fts.elements.utils.TableUtils;

/**
 * Displays the tables of a device: the group addresses, the communication
 * objects, the association table.
 */
public class DeviceTables extends JPanel
{
   private static final long serialVersionUID = 3586461856829202482L;

   private DefaultTableModel groupsModel = new DefaultTableModel();
   private final JTable groupsTable = new ReadOnlyTable(groupsModel);
   private final JScrollPane groupsScrollPane = new JScrollPane(groupsTable);

   private DefaultTableModel associationsModel = new DefaultTableModel();
   private final JTable associationsTable = new ReadOnlyTable(associationsModel);
   private final JScrollPane associationsScrollPane = new JScrollPane(associationsTable);

   private DefaultTableModel objectsModel = new DefaultTableModel();
   private final JTable objectsTable = new ReadOnlyTable(objectsModel);
   private final JScrollPane objectsScrollPane = new JScrollPane(objectsTable);

   private DeviceController deviceAdapter;
   private boolean updatingSelections;

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
      groupsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            if (!updatingSelections)
            {
               updatingSelections = true;
               groupsSelectionChanged();
               updatingSelections = false;
            }
         }
      });

      associationsModel.addColumn(I18n.getMessage("DeviceTables.ColGroupId"));
      associationsModel.addColumn(I18n.getMessage("DeviceTables.ColComObjectId"));
      associationsScrollPane.setBorder(BorderFactory.createTitledBorder(I18n
            .getMessage("DeviceTables.AssociationsTable")));
      add(associationsScrollPane);
      associationsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            if (!updatingSelections)
            {
               updatingSelections = true;
               associationsSelectionChanged();
               updatingSelections = false;
            }
         }
      });

      objectsModel.addColumn(I18n.getMessage("DeviceTables.ColNumber"));
      objectsModel.addColumn(I18n.getMessage("DeviceTables.ColComObjectType"));
      objectsModel.addColumn(I18n.getMessage("DeviceTables.ColComObjectFlags"));
      objectsModel.addColumn(I18n.getMessage("DeviceTables.ColComObjectPrio"));
      objectsScrollPane.setBorder(BorderFactory.createTitledBorder(I18n.getMessage("DeviceTables.ObjectsTable")));
      add(objectsScrollPane);
      objectsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            if (!updatingSelections)
            {
               updatingSelections = true;
               objectsSelectionChanged();
               updatingSelections = false;
            }
         }
      });
   }

   /**
    * Set the device adapter of the device to display. Calls
    * {@link #updateContents()}.
    * 
    * @param deviceAdapter - the KNX device adapter to set.
    */
   public void setDeviceAdapter(DeviceController deviceAdapter)
   {
      this.deviceAdapter = deviceAdapter;
      updateContents();
   }

   /**
    * Update the contents of the panel. Call when the device has been changed.
    */
   public synchronized void updateContents()
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
         final AssociationTableEntry ae = associations[i];
         associationsModel.setValueAt(ae == null ? "" : ae.getConnectionIndex(), i, 0);
         associationsModel.setValueAt(ae == null ? "" : ae.getDeviceObjectIndex(), i, 1);
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

   /**
    * The selection in the groups table has changed.
    */
   private synchronized void groupsSelectionChanged()
   {
      final ListSelectionModel groupsSelModel = groupsTable.getSelectionModel();
      final ListSelectionModel assocSelModel = associationsTable.getSelectionModel();
      final ListSelectionModel objectsSelModel = objectsTable.getSelectionModel();

      assocSelModel.clearSelection();
      objectsSelModel.clearSelection();

      for (int i = associationsModel.getRowCount() - 1; i >= 0; --i)
      {
         final Object obj = associationsModel.getValueAt(i, 0); 
         if (!(obj instanceof Integer))
            continue;

         final Integer groupIdx = (Integer) obj;
         if (groupIdx == null || !groupsSelModel.isSelectedIndex(groupIdx))
            continue;

         assocSelModel.addSelectionInterval(i, i);

         final Integer objectIdx = (Integer) associationsModel.getValueAt(i, 1);
         objectsSelModel.addSelectionInterval(objectIdx, objectIdx);
      }
   }

   /**
    * The selection in the associations table has changed.
    */
   private synchronized void associationsSelectionChanged()
   {
      final ListSelectionModel groupsSelModel = groupsTable.getSelectionModel();
      final ListSelectionModel assocSelModel = associationsTable.getSelectionModel();
      final ListSelectionModel objectsSelModel = objectsTable.getSelectionModel();

      groupsSelModel.clearSelection();
      objectsSelModel.clearSelection();

      for (int i = associationsModel.getRowCount() - 1; i >= 0; --i)
      {
         if (!assocSelModel.isSelectedIndex(i))
            continue;

         if (!(associationsModel.getValueAt(i, 0) instanceof Integer))
            continue;

         final Integer groupIdx = (Integer) associationsModel.getValueAt(i, 0);
         final Integer objectIdx = (Integer) associationsModel.getValueAt(i, 1);

         groupsSelModel.addSelectionInterval(groupIdx, groupIdx);
         objectsSelModel.addSelectionInterval(objectIdx, objectIdx);
      }
   }

   /**
    * The selection in the device objects table has changed.
    */
   private synchronized void objectsSelectionChanged()
   {
      final ListSelectionModel groupsSelModel = groupsTable.getSelectionModel();
      final ListSelectionModel assocSelModel = associationsTable.getSelectionModel();
      final ListSelectionModel objectsSelModel = objectsTable.getSelectionModel();

      groupsSelModel.clearSelection();
      assocSelModel.clearSelection();

      for (int i = associationsModel.getRowCount() - 1; i >= 0; --i)
      {
         if (!(associationsModel.getValueAt(i, 0) instanceof Integer))
            continue;

         final Integer objectIdx = (Integer) associationsModel.getValueAt(i, 1);
         if (objectIdx == null || !objectsSelModel.isSelectedIndex(objectIdx))
            continue;

         assocSelModel.addSelectionInterval(i, i);

         final Integer groupIdx = (Integer) associationsModel.getValueAt(i, 0);
         groupsSelModel.addSelectionInterval(groupIdx, groupIdx);
      }
   }
}
