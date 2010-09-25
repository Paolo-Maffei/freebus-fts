package org.freebus.fts.pages.deviceeditor;

import org.freebus.fts.pages.DeviceDetails;
import org.freebus.fts.project.Device;

/**
 * Interface for parts of the {@link DeviceDetails} that need to know about the
 * edited device.
 */
public interface DeviceEditorComponent
{
   /**
    * Set the edited device. Called by the device editor when the device
    * changes.
    */
   public void setDevice(Device device);

   /**
    * A component of the project was changed.
    */
   public void componentChanged(Object obj);
}
