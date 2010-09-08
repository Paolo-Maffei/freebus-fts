package org.freebus.fts.pages;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.pages.deviceeditor.ComObjectsPanel;
import org.freebus.fts.pages.deviceeditor.DebugPanel;
import org.freebus.fts.pages.deviceeditor.GeneralPanel;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectListener;

/**
 * An editor for the details of a device: description, name, physical address,
 * parameters, etc.
 */
public class DeviceEditor extends AbstractPage
{
   private static final long serialVersionUID = 1396768831831692179L;

   private final JLabel icon = new JLabel();
   private final JLabel caption = new JLabel();
   private final JTabbedPane tabPane = new JTabbedPane();
   private final GeneralPanel generalPanel = new GeneralPanel();
   private final ComObjectsPanel comObjectsPanel = new ComObjectsPanel();
   private final ParameterEditor paramsPanel = new ParameterEditor();
   private final DebugPanel debugPanel = new DebugPanel();

   private Device device;

   /**
    * Create a device-editor.
    */
   public DeviceEditor()
   {
      setLayout(new GridBagLayout());
      setName(I18n.getMessage("DeviceEditor.EmptyTitle"));

      final Dimension iconSize = new Dimension(32, 32);
      icon.setMinimumSize(iconSize);
      icon.setMaximumSize(iconSize);
      icon.setIcon(ImageCache.getIcon("icons-large/device"));
      add(icon, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL,
            new Insets(2, 4, 2, 4), 0, 0));

      final Font fnt = caption.getFont();
      caption.setFont(fnt.deriveFont(Font.BOLD).deriveFont(fnt.getSize2D() * 1.2f));
      add(caption, new GridBagConstraints(1, 0, 1, 1, 100, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
            new Insets(2, 4, 2, 0), 0, 0));

      add(tabPane, new GridBagConstraints(0, 1, 2, 1, 100, 100, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

      tabPane.add(I18n.getMessage("DeviceEditor.General") + " ", generalPanel);

      tabPane.add(I18n.getMessage("DeviceEditor.CommunicationObjects"), comObjectsPanel);

      tabPane.add(I18n.getMessage("DeviceEditor.Parameters"), paramsPanel);
      paramsPanel.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            paramsPanel.apply();
            comObjectsPanel.updateContents();
            debugPanel.updateContents();
         }
      });

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
      }
      
      @Override
      public void projectComponentModified(Object obj)
      {
         if (obj == device)
            updateContents();
      }

      @Override
      public void projectComponentAdded(Object obj)
      {
      }
      
      @Override
      public void projectChanged(Project project)
      {
         updateContents();
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

      setModified(false);

      final Object[] msgArgs = new Object[] { device.getPhysicalAddress(),
            device.getCatalogEntry().getName() };
      
      setName(I18n.formatMessage("DeviceEditor.Title", msgArgs));
      caption.setText(I18n.formatMessage("DeviceEditor.Caption", msgArgs));

      paramsPanel.setDevice(device);
      generalPanel.setDevice(device);
      comObjectsPanel.setDevice(device);
      debugPanel.setDevice(device);
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
}
