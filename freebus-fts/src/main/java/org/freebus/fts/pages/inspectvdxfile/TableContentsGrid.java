package org.freebus.fts.pages.inspectvdxfile;

import java.util.Comparator;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.freebus.fts.pages.InspectVdxFile;
import org.freebus.fts.persistence.vdx.VdxFieldType;
import org.freebus.fts.persistence.vdx.VdxSection;
import org.freebus.fts.persistence.vdx.VdxSectionHeader;
import org.freebus.fts.utils.TableUtils;
import org.hsqldb.lib.Sort.StringComparator;

/**
 * Displays the contents of a VDX table, with a list of records on the left side
 * and the fields of the selected record on the right side.
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
   private final TableRowSorter<? extends TableModel> sorter = new TableRowSorter<DefaultTableModel>(tmData);

   private final VdxNumberComparator compNumber = new VdxNumberComparator();
   private final VdxDoubleComparator compDouble = new VdxDoubleComparator();
   private final DefaultTableCellRenderer rendererNumber = new DefaultTableCellRenderer();

   /**
    * Create a table-contents widget.
    */
   public TableContentsGrid()
   {
      setName(getClass().getName());

      rendererNumber.setHorizontalAlignment(SwingConstants.RIGHT);
      
      tblData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      tblData.setRowSorter(sorter);

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
      // rowSorter.setFieldTypes(header.types);

      // Determine displayed number of records
      int numRecords = table.getNumElements();
      if (numRecords > maxRecords)
         numRecords = maxRecords;

      final Object data[][] = new Object[numRecords][];
      for (int row = 0; row < numRecords; ++row)
         data[row] = table.getElementValues(row);

      tmData.setDataVector(data, header.fields);
      TableUtils.pack(tblData, 2);

      // Set the comparators for the columns
      final TableColumnModel tcmData = tblData.getColumnModel();
      for (int col = header.types.length - 1; col >= 0; --col)
      {
         final VdxFieldType type = header.types[col];
         if (type == VdxFieldType.INTEGER || type == VdxFieldType.SHORT)
         {
            sorter.setComparator(col, compNumber);
            tcmData.getColumn(col).setCellRenderer(rendererNumber);
         }
         else if (type == VdxFieldType.FLOAT)
         {
            sorter.setComparator(col, compDouble);
            tcmData.getColumn(col).setCellRenderer(rendererNumber);
         }
      }
   }

   /**
    * Internal class for sorting the rows of a VDX table, depending on the
    * data-type of the sorted column.
    */
   class DataRowSorter extends TableRowSorter<DefaultTableModel>
   {
      private VdxFieldType[] fieldTypes;

      /**
       * Create a row-sorter.
       * 
       * @param model - the model that is sorted.
       */
      public DataRowSorter(DefaultTableModel model)
      {
         super(model);
      }

      /**
       * Set the field types of the table that shall be sorted.
       */
      public void setFieldTypes(final VdxFieldType[] fieldTypes)
      {
         this.fieldTypes = fieldTypes;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Comparator<?> getComparator(int column)
      {
         final VdxFieldType type = fieldTypes[column];
         if (type == VdxFieldType.INTEGER || type == VdxFieldType.SHORT)
         {
            return new Comparator<String>()
            {
               @Override
               public int compare(String a, String b)
               {
                  final int ia = a.isEmpty() ? 0 : Integer.parseInt(a);
                  final int ib = b.isEmpty() ? 0 : Integer.parseInt(b);
                  return ib - ia;
               }
            };
         }

         return (Comparator<?>) new StringComparator();
      }
   }

   static private class VdxNumberComparator implements Comparator<String>
   {
      @Override
      public int compare(String a, String b)
      {
         if (a.isEmpty() || b.isEmpty())
            return (a.isEmpty() ? 0 : 1) - (b.isEmpty() ? 0 : 1); 

         return Integer.parseInt(a) - Integer.parseInt(b);
      }
   }

   static private class VdxDoubleComparator implements Comparator<String>
   {
      @Override
      public int compare(String a, String b)
      {
         if (a.isEmpty() || b.isEmpty())
            return (a.isEmpty() ? 0 : 1) - (b.isEmpty() ? 0 : 1); 

         final double diff = Double.parseDouble(a) - Double.parseDouble(b);

         if (diff < 0) return -1;
         if (diff > 0) return 1;
         return 0;
      }
   }
}
