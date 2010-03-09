package org.freebus.fts.pages;

import java.awt.BorderLayout;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.core.I18n;
import org.freebus.fts.project.Device;

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
     setName(I18n.formatMessage("DeviceEditor.Title", new Object[]{ "" }));

     add(paramEdit, BorderLayout.CENTER);
  }

  /**
   * {@inheritDoc}
   */
   @Override
   public void setObject(Object o)
   {
      device = (Device) o;

      setName(I18n.formatMessage("DeviceEditor.Title", new Object[]{ device.getCatalogEntry().getName() }));
      paramEdit.setDevice(device);
   }
}
