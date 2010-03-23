package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.core.I18n;
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

     createToolBar();
  }

  /**
   * Create the tool-bar.
   */
  private void createToolBar()
  {
     final ToolBar toolBar = new ToolBar();
     add(toolBar, BorderLayout.NORTH);

     final JButton btnApply = new JButton(I18n.getMessage("Button.Apply"));
     ButtonUtils.setToolButtonProperties(btnApply);
     toolBar.add(btnApply);

     btnApply.addActionListener(new ActionListener()
     {
        @Override
        public void actionPerformed(ActionEvent e)
        {
           // TODO
        }
     });
  }

  /**
   * {@inheritDoc}
   */
   @Override
   public void setObject(Object o)
   {
      device = (Device) o;

      setName(I18n.formatMessage("DeviceEditor.Title", new Object[]{ device.getPhysicalAddress(), device.getCatalogEntry().getName() }));
      paramEdit.setDevice(device);
   }
}
