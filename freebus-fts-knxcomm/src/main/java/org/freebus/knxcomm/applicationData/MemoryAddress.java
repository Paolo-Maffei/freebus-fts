package org.freebus.knxcomm.applicationData;

public class MemoryAddress
{
   private int adress;
   private int length;
   private MemoryAddressTypes memoryAddressTypes;

   /**
    * Create a new MemoryAddress Object
    * 
    * @param adress - The MemoryAdress
    * @param length - The length of the memory block
    * @param memoryAddressType - The MemoryAddressTypes
    */
   protected MemoryAddress(int adress, int length, MemoryAddressTypes memoryAddressType)
   {
      this.memoryAddressTypes = memoryAddressType;
      this.adress = adress;
      this.length = length;
   }

   /**
    * 
    * @return the MemoryAddress as Integer
    */
   public int getAdress()
   {
      return adress;
   }

   /**
    * @return the length of the memory address block
    */
   public int getLength()
   {
      return length;
   }

   /**
    * @return the MemoryAddressType
    */
   public MemoryAddressTypes getMemoryAddressType()
   {
      return memoryAddressTypes;
   }

}
