package org.freebus.fts.pages;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.core.I18n;
import org.freebus.fts.jobs.DeviceScannerJob;
import org.freebus.fts.jobs.DeviceScannerJobListener;
import org.freebus.knxcomm.jobs.JobQueue;

/**
 * A page for scanning the KNX/EIB network for devices and gather
 * some info from the devices.
 */
public class DeviceScanner extends AbstractPage implements DeviceScannerJobListener
{
   private static final long serialVersionUID = 5059567180513438199L;

   private DefaultTableModel tbmDevices = new DefaultTableModel();
   private final JTable tblDevices = new JTable();
   private final JScrollPane scpDevices = new JScrollPane(tblDevices);


   /**
    * Create a device scanner page.
    */
   public DeviceScanner()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("DeviceScanner.Title"));

      tbmDevices.setColumnIdentifiers(new String[] { I18n.getMessage("DeviceScanner.ColAddress") });

      tblDevices.setFillsViewportHeight(true);

      add(scpDevices, BorderLayout.CENTER);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setObject(Object o)
   {
      JobQueue.getDefaultJobQueue().add(new DeviceScannerJob(1, 1));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void progress(int done, String message)
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deviceFound(final PhysicalAddress addr)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            tbmDevices.addRow(new Object[] { addr });
         }
      });
   }
}
