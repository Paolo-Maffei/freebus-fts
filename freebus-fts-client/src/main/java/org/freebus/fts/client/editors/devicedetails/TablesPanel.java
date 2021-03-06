package org.freebus.fts.client.editors.devicedetails;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freebus.fts.client.components.DeviceTables;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.project.Device;
import org.freebus.fts.service.devicecontroller.DeviceController;

/**
 * Displays the tables of the edited {@link Device}: the group addresses, the
 * communication objects, the association table.
 */
public class TablesPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -7096959777110755305L;

   private final DeviceTables tables = new DeviceTables();

   /**
    * Create a device tables panel.
    */
   public TablesPanel()
   {
      setLayout(new BorderLayout());

      final JLabel caption = new JLabel(I18n.getMessage("DeviceEditor.TablesPanel.Caption"));
      caption.setFont(caption.getFont().deriveFont(Font.BOLD));
      caption.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
      add(caption, BorderLayout.NORTH);

      add(tables, BorderLayout.CENTER);

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
      tables.setDeviceAdapter(adapter);
   }

   /**
    * Update the contents of the panel
    */
   public void updateContents()
   {
      if (isVisible())
         tables.updateContents();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void componentChanged(Object obj)
   {
   }
}
