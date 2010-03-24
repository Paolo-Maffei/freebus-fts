package org.freebus.knxcomm.application;

import org.freebus.knxcomm.telegram.InvalidDataException;


/**
 * Abstract base class for device descriptor read requests and responses.
 */
public abstract class DeviceDescriptor implements Application
{
   /**
    * An invalid device descriptor type.
    */
   final public static int INVALID_DESCRIPTOR_TYPE = 0x3f;

   /**
    * The bit mask for the device descriptor types.
    */
   final public static int DESCRIPTOR_TYPE_MASK = 0x3f;

   private int descriptorType;

   /**
    * Create a device descriptor object.
    *
    * @param descriptorType - the device descriptor type.
    */
   protected DeviceDescriptor(int descriptorType)
   {
      this.descriptorType = descriptorType;
   }

   /**
    * @return the device descriptor type
    */
   public int getDescriptorType()
   {
      return descriptorType;
   }

   /**
    * Set the device descriptor type.
    *
    * @param descriptorType - the descriptor type to set
    */
   public void setDescriptorType(int descriptorType)
   {
      this.descriptorType = descriptorType;
   }

   /**
    * @return The type of the application: {@link ApplicationType#DeviceDescriptor_Read}.
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.DeviceDescriptor_Read;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
      descriptorType = rawData[start] & DESCRIPTOR_TYPE_MASK;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final ApplicationType appType = getType();
      rawData[start] = appType.apci | (descriptorType & DESCRIPTOR_TYPE_MASK);
      return 1;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return getType().apci | descriptorType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof DeviceDescriptor))
         return false;

      final DeviceDescriptor oo = (DeviceDescriptor) o;
      return descriptorType == oo.descriptorType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getType().toString() + " descriptor #" + descriptorType;
   }
}
