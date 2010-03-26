package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.project.Device;
import org.freebus.fts.utils.ButtonUtils;

/**
 * An editor for the details of a device: description, name, physical address,
 * parameters, etc.
 */
public class DeviceEditor extends AbstractPage
{
   private static final long serialVersionUID = 1396768831831692179L;

   private ParameterEditor paramEdit = new ParameterEditor();
   private Device device;

   /**
    * Create a device-editor.
    */
   public DeviceEditor()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("DeviceEditor.EmptyTitle"));

      add(paramEdit, BorderLayout.CENTER);
      paramEdit.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            setModified(true);
         }
      });

      initToolBar();
   }

   /**
    * Create the tool-bar.
    */
   private void initToolBar()
   {
      final ToolBar toolBar = new ToolBar();
      add(toolBar, BorderLayout.NORTH);

      final JButton btnApply = new JButton(I18n.getMessage("Button.Apply"), ImageCache.getIcon("icons/apply"));
      ButtonUtils.setToolButtonProperties(btnApply);
      toolBar.add(btnApply);
      btnApply.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            apply();
         }
      });

      final JButton btnRevert = new JButton(I18n.getMessage("Button.Revert"), ImageCache.getIcon("icons/undo"));
      ButtonUtils.setToolButtonProperties(btnRevert);
      toolBar.add(btnRevert);
      btnRevert.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            setObject(device);
         }
      });

      final JButton btnCancel = new JButton(I18n.getMessage("Button.Cancel"), ImageCache.getIcon("icons/cancel"));
      ButtonUtils.setToolButtonProperties(btnCancel);
      toolBar.add(btnCancel);
      btnCancel.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            close();
         }
      });
   }

   /**
    * Apply the changes to the project
    */
   public void apply()
   {
      paramEdit.apply();
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
      setName(I18n.formatMessage("DeviceEditor.Title", new Object[] { device.getPhysicalAddress(),
            device.getCatalogEntry().getName() }));

      paramEdit.setDevice(device);
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
