package org.freebus.fts.service.job.entity;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.service.job.device.DeviceScannerJob;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;

/**
 * Information about a (physical) KNX device that e.g. the
 * {@link DeviceScannerJob} collects.
 */
public class DeviceInfo implements Comparable<DeviceInfo>
{
   private final PhysicalAddress address;
   private DeviceDescriptor descriptor;
   private int deviceType = -1;
   private int manufacturerId = -1;

   /**
    * Create a device info object.
    *
    * @param address - the physical address of the device.
    */
   public DeviceInfo(PhysicalAddress address)
   {
      this.address = address;
   }

   /**
    * @return The physical address of the device.
    */
   public PhysicalAddress getAddress()
   {
      return address;
   }

   /**
    * @return The device descriptor. May be null.
    */
   public DeviceDescriptor getDescriptor()
   {
      return descriptor;
   }

   /**
    * Set the device descriptor.
    *
    * @param descriptor - the device descriptor to set.
    */
   public void setDescriptor(DeviceDescriptor descriptor)
   {
      this.descriptor = descriptor;
   }

   /**
    * @return The device type, or -1 if unknown.
    */
   public int getDeviceType()
   {
      return deviceType;
   }

   /**
    * Set the device type.
    *
    * @param deviceType - the device type to set, -1 for unknown.
    */
   public void setDeviceType(int deviceType)
   {
      this.deviceType = deviceType;
   }

   /**
    * @return The manufacturer ID, or -1 if unknown.
    */
   public int getManufacturerId()
   {
      return manufacturerId;
   }

   /**
    * Set the manufacturer ID.
    *
    * @param manufacturerId - the manufacturer ID to set, -1 for unknown.
    */
   public void setManufacturerId(int manufacturerId)
   {
      this.manufacturerId = manufacturerId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return address.getAddr();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (!(o instanceof DeviceInfo))
         return false;

      final DeviceInfo oo = (DeviceInfo) o;
      return address.equals(oo.address) && (descriptor == null ? oo.descriptor == null : descriptor.equals(oo.descriptor));
   }

   /**
    * Compare by address.
    */
   @Override
   public int compareTo(DeviceInfo o)
   {
      return address.getAddr() - o.address.getAddr();
   }

   /**
    * @return True if the info is complete.
    */
   public boolean isComplete()
   {
      return address != null && descriptor != null && deviceType != -1 && manufacturerId != -1;
   }
}
