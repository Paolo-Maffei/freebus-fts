package org.freebus.fts.common.address;

/**
 * An EIB/KNX bus address.
 */
public interface Address
{
   /**
    * @return the address as 16-bit number.
    */
   int getAddr();

   /**
    * @return the address as 2-byte array.
    */
   int[] getBytes();
}
