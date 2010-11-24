package org.freebus.fts.pages;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.elements.components.ReadOnlyTable;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.service.job.DeviceScannerJob;
import org.freebus.fts.service.job.DeviceScannerJobListener;
import org.freebus.fts.service.job.JobQueue;
import org.freebus.fts.service.job.entity.DeviceInfo;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;

/**
 * A page for scanning the KNX/EIB network for devices and gather some info from
 * the devices.
 */
public class DeviceScanner extends AbstractPage implements DeviceScannerJobListener
{
   private static final long serialVersionUID = 5059567180513438199L;

   private DeviceScannerJob job;

   private final DefaultTableModel devicesModel = new DefaultTableModel();
   private final JTable devicesTable = new ReadOnlyTable(devicesModel);
   private final JScrollPane devicesScrollPane = new JScrollPane(devicesTable);

   private final JComboBox areaAddrCombo, lineAddrCombo, minDeviceAddrCombo, maxDeviceAddrCombo;
   private final JButton startStopButton = new JButton(I18n.getMessage("DeviceScanner.Start"));

   /**
    * Create a device scanner page.
    */
   public DeviceScanner()
   {
      setLayout(new GridBagLayout());
      setName(I18n.getMessage("DeviceScanner.Title"));

      final Config cfg = Config.getInstance();
      final Insets noInsets = new Insets(0, 0, 0, 0);
      final Insets stdInsets = new Insets(2, 2, 2, 2);
      final int anchorW = GridBagConstraints.WEST;
      JLabel lbl;
      int col = -1;

      lbl = new JLabel(ImageCache.getIcon("icons-large/find"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));

      lbl = new JLabel(I18n.getMessage("DeviceScanner.ScanArea"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));

      areaAddrCombo = createNumbersComboBox(0, 15);
      areaAddrCombo.setMaximumRowCount(16);
      areaAddrCombo.setSelectedItem(cfg.getIntValue("DeviceScanner.areaAddr", 0));
      add(areaAddrCombo,
            new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));
      areaAddrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Config.getInstance().put("DeviceScanner.areaAddr", (Integer) areaAddrCombo.getSelectedItem());
         }
      });

      lbl = new JLabel(I18n.getMessage("DeviceScanner.ScanLine"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, noInsets, 0, 0));

      lineAddrCombo = createNumbersComboBox(0, 15);
      lineAddrCombo.setMaximumRowCount(16);
      lineAddrCombo.setSelectedItem(cfg.getIntValue("DeviceScanner.lineAddr", 0));
      add(lineAddrCombo,
            new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));
      lineAddrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Config.getInstance().put("DeviceScanner.lineAddr", (Integer) lineAddrCombo.getSelectedItem());
         }
      });

      lbl = new JLabel(I18n.getMessage("DeviceScanner.FromAddr"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, new Insets(2, 20, 2, 2),
            0, 0));

      minDeviceAddrCombo = createNumbersComboBox(0, 255);
      minDeviceAddrCombo.setMaximumRowCount(32);
      minDeviceAddrCombo.setSelectedItem(cfg.getIntValue("DeviceScanner.deviceMinAddr", 0));
      add(minDeviceAddrCombo, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets,
            0, 0));
      minDeviceAddrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            int minValue = (Integer) minDeviceAddrCombo.getSelectedItem();
            int maxValue = (Integer) maxDeviceAddrCombo.getSelectedItem();
            Config.getInstance().put("DeviceScanner.deviceMinAddr", minValue);
            if (minValue > maxValue)
               maxDeviceAddrCombo.setSelectedItem(minValue);
         }
      });

      lbl = new JLabel(I18n.getMessage("DeviceScanner.ToAddr"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));

      maxDeviceAddrCombo = createNumbersComboBox(0, 255);
      maxDeviceAddrCombo.setMaximumRowCount(32);
      maxDeviceAddrCombo.setSelectedItem(cfg.getIntValue("DeviceScanner.deviceMaxAddr", 255));
      add(maxDeviceAddrCombo, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets,
            0, 0));
      maxDeviceAddrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            int minValue = (Integer) minDeviceAddrCombo.getSelectedItem();
            int maxValue = (Integer) maxDeviceAddrCombo.getSelectedItem();
            Config.getInstance().put("DeviceScanner.deviceMaxAddr", maxValue);
            if (maxValue < minValue)
               minDeviceAddrCombo.setSelectedItem(maxValue);
         }
      });

      add(startStopButton, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, new Insets(2,
            40, 2, 2), 0, 0));
      startStopButton.addActionListener(new ActionListener()
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

      add(Box.createHorizontalGlue(), new GridBagConstraints(++col, 0, 1, 1, 100, 1, anchorW,
            GridBagConstraints.HORIZONTAL, noInsets, 0, 0));

      final int cols = col + 1;

      devicesModel.setColumnIdentifiers(new String[] { I18n.getMessage("DeviceScanner.ColAddress"),
            I18n.getMessage("DeviceScanner.ColManufacturer"), I18n.getMessage("DeviceScanner.ColDeviceType"),
            I18n.getMessage("DeviceScanner.ColInfo") });
      devicesTable.setFillsViewportHeight(true);

      add(devicesScrollPane, new GridBagConstraints(0, 2, cols, 1, 1, 100, anchorW, GridBagConstraints.BOTH, stdInsets,
            0, 0));
   }

   /**
    * Create a combo-box with numbers from minValue to maxValue (including
    * minValue and maxValue).
    *
    * @param minValue - the minimum value.
    * @param maxValue - the maximum value.
    *
    * @return The created combo-box.
    */
   private JComboBox createNumbersComboBox(int minValue, int maxValue)
   {
      final JComboBox combo = new JComboBox();

      for (int value = minValue; value <= maxValue; ++value)
         combo.addItem(value);

      return combo;
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
   public void setObject(Object o)
   {
   }

   /**
    * The start/stop button was clicked
    */
   private synchronized void startStopButtonClicked()
   {
      if (job == null)
      {
         job = new DeviceScannerJob((Integer) areaAddrCombo.getSelectedItem(),
               (Integer) lineAddrCombo.getSelectedItem());
         job.setMinAddress((Integer) minDeviceAddrCombo.getSelectedItem());
         job.setMaxAddress((Integer) maxDeviceAddrCombo.getSelectedItem());
         job.addListener(this);

         JobQueue.getDefaultJobQueue().add(job);
         startStopButton.setText(I18n.getMessage("DeviceScanner.Stop"));
      }
      else
      {
         JobQueue.getDefaultJobQueue().cancel(job);
         job = null;
         startStopButton.setText(I18n.getMessage("DeviceScanner.Start"));
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
         startStopButton.setText(I18n.getMessage("DeviceScanner.Start"));
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
         if (address.equals(devicesModel.getValueAt(i, 0)))
            return i;
      }

      return -1;
   }

   /**
    * Update a device information
    *
    * @param info - the device information.
    */
   public void updateDeviceInfo(DeviceInfo info)
   {
      int row = getRowIndex(info.getAddress());
      final DeviceDescriptor descriptor = info.getDescriptor();

      if (row < 0)
      {
         row = devicesModel.getRowCount();
         devicesModel.addRow(new Object[] { info.getAddress(), "", "", descriptor });
      }
      else
      {
         devicesModel.setValueAt(descriptor, row, 3);
      }

      final int manufacturerId = info.getManufacturerId();
      if (manufacturerId >= 0)
      {
         final Manufacturer manufacturer = ProductsManager.getFactory().getManufacturerService().getManufacturer(manufacturerId);
         devicesModel.setValueAt(manufacturer == null ? ("#" + manufacturerId) : manufacturer.getName(), row, 1);
      }

      final int deviceType = info.getDeviceType();
      if (deviceType >= 0)
         devicesModel.setValueAt("$" + Integer.toHexString(deviceType), row, 2);

      devicesTable.revalidate();
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
