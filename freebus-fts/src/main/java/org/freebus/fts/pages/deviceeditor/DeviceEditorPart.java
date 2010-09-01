package org.freebus.fts.pages.deviceeditor;

import org.freebus.fts.pages.DeviceEditor;
import org.freebus.fts.project.Device;

/**
 * Interface for parts of the {@link DeviceEditor} that need to know about the
 * edited device.
 */
public interface DeviceEditorPart
{
   /**
    * Set the edited device. Called by the device editor when the device
    * changes.
    */
   public void setDevice(Device device);
}
