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
            //setModified(true);
         }
      });

      tabPane.add(I18n.getMessage("DeviceEditor.Debug"), debugPanel);

      // initToolBar();
   }

//   /**
//    * Create the tool-bar.
//    */
//   private void initToolBar()
//   {
//      final ToolBar toolBar = new ToolBar();
//      add(toolBar, BorderLayout.NORTH);
//
//      final JButton btnApply = new JButton(I18n.getMessage("Button.Apply"), ImageCache.getIcon("icons/apply"));
//      ButtonUtils.setToolButtonProperties(btnApply);
//      toolBar.add(btnApply);
//      btnApply.addActionListener(new ActionListener()
//      {
//         @Override
//         public void actionPerformed(ActionEvent e)
//         {
//            apply();
//         }
//      });
//
//      final JButton btnRevert = new JButton(I18n.getMessage("Button.Revert"), ImageCache.getIcon("icons/undo"));
//      ButtonUtils.setToolButtonProperties(btnRevert);
//      toolBar.add(btnRevert);
//      btnRevert.addActionListener(new ActionListener()
//      {
//         @Override
//         public void actionPerformed(ActionEvent e)
//         {
//            setObject(device);
//         }
//      });
//
//      final JButton btnCancel = new JButton(I18n.getMessage("Button.Cancel"), ImageCache.getIcon("icons/cancel"));
//      ButtonUtils.setToolButtonProperties(btnCancel);
//      toolBar.add(btnCancel);
//      btnCancel.addActionListener(new ActionListener()
//      {
//         @Override
//         public void actionPerformed(ActionEvent e)
//         {
//            close();
//         }
//      });
//   }

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
