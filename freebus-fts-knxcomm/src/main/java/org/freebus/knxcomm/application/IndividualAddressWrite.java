package org.freebus.knxcomm.application;

import java.io.DataInput;
import java.io.IOException;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptorProperties;

/**
 * Set the physical address of all devices that are in programming mode. To be
 * sent as a broadcast to {@link PhysicalAddress#NULL 0.0.0}
 */
public class IndividualAddressWrite extends AbstractApplication
{
   private PhysicalAddress address = null;

   /**
    * Create an object with an undefined address.
    */
   public IndividualAddressWrite()
   {
   }

   /**
    * Create an empty instance, with the given address.
    */
   public IndividualAddressWrite(PhysicalAddress address)
   {
      this.address = address;
   }

   /**
    * @return The type of the application:
    *         {@link ApplicationType#IndividualAddress_Write}.
    */
   @Override
   public ApplicationType getType()
   {
      return ApplicationType.IndividualAddress_Write;
   }

   /**
    * Set the physical address.
    */
   public void setAddress(PhysicalAddress address)
   {
      this.address = address;
   }

   /**
    * @return the physical address
    */
   public PhysicalAddress getAddress()
   {
      return address;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(DataInput in, int length) throws IOException
   {
      address = new PhysicalAddress(in.readUnsignedShort());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      final int[] addrData = (address == null ? PhysicalAddress.NULL : address).getBytes();

      rawData[start++] = ApplicationType.IndividualAddress_Write.getApci() & 255;
      rawData[start++] = addrData[0];
      rawData[start++] = addrData[1];

      return 3;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDeviceDescriptorRequired()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDeviceDescriptorProperties(DeviceDescriptorProperties deviceDescriptorProperties)
   {
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ApplicationType getApplicationResponses()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getType().toString() + ": " + address;
   }
}
