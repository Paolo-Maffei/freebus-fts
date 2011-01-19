package org.freebus.fts.client.editors.devicedetails;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.freebus.fts.client.components.memorytable.DeviceMemoryTableModel;
import org.freebus.fts.client.components.memorytable.MemoryCell;
import org.freebus.fts.client.components.memorytable.MemoryCellRenderer;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.project.Device;
import org.freebus.fts.service.devicecontroller.DeviceController;

/**
 * Displays details of the edited {@link Device}: the EEPROM memory contents of
 * the device. Part of the {@link DeviceDetails}.
 */
public class MemoryPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -2622838125174217870L;

   private DeviceMemoryTableModel tableModel;
   private final JTable table;
   private final JScrollPane tableView;

   /**
    * Create a debug panel.
    */
   public MemoryPanel()
   {
      setLayout(new BorderLayout());

      final JLabel lbl = new JLabel(I18n.getMessage("MemoryPanel.Caption"));
      lbl.setFont(lbl.getFont().deriveFont(lbl.getFont().getSize2D() * 1.2f));
      lbl.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
      add(lbl, BorderLayout.NORTH);

      tableModel = new DeviceMemoryTableModel(0, 4096, getBackground());
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
            updateContents();
         }
      });
   }

   /**
    * Set the edited device.
    */
   public void setDevice(Device device, DeviceController adapter)
   {
      if (device != null)
      {
         int endAddr = device.getProgram().getMask().getUserEepromEnd();
         endAddr = (endAddr + 15) & ~15;

         tableModel = new DeviceMemoryTableModel(0, endAddr, getBackground());
         table.setModel(tableModel);
      }

      tableModel.setDevice(device);
   }

   /**
    * Update the contents of the panel
    */
   public void updateContents()
   {
      if (!isVisible())
         return;

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
