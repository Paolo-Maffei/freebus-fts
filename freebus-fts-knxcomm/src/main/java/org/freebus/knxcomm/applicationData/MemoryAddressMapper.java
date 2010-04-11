package org.freebus.knxcomm.applicationData;

import java.util.ArrayList;
import java.util.Properties;

public class MemoryAddressMapper
{

   private static final long serialVersionUID = -4685160705756019114L;

   private ArrayList<MemoryAddress> memoryAddressList;

   /**
    * Creates the MemoryAddressList with Parameters from the Properties File
    * 
    * @param deviceProperties
    */
   protected MemoryAddressMapper(Properties deviceProperties)
   {
      String strlength;
      String strAddress;
      this.memoryAddressList = new ArrayList<MemoryAddress>();
      for (MemoryAddressTypes mat : MemoryAddressTypes.values())
      {
         strAddress = deviceProperties.getProperty("MemoryAddress." + mat.toString() + "Address");
         strlength = deviceProperties.getProperty("MemoryAddress." + mat.toString() + "Length");
         if (strAddress != null)
         {
            MemoryAddress memoryAddress = new MemoryAddress(AddressString2intAdr(strAddress), Integer
                  .parseInt(strlength), mat);

            memoryAddressList.add(memoryAddress);
         }
      }

   }

   /**
    * Internal converter a 4 Digits long String of Hex chars to one integer
    * value
    * 
    * @param adr - String with the memory address
    * @return integer value
    */
   public int AddressString2intAdr(String adr)
   {
      char[] HEX_String_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
      int x = 0;
      int y;

      for (int i = 0; i < adr.length(); i++)
      {
         int j = 0;
         for (char s : HEX_String_TABLE)
         {
            char a = adr.charAt(i);

            if (s == a)
            {
               y = j << (adr.length() - 1 - i) * 4;
               x = x + y;
            }
            j++;
         }

      }
      return x;
   }

   /**
    * @param Address - The memory address as int array
    * @return The MemoryAddress object for the memory address
    */
   public MemoryAddress getMemoryAddress(int[] Address)
   {
      int memadr = memadrArray2int(Address);
      for (MemoryAddress m : memoryAddressList)
      {
         memadrArray2int(Address);
         if (m.getAdress() <= memadr && m.getAdress() + m.getLength() > memadr)
            return m;

      }

      return null;
   }

   /**
    * @param AddressType - a MemoryAddressTypes
    * @return The MemoryAddress object for the memory address type
    */
   public MemoryAddress getMemoryAddress(MemoryAddressTypes AddressType)
   {
      for (MemoryAddress m : memoryAddressList)
      {

         if (m.getMemoryAddressType() == AddressType)
            return m;

      }

      return null;
   }

   /**
    * Internal converter for integer array to an integer value
    * 
    * @param adr - the integer array
    * @return integer value
    */
   private int memadrArray2int(int[] adr)
   {
      int a = adr[0];
      a = a << 8;
      a = a + adr[1];
      return a;

   }

}
