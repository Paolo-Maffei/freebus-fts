package org.freebus.fts.pages.deviceeditor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freebus.fts.I18n;
import org.freebus.fts.backend.KNXDeviceAdapterFactory;
import org.freebus.fts.components.DeviceTables;
import org.freebus.fts.project.Device;

/**
 * Displays the tables of the edited {@link Device}: the group addresses, the
 * communication objects, the association table.
 */
public class TablesPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -7096959777110755305L;

   private final DeviceTables tables = new DeviceTables();

   private boolean dirty = false;

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
      tables.setDeviceAdapter(KNXDeviceAdapterFactory.getDeviceAdapter(device));
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
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void componentChanged(Object obj)
   {
   }
}
