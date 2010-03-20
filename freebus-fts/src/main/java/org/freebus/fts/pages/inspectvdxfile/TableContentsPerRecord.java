package org.freebus.fts.pages.inspectvdxfile;

import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.freebus.fts.components.SortedListModel;
import org.freebus.fts.core.I18n;
import org.freebus.fts.pages.InspectVdxFile;
import org.freebus.fts.persistence.vdx.VdxSection;
import org.freebus.fts.persistence.vdx.VdxSectionHeader;
import org.freebus.fts.utils.TableUtils;

/**
 * Displays the contents of a VDX table, with a list of records on the left side
 * and the fields of the selected record on the right side.
 *
 * This is an internal class of {@link InspectVdxFile}.
 */
public class TableContentsPerRecord extends JSplitPane implements TableContents
{
   private static final long serialVersionUID = -3850390134630129910L;

   private DefaultListModel lmRecords = new DefaultListModel();
   private SortedListModel slmRecords = new SortedListModel(lmRecords);
   private final JList lstRecords = new JList(slmRecords);

   private final DefaultTableModel tbmFields = new DefaultTableModel();
   private final JTable tblFields = new JTable(tbmFields);
   private String selectedTableName;

   private VdxSection table;
   private int maxRecords = 1000;

   /**
    * Create a table-contents widget.
    */
   public TableContentsPerRecord()
   {
      super(JSplitPane.HORIZONTAL_SPLIT);

      setName(getClass().getName());
      setResizeWeight(0.25);
      setDividerLocation(250);

      lstRecords.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent ev)
         {
            updateFields();
         }
      });

      add(new JScrollPane(lstRecords));

      final JScrollPane scpFields = new JScrollPane(tblFields);
      scpFields.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      add(scpFields);

      final String[] columnNames = new String[] { I18n.getMessage("InspectVdxFile.KeyColumn"),
            I18n.getMessage("InspectVdxFile.ValueColumn") };
      tbmFields.setColumnIdentifiers(columnNames);
      tblFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setTable(VdxSection table, int maxRecords)
   {
      if (table == this.table && maxRecords == this.maxRecords)
         return;

      this.table = table;
      this.maxRecords = maxRecords;

      updateContents();
   }

   /**
    * Update the contents.
    */
   private void updateContents()
   {
      lmRecords.clear();

      final VdxSectionHeader header = table.getHeader();

      // Find a suitable key field
      int keyIdx = header.getIndexOf(selectedTableName + "_id");
      int nameIdx = -1;
      final int numFields = header.fields.length;
      for (int idx = 0; idx < numFields && (keyIdx < 0 || nameIdx < 0); ++idx)
      {
         final String fieldName = header.fields[idx].toLowerCase();

         if (nameIdx < 0)
         {
            if (fieldName.endsWith("_name"))
               nameIdx = idx;
            else if (fieldName.endsWith("text"))
               nameIdx = idx;
            else if (fieldName.startsWith("display"))
               nameIdx = idx;
         }

         if (keyIdx < 0 && fieldName.endsWith("_id"))
            keyIdx = idx;
      }
      if (keyIdx < 0)
         keyIdx = 0;

      // Determine displayed number of records
      int numRecords = table.getNumElements();
      if (numRecords > maxRecords)
         numRecords = maxRecords;

      // fill the list
      lmRecords = new DefaultListModel();
      lmRecords.setSize(numRecords);
      for (int i = 0; i < numRecords; ++i)
      {
         final String keyStr = table.getValue(i, keyIdx);
         if (nameIdx >= 0)
            lmRecords.set(i, table.getValue(i, nameIdx) + " [" + keyStr + ']');
         else lmRecords.set(i, keyStr);
      }

      slmRecords = new SortedListModel(lmRecords);
      lstRecords.setModel(slmRecords);

      if (numRecords > 0)
         lstRecords.setSelectedIndex(0);

      updateFields();
   }

   /**
    * Update the list of record fields of the currently selected record.
    *
    * @throws IOException if there is an error reading the VD_ file.
    */
   public void updateFields()
   {
      final int recordIdx = getSelectedRecordIndex();
      if (recordIdx < 0)
      {
         tbmFields.setNumRows(0);
         return;
      }

      final VdxSection section = table;
      final VdxSectionHeader header = table.getHeader();

      final int numFields = header.fields.length;
      tbmFields.setNumRows(numFields);

      for (int fieldIdx = 0; fieldIdx < numFields; ++fieldIdx)
      {
         tbmFields.setValueAt(header.fields[fieldIdx], fieldIdx, 0);
         tbmFields.setValueAt(section.getValue(recordIdx, fieldIdx), fieldIdx, 1);
      }

      TableUtils.pack(tblFields, 2);
   }

   /**
    * @return the index of the selected record, or -1 if no record is selected.
    */
   public int getSelectedRecordIndex()
   {
      final int sortedIdx = lstRecords.getSelectedIndex();
      if (sortedIdx < 0)
         return -1;
      return slmRecords.toUnsortedModelIndex(sortedIdx);
   }
}
