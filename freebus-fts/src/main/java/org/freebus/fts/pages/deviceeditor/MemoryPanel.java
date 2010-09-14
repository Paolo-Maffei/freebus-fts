package org.freebus.fts.pages.deviceeditor;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.freebus.fts.components.memorytable.DeviceMemoryTableModel;
import org.freebus.fts.components.memorytable.MemoryCell;
import org.freebus.fts.components.memorytable.MemoryCellRenderer;
import org.freebus.fts.pages.DeviceEditor;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.project.Device;

/**
 * Displays details of the edited {@link Device}: the EEPROM memory contents of
 * the device. Part of the {@link DeviceEditor}.
 */
public class MemoryPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -2622838125174217870L;

   private final DeviceMemoryTableModel tableModel;
   private final JTable table;
   private final JScrollPane tableView;

   private boolean dirty = false;

//   class JColoredTable extends JTable
//   {
//      private static final long serialVersionUID = 1L;
//
//      public JColoredTable(TableModel model)
//      {
//         super(model);
//      }
//
//      @Override
//      public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex)
//      {
//         Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
//         Object o = getModel().getValueAt(rowIndex, vColIndex);
//
//         if (c instanceof JLabel && o instanceof MemoryCell)
//         {
//            JLabel jl = (JLabel) c;
//
//            MemoryCell cell = (MemoryCell) o;
//            MemoryRange range = cell.getRange();
//            jl.setBackground(range == null ? getBackground() : range.getBackground());
//         }
//         return c;
//      }
//
//   }

   /**
    * Create a debug panel.
    */
   public MemoryPanel()
   {
      setLayout(new BorderLayout());

      tableModel = new DeviceMemoryTableModel(0, 512, getBackground());
      table = new JTable(tableModel);
      table.setDefaultRenderer(MemoryCell.class, new MemoryCellRenderer());
      table.setCellSelectionEnabled(true);
      table.setDoubleBuffered(false);

      tableView = new JScrollPane(table);
      add(tableView, BorderLayout.CENTER);

      addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentShown(ComponentEvent e)
         {
            if (dirty)
               updateContents();
         }
      });
   }

   /**
    * Set the edited device.
    */
   public void setDevice(Device device)
   {
      tableModel.setDevice(device);
   }

   /**
    * Update the contents of the panel
    */
   public void updateContents()
   {
      if (!isVisible())
      {
         dirty = true;
         return;
      }

      dirty = false;
      tableModel.updateContents();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void componentChanged(Object obj)
   {
   }

   /**
    * Sort the parameters by display order {@link Parameter#getDisplayOrder()}.
    * 
    * @param params - the parameters to sort.
    * 
    * @return a sorted array of parameters.
    */
   public static Parameter[] sortParametersByDisplayOrder(final Collection<Parameter> params)
   {
      final Parameter[] paramsSorted = new Parameter[params.size()];
      params.toArray(paramsSorted);
      Arrays.sort(paramsSorted, new Comparator<Parameter>()
      {
         @Override
         public int compare(Parameter a, Parameter b)
         {
            return a.getDisplayOrder() - b.getDisplayOrder();
         }
      });

      return paramsSorted;
   }

   /**
    * Sort the communication objects by display order
    * {@link Parameter#getDisplayOrder()}.
    * 
    * @param params - the communication objects to sort.
    * 
    * @return a sorted array of communication objects.
    */
   public static CommunicationObject[] sortCommunicationObjectsByDisplayOrder(final Collection<CommunicationObject> objs)
   {
      final CommunicationObject[] objsSorted = new CommunicationObject[objs.size()];
      objs.toArray(objsSorted);
      Arrays.sort(objsSorted, new Comparator<CommunicationObject>()
      {
         @Override
         public int compare(CommunicationObject a, CommunicationObject b)
         {
            return a.getDisplayOrder() - b.getDisplayOrder();
         }
      });

      return objsSorted;
   }
}
