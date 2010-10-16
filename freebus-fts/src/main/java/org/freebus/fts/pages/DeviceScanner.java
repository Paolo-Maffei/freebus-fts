package org.freebus.fts.pages;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.freebus.fts.I18n;
import org.freebus.fts.backend.job.DeviceScannerJob;
import org.freebus.fts.backend.job.DeviceScannerJobListener;
import org.freebus.fts.backend.job.JobQueue;
import org.freebus.fts.backend.job.entity.DeviceInfo;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.elements.components.ReadOnlyTable;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;

/**
 * A page for scanning the KNX/EIB network for devices and gather some info from
 * the devices.
 */
public class DeviceScanner extends AbstractPage implements DeviceScannerJobListener
{
   private static final long serialVersionUID = 5059567180513438199L;

   private DefaultTableModel tbmDevices = new DefaultTableModel();
   private final JTable tblDevices = new ReadOnlyTable(tbmDevices);
   private final JScrollPane scpDevices = new JScrollPane(tblDevices);

   /**
    * Create a device scanner page.
    */
   public DeviceScanner()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("DeviceScanner.Title"));

      tbmDevices.setColumnIdentifiers(new String[] { I18n.getMessage("DeviceScanner.ColAddress"), I18n.getMessage("DeviceScanner.ColInfo") });
      tblDevices.setFillsViewportHeight(true);

      add(scpDevices, BorderLayout.CENTER);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setObject(Object o)
   {
      final DeviceScannerJob job = new DeviceScannerJob(1, 1);
      job.addListener(this);

      JobQueue.getDefaultJobQueue().add(job);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void progress(int done, String message)
   {
   }

   /**
    * Find the table model row where the device info for the address is stored.
    * 
    * @param address - the address to find.
    * @return The row index of the address, or -1 if not found. 
    */
   private int getRowIndex(final PhysicalAddress address)
   {
      for (int i = tbmDevices.getRowCount() - 1; i >= 0; --i)
      {
         if (address.equals(tbmDevices.getValueAt(i, 0)))
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
      final int row = getRowIndex(info.getAddress());
      final DeviceDescriptor descriptor = info.getDescriptor();

      if (row < 0)
      {
         tbmDevices.addRow(new Object[] { info.getAddress(), descriptor });         
      }
      else
      {
         tbmDevices.setValueAt(descriptor, row, 1);
      }

      tblDevices.revalidate();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deviceInfo(final DeviceInfo info)
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
