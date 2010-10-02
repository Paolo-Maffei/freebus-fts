package org.freebus.fts.pages.productsbrowser;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.VirtualDevice;

/**
 * A {@link ListCellRenderer} that renders lists of {@link VirtualDevice} and
 * {@link CatalogEntry} objects.
 */
public class ProductsListCellRenderer extends JLabel implements ListCellRenderer
{
   private static final long serialVersionUID = 3299877501930451106L;

   /**
    * Create a renderer with default settings.
    */
   public ProductsListCellRenderer()
   {
      setOpaque(true);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
         boolean cellHasFocus)
   {
      if (value instanceof VirtualDevice)
      {
         final VirtualDevice virtDev = (VirtualDevice) value;
         final CatalogEntry catEntry = virtDev.getCatalogEntry();

         String txt = virtDev.getName();
//         if (virtDev.getName().equals(catEntry.getName()))
//            txt = virtDev.getName();
//         else txt = virtDev.getName() + " [" + catEntry.getName() + "]";

         final Program prog = virtDev.getProgram();
         if (prog != null)
            txt += " [" + prog.getName() + ']';

         setText(txt);
      }
      else if (value != null)
      {
         setText(value.toString());
      }
      else
      {
         setText("");
      }

      if (isSelected)
      {
         setBackground(list.getSelectionBackground());
         setForeground(list.getSelectionForeground());
      }
      else
      {
         setBackground(list.getBackground());
         setForeground(list.getForeground());
      }
      return this;
   }

}
