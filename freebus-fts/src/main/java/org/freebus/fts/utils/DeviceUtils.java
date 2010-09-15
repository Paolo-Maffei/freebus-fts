package org.freebus.fts.utils;

import java.util.List;
import java.util.Vector;

import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.types.ObjectType;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;

/**
 * Utility methods for {@link Device} handling.
 */
public final class DeviceUtils
{
   /**
    * Create a list of {@link ObjectDescriptor object descriptors} for the given
    * device.
    * 
    * @param device - the device to process.
    * @return The list of object descriptors for the device.
    */
   public static List<ObjectDescriptor> getObjectDescriptors(Device device)
   {
      final List<DeviceObject> devObjects = device.getDeviceObjects();
      final List<ObjectDescriptor> descriptors = new Vector<ObjectDescriptor>(devObjects.size());

      for (DeviceObject devObject : devObjects)
      {
         final CommunicationObject comObject = devObject.getComObject();
         final ObjectDescriptor od = new ObjectDescriptor();
         od.setPriority(comObject.getObjectPriority());

         ObjectType type = devObject.getObjectType();
         if (type == null)
            type = comObject.getObjectType();
         od.setType(type);

         od.setCommEnabled(devObject.isComm());
         od.setReadEnabled(devObject.isRead());
         od.setWriteEnabled(devObject.isWrite());
         od.setTransEnabled(devObject.isTrans());

         // TODO what has to be set into od.setDataPointer() ?

         descriptors.add(od);
      }

      return descriptors;
   }

   /*
    * Disabled
    */
   private DeviceUtils()
   {
   }
}
