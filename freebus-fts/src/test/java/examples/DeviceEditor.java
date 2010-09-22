package examples;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.freebus.fts.common.Environment;
import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.core.LookAndFeelManager;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Device;

/**
 * A test program that opens an editor for a device.
 * 
 * This program works with the virtual devices that were imported with FTS into
 * FTS' database.
 */
public class DeviceEditor
{
   static private final String appTitle = "FTS Device Editor";
   private final JFrame frame = new JFrame();
   private final ParameterEditor paramEdit = new ParameterEditor();
   private final JList lstDevices;
   private final VirtualDevice[] virtualDevices;

   /**
    * Create a new device editor test-window.
    *
    * @param virtualDevs - the list of devices that can be edited.
    */
   public DeviceEditor(final List<VirtualDevice> virtualDevs)
   {
      this.virtualDevices = new VirtualDevice[virtualDevs.size()];
      virtualDevs.toArray(this.virtualDevices);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setTitle(appTitle);
      frame.setLayout(new BorderLayout());
      frame.setIconImage(ImageCache.getIcon("app-icon").getImage());

      final JLabel lbl = new JLabel("<html><h3>Device Editor</h3><p>This is a standalone test program to test the parameter editor of FTS.</p></html>");
      lbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
      frame.add(lbl, BorderLayout.NORTH);

      frame.add(paramEdit, BorderLayout.CENTER);

      lstDevices = new JList(this.virtualDevices);
      frame.add(lstDevices, BorderLayout.WEST);
      lstDevices.getSelectionModel().addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent event)
         {
            final int idx = lstDevices.getSelectedIndex();
            if (idx < 0) return;

            try
            {
               frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               final Device dev = new Device(virtualDevices[idx]);
               paramEdit.setDevice(dev);
            }
            catch (Exception e)
            {
               e.printStackTrace();
               Dialogs.showExceptionDialog(e, "Failed to open the parameter editor for the selected device.");
            }
            finally
            {
               frame.setCursor(Cursor.getDefaultCursor());
            }
         }
      });
      
      frame.setSize(1200, 800);
      frame.setVisible(true);
   }

   /**
    * The main.
    */
   public static void main(String[] args)
   {
      Environment.init();
      @SuppressWarnings("unused")
      final Config globalConfig = new Config();
      LookAndFeelManager.setDefaultLookAndFeel();
      DatabaseResources.setEntityManagerFactory(DatabaseResources.createDefaultEntityManagerFactory());

      final List<VirtualDevice> virtualDevices = ProductsManager.getFactory().getVirtualDeviceService()
            .getVirtualDevices();
      if (virtualDevices.isEmpty())
      {
         JOptionPane.showMessageDialog(null,
               "No virtual devices found.\n\nPlease import org.freebus.fts.products from vd_\nfiles with FTS and try again.",
               "Error - " + appTitle, JOptionPane.ERROR_MESSAGE);
         System.exit(1);
      }

      new DeviceEditor(virtualDevices);
   }
}
