package org.freebus.knxcomm.application;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor0;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor2;

/**
 * Read a device descriptor.
 */
public class DeviceDescriptorRead extends AbstractApplication
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
    * Create a device descriptor object for device descriptor type 0.
    */
   public DeviceDescriptorRead()
   {
      this(0);
   }

   /**
    * Create a device descriptor object. Known descriptor types are type 0 and
    * type 2. The returned device descriptors will then be
    * {@link DeviceDescriptor0} or {@link DeviceDescriptor2}.
    *
    * @param descriptorType - the device descriptor type. Known types are type 0
    *           and type 2.
    */
   public DeviceDescriptorRead(int descriptorType)
   {
      setDescriptorType(descriptorType);
   }

   /**
    * @return the device descriptor type
    */
   public int getDescriptorType()
   {
      return descriptorType;
   }

   /**
    * Set the device descriptor type to read. Known types are type 0 and type 2.
    * The returned device descriptors will then be {@link DeviceDescriptor0} or
    * {@link DeviceDescriptor2}.
    *
    * @param descriptorType - the descriptor type to set
    */
   public void setDescriptorType(int descriptorType)
   {
      this.descriptorType = descriptorType;
   }

   /**
    * @return The type of the application:
    *         {@link ApplicationType#DeviceDescriptor_Read}.
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.DeviceDescriptor_Read;
   }

   /**
    * @return The {@link #getDescriptorType() descriptor type} as APCI value.
    */
   @Override
   public final int getApciValue()
   {
      return descriptorType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(DataInput in, int length) throws IOException
   {
      descriptorType = super.getApciValue() & DESCRIPTOR_TYPE_MASK;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeData(DataOutput out) throws IOException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return getType().getApci() | descriptorType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof DeviceDescriptorRead))
         return false;

      final DeviceDescriptorRead oo = (DeviceDescriptorRead) o;
      return descriptorType == oo.descriptorType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getType().toString() + " type #" + descriptorType;
   }
}
