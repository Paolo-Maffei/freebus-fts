package org.freebus.fts.client.pages.devicedetails;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.freebus.fts.client.components.AbstractPage;
import org.freebus.fts.client.components.ParameterEditor;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;
import org.freebus.fts.service.devicecontroller.DeviceController;
import org.freebus.fts.service.devicecontroller.DeviceControllerFactory;

/**
 * An editor for the details of a device: description, name, physical address,
 * parameters, etc.
 */
public class DeviceDetails extends AbstractPage
{
   private static final long serialVersionUID = 1396768831831692179L;

   private final JLabel iconLabel = new JLabel();
   private final JLabel caption = new JLabel();
   private final JTabbedPane tabPane = new JTabbedPane();
   private final GeneralPanel generalPanel = new GeneralPanel();
   private final DeviceObjectsPanel deviceObjectsPanel = new DeviceObjectsPanel();
   private final ParameterEditor paramsPanel = new ParameterEditor();
   private final DebugPanel debugPanel = new DebugPanel();
   private final MemoryPanel memoryPanel = new MemoryPanel();
   private final TablesPanel tablesPanel = new TablesPanel();

   private Device device;
   private DeviceController adapter;

   /**
    * Create a device-editor.
    */
   public DeviceDetails()
   {
      setLayout(new GridBagLayout());
      setName(I18n.getMessage("DeviceEditor.EmptyTitle"));

      final Icon icon = ImageCache.getIcon("icons-large/device");
      final Dimension iconSize = new Dimension(icon.getIconWidth(), icon.getIconHeight());
      iconLabel.setMinimumSize(iconSize);
      iconLabel.setMaximumSize(iconSize);
      iconLabel.setPreferredSize(iconSize);
      iconLabel.setIcon(icon);

      add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL,
            new Insets(2, 4, 2, 4), 0, 0));

      final Font fnt = caption.getFont();
      caption.setFont(fnt.deriveFont(Font.BOLD).deriveFont(fnt.getSize2D() * 1.2f));
      add(caption, new GridBagConstraints(1, 0, 1, 1, 100, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
            new Insets(2, 4, 2, 0), 0, 0));

      add(tabPane, new GridBagConstraints(0, 1, 2, 1, 100, 100, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

      tabPane.add(I18n.getMessage("DeviceEditor.General") + " ", generalPanel);

      tabPane.add(I18n.getMessage("DeviceEditor.CommunicationObjects"), deviceObjectsPanel);

      tabPane.add(I18n.getMessage("DeviceEditor.Parameters"), paramsPanel);
      paramsPanel.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            paramsPanel.apply();
            deviceObjectsPanel.updateContents();
            debugPanel.updateContents();
         }
      });

      tabPane.add(I18n.getMessage("DeviceEditor.Tables"), tablesPanel);
      tabPane.add(I18n.getMessage("DeviceEditor.Memory"), memoryPanel);
      tabPane.add(I18n.getMessage("DeviceEditor.Debug"), debugPanel);

      ProjectManager.addListener(projectListener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void closeEvent()
   {
      ProjectManager.removeListener(projectListener);
   }

   /*
    * Listener for project changes
    */
   private final ProjectListener projectListener = new ProjectListener()
   {
      @Override
      public void projectComponentRemoved(Object obj)
      {
         if (obj == device)
            close();
         else componentChanged(obj);
      }

      @Override
      public void projectComponentModified(Object obj)
      {
         componentChanged(obj);
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
         componentChanged(obj);
      }

      @Override
      public void projectChanged(Project project)
      {
         componentChanged(project);
      }
   };

   /**
    * Apply the changes to the project
    */
   public void apply()
   {
      paramsPanel.apply();
      setModified(false);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setObject(Object o)
   {
      device = (Device) o;
      adapter = DeviceControllerFactory.getDeviceController(device);

      setModified(false);

      setName(device.getPhysicalAddress().toString());
      caption.setText(I18n.formatMessage("DeviceEditor.Caption", device.getPhysicalAddress().toString(),
            device.getCatalogEntry().getName()));

      paramsPanel.setDevice(device);
      generalPanel.setDevice(device, adapter);
      deviceObjectsPanel.setDevice(device, adapter);
      debugPanel.setDevice(device, adapter);
      memoryPanel.setDevice(device, adapter);
      tablesPanel.setDevice(device, adapter);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      if (isModified())
      {
         final int ret = confirmClose();

         if (ret == JOptionPane.CANCEL_OPTION)
            return;
         else if (ret == JOptionPane.YES_OPTION)
            apply();
      }

      super.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void updateContents()
   {
      componentChanged(null);
   }

   public void componentChanged(Object obj)
   {
      if (obj instanceof Device || obj instanceof Line || obj instanceof Area)
         setName(device.getPhysicalAddress().toString());

      if (obj == device)
         adapter.deviceChanged();

      generalPanel.componentChanged(obj);
      deviceObjectsPanel.componentChanged(obj);
      memoryPanel.updateContents();
      tablesPanel.updateContents();
   }
}
