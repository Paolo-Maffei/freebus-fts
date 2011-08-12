package org.freebus.fts.client.editors.scanner;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.workbench.WorkBenchEditor;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.elements.components.ToolBar;
import org.freebus.fts.elements.components.ToolBarButton;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.elements.table.ReadOnlyTable;
import org.freebus.fts.elements.table.TableButtonRenderer;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.services.ProgramService;
import org.freebus.fts.service.job.DeviceScannerJob;
import org.freebus.fts.service.job.DeviceScannerJobListener;
import org.freebus.fts.service.job.JobQueue;
import org.freebus.fts.service.job.entity.DeviceInfo;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;

/**
 * An editor for scanning the KNX/EIB network for devices and gather some info from
 * the devices.
 */
public class Scanner extends WorkBenchEditor implements DeviceScannerJobListener
{
   private static final long serialVersionUID = 5059567180513438199L;
   private static final int COL_ID_BUTTON = 0;
   private static final int COL_ID_ADDRESS = 1;
   private static final int COL_ID_MANUFACTURER = 2;
   private static final int COL_ID_DEVICE_TYPE = 3;
   private static final int COL_ID_INFO = 4;

   private DeviceScannerJob job;

   private final DefaultTableModel devicesModel = new DefaultTableModel();
   private final JTable devicesTable = new ReadOnlyTable(devicesModel);
   private final JScrollPane devicesScrollPane = new JScrollPane(devicesTable);
   private final ScanParamsPanel paramsPanel = new ScanParamsPanel("Scanner");

   private final ImageIcon addIcon = ImageCache.getIcon("icons-small/add");

   /**
    * Create a device scanner page.
    */
   public Scanner()
   {
      setLayout(new GridBagLayout());
      setName(I18n.getMessage("Scanner.Title"));

      add(createToolBar(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, 
            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

      add(paramsPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, 
            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      paramsPanel.addStartStopActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               @Override
               public void run()
               {
                  startStopButtonClicked();
               }
            });
         }
      });

      // If you change the columns here, be sure to change the COL_ID_xy constants in the top
      // of the class too.
      devicesModel.setColumnIdentifiers(new String[]{
            "+",  // "add" button
            I18n.getMessage("Scanner.ColAddress"),
            I18n.getMessage("Scanner.ColManufacturer"),
            I18n.getMessage("Scanner.ColDeviceType"),
            I18n.getMessage("Scanner.ColInfo")
            });

      devicesTable.setFillsViewportHeight(true);
      final TableColumn btnColumn = devicesTable.getColumn("+");
      btnColumn.setCellRenderer(new TableButtonRenderer());
      btnColumn.setMaxWidth(32);

      add(devicesScrollPane, new GridBagConstraints(0, 2, 1, 1, 1, 100, GridBagConstraints.WEST,
            GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void closeEvent()
   {
      Config.getInstance().save();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void objectChanged(Object o)
   {
   }

   /**
    * The start/stop button was clicked
    */
   private synchronized void startStopButtonClicked()
   {
      if (job == null)
      {
         job = new DeviceScannerJob(paramsPanel.getAreaAddr(), paramsPanel.getLineAddr());
         job.setMinAddress(paramsPanel.getMinDeviceAddr());
         job.setMaxAddress(paramsPanel.getMaxDeviceAddr());
         job.addListener(this);

         JobQueue.getDefaultJobQueue().add(job);
         paramsPanel.setScanning(true);
      }
      else
      {
         JobQueue.getDefaultJobQueue().cancel(job);
         job = null;
         paramsPanel.setScanning(false);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void progress(int done, String message)
   {
      if (done == 100)
      {
         paramsPanel.setScanning(false);
         job = null;
      }
   }

   /**
    * Find the table model row where the device info for the address is stored.
    *
    * @param address - the address to find.
    * @return The row index of the address, or -1 if not found.
    */
   private int getRowIndex(final PhysicalAddress address)
   {
      for (int i = devicesModel.getRowCount() - 1; i >= 0; --i)
      {
         if (address.equals(devicesModel.getValueAt(i, COL_ID_ADDRESS)))
            return i;
      }

      return -1;
   }

   /**
    * Update a device information
    *.
    * @param info - the device information.
    */
   public void updateDeviceInfo(DeviceInfo info)
   {
      int row = getRowIndex(info.getAddress());
      final DeviceDescriptor descriptor = info.getDescriptor();

      if (row < 0)
      {
         row = devicesModel.getRowCount();
         devicesModel.addRow(new Object[] { null, info.getAddress(), "", "", descriptor });
      }
      else
      {
         devicesModel.setValueAt(descriptor, row, COL_ID_INFO);
      }

      final int manufacturerId = info.getManufacturerId();
      Manufacturer manufacturer = null;
      if (manufacturerId >= 0)
      {
         manufacturer = ProductsManager.getFactory().getManufacturerService().getManufacturer(manufacturerId);

         if (manufacturer == null)
            devicesModel.setValueAt("#" + manufacturerId, row, COL_ID_MANUFACTURER);
         else devicesModel.setValueAt(manufacturer, row, COL_ID_MANUFACTURER);
      }

      final int deviceType = info.getDeviceType();
      if (deviceType >= 0)
      {
         List<Program> programs = new Vector<Program>(0);

         if (manufacturer != null)
         {
            final ProgramService programService = ProductsManager.getFactory().getProgramService();
            programs = programService.findProgram(manufacturer, deviceType);
         }

         if (programs.isEmpty())
            devicesModel.setValueAt("$" + Integer.toHexString(deviceType), row, COL_ID_DEVICE_TYPE);
         else devicesModel.setValueAt(programs.get(0).getName(), row, COL_ID_DEVICE_TYPE);
      }

      if (info.isComplete() && devicesModel.getValueAt(row, COL_ID_BUTTON) == null)
      {
         final JButton addButton = new ToolBarButton(addIcon);
         devicesModel.setValueAt(addButton, row, COL_ID_BUTTON);
         
         // TODO addButton.addActionListener(...)
      }
      
      devicesTable.revalidate();
   }

   /**
    * Create the tool-bar.
    * 
    * @return The tool-bar.
    */
   private JToolBar createToolBar()
   {
      final JToolBar toolBar = new ToolBar();

      JButton btn = toolBar.add(new AbstractAction()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public void actionPerformed(ActionEvent e)
         {
         }

      });
      btn.setIcon(ImageCache.getIcon("icons/add"));
      btn.setToolTipText(I18n.getMessage("Scanner.Add...."));

      return toolBar;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public final void deviceInfo(final DeviceInfo info)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            updateDeviceInfo(info);
         }
      });
   }
}
