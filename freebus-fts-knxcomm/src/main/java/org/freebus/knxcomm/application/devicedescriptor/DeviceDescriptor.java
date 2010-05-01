package org.freebus.knxcomm.application.devicedescriptor;

import org.freebus.knxcomm.application.DeviceDescriptorResponse;

/**
 * A device descriptor. Used in {@link DeviceDescriptorResponse}.
 *
 * @see DeviceDescriptor0
 * @see DeviceDescriptor2
 */
public interface DeviceDescriptor
{
   /**
    * @return the numerical descriptor type.
    */
   public int getTypeCode();

   /**
    * @return the object as data byte array.
    */
   public byte[] toByteArray();
}
