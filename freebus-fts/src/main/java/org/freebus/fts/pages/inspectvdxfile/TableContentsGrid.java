package org.freebus.fts.pages.inspectvdxfile;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.freebus.fts.pages.InspectVdxFile;
import org.freebus.fts.persistence.vdx.VdxSection;
import org.freebus.fts.persistence.vdx.VdxSectionHeader;
import org.freebus.fts.utils.TableUtils;

/**
 * Displays the contents of a VDX table, with a list of records on the
 * left side and the fields of the selected record on the right side.
 *
 * This is an internal class of {@link InspectVdxFile}.
 */
public class TableContentsGrid extends JScrollPane implements TableContents
{
   private static final long serialVersionUID = -3850390134630129910L;
   
   private VdxSection table;
   private int maxRecords = 1000;

   private final DefaultTableModel tmData = new DefaultTableModel();
   private final JTable tblData = new JTable(tmData);

   /**
    * Create a table-contents widget.
    */
   public TableContentsGrid()
   {
      setName(getClass().getName());

      tblData.setAutoCreateRowSorter(true);
      tblData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      setViewportView(tblData);
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
      tmData.setRowCount(0);

      if (table == null)
         return;

      final VdxSectionHeader header = table.getHeader();

      // Determine displayed number of records
      int numRecords = table.getNumElements();
      if (numRecords > maxRecords)
         numRecords = maxRecords;

      final Object data[][] = new Object[numRecords][];
      for (int row = 0; row < numRecords; ++row)
         data[row] = table.getElementValues(row);

      tmData.setDataVector(data, header.fields);
      TableUtils.pack(tblData, 2);
//      tblData.setModel(tmData);
   }
}
