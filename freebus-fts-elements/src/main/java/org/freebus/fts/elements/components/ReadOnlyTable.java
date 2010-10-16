package org.freebus.fts.elements.components;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A {@link JTable} that disallows editing of the cells.
 */
public class ReadOnlyTable extends JTable
{
   private static final long serialVersionUID = 1444448663563604142L;

   public ReadOnlyTable()
   {
      super();
   }

   public ReadOnlyTable(TableModel dm)
   {
      super(dm);
   }

   public ReadOnlyTable(TableModel dm, TableColumnModel cm)
   {
      super(dm, cm);
   }

   public ReadOnlyTable(int numRows, int numColumns)
   {
      super(numRows, numColumns);
   }

   public ReadOnlyTable(Vector<?> rowData, Vector<?> columnNames)
   {
      super(rowData, columnNames);
   }

   public ReadOnlyTable(Object[][] rowData, Object[] columnNames)
   {
      super(rowData, columnNames);
   }

   public ReadOnlyTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
   {
      super(dm, cm, sm);
   }

   public final boolean isCellEditable(int row, int column)
   {
      return false;
   }
}
