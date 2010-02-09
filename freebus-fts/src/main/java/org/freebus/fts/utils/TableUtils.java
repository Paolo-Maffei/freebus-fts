package org.freebus.fts.utils;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Utility methods for {@link JTree} trees.
 */
public final class TableUtils
{
   /**
    * Pack the table such that all columns are just as wide as their contents
    * requires.
    */
   public static void pack(JTable table, int margin)
   {
      final TableColumnModel colModel = table.getColumnModel();

      for (int colIdx = colModel.getColumnCount() - 1; colIdx >= 0; --colIdx)
      {
         final TableColumn col = colModel.getColumn(colIdx);
         int width = 0;

         TableCellRenderer renderer = col.getHeaderRenderer();
         if (renderer == null)
            renderer = table.getTableHeader().getDefaultRenderer();

         Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
         width = comp.getPreferredSize().width;

         // Get maximum width of column data
         for (int r = 0; r < table.getRowCount(); r++)
         {
            renderer = table.getCellRenderer(r, colIdx);
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, colIdx), false, false, r, colIdx);
            width = Math.max(width, comp.getPreferredSize().width);
         }

         col.setPreferredWidth(width + (margin << 1));
      }
   }

   /*
    * Not to be used.
    */
   private TableUtils()
   {
   }
}
