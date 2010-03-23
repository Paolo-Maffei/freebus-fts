package org.freebus.knxcomm.application;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.telegram.ApplicationType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Set the physical address of all devices that are in programming mode.
 * To be sent as a broadcast to {@link PhysicalAddress#NULL 0.0.0}
 */
public class IndividualAddress_Write implements Application
{
   private PhysicalAddress address = null;

   /**
    * Create an empty instance, with an undefined address.
    */
   public IndividualAddress_Write()
   {
   }

   /**
    * Create an empty instance, with the given address.
    */
   public IndividualAddress_Write(PhysicalAddress address)
   {
      this.address = address;
   }

   /**
    * @return The type of the application: {@link ApplicationType#IndividualAddress_Write}.
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
   public void fromRawData(int[] data, int start, int length) throws InvalidDataException
   {
      // TODO Auto-generated method stub
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      // TODO Auto-generated method stub
      return 0;
   }
}
