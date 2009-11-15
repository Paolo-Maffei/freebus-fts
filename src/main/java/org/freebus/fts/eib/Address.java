package org.freebus.fts.eib;

/**
 * An EIB/KNX bus address.
 */
public interface Address
{
   /**
    * @return the address as 16-bit number.
    */
   public int getAddr();

   /**
    * @return the address as 2-byte array.
    */
   public int[] getBytes();
}
