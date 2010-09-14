package org.freebus.fts.components.memorytable;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A {@link DefaultTableCellRenderer table cell renderer} that is suited to
 * render {@link MemoryCell memory cells} of a {@link MemoryCellTableModel}.
 */
public class MemoryCellRenderer extends DefaultTableCellRenderer
{
   private static final long serialVersionUID = -8985604131449863609L;

   /**
    * Create a {@link MemoryCell memory cell} table renderer.
    */
   public MemoryCellRenderer()
   {
      setHorizontalAlignment(SwingConstants.CENTER);
   }

   /**
    * {@inheritDoc}
    */
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
         int row, int column)
   {
      final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (c instanceof JComponent && value instanceof MemoryCell)
      {
         final JComponent jc = (JComponent) c;
         final MemoryCell cell = (MemoryCell) value;

         final MemoryRange range = cell.getRange();
         if (range == null)
            jc.setToolTipText("Anonymous Memory");
         else jc.setToolTipText("<html><body>Type: " + range.getName() + "<br>Start: " + range.getStart() + "<br>Size: " + range.getSize() + "</body></html>");

         if (!isSelected)
            jc.setBackground(range == null ? table.getBackground() : range.getBackground());
      }

      return c;
   }
}
