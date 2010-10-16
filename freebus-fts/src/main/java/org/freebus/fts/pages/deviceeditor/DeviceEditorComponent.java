package org.freebus.fts.pages.deviceeditor;

import org.freebus.fts.backend.DeviceController;
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
    * 
    * @param device - the device.
    * @param adapter - the device adapter.
    */
   public void setDevice(Device device, DeviceController adapter);

   /**
    * A component of the project was changed.
    */
   public void componentChanged(Object obj);
}
