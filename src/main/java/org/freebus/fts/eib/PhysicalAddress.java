package org.freebus.fts.eib;

/**
 * A physical (device) address on the EIB/KNX bus.
 */
public final class PhysicalAddress implements Address
{
   private final int addr;

   /**
    * A null address (0.0.0)
    */
   public static final PhysicalAddress NULL = new PhysicalAddress();

   /**
    * The first regular address (1.1.1)
    */
   public static final PhysicalAddress ONE = new PhysicalAddress(1, 1, 1);

   /**
    * Create an empty address object.
    */
   public PhysicalAddress()
   {
      addr = 0;
   }

   /**
    * Create an address object.
    * 
    * @param zone - the zone address (0..15)
    * @param line - the line address (0..15)
    * @param node - the node address (0..255)
    */
   public PhysicalAddress(int zone, int line, int node)
   {
      this.addr = createAddr(zone, line, node);
   }

   /**
    * Create an address object from high-byte and low-byte.
    * 
    * @param high - the high-byte of the address.
    * @param low - the low-byte of the address.
    */
   public PhysicalAddress(int high, int low)
   {
      this.addr = ((high & 255) << 8) | (low & 255);
   }

   /**
    * Create an address object.
    * 
    * @param addr - the address as 16-bit number.
    */
   public PhysicalAddress(int addr)
   {
      this.addr = addr & 0xffff;
   }

   /**
    * @return the address as 16-bit number.
    */
   @Override
   public int getAddr()
   {
      return addr;
   }

   /**
    * @return the zone component of the address (0..15)
    */
   public int getZone()
   {
      return addr >> 12;
   }

   /**
    * @return the line component of the address (0..15)
    */
   public int getLine()
   {
      return (addr >> 8) & 15;
   }

   /**
    * @return the node component of the address (0..255)
    */
   public int getNode()
   {
      return addr & 255;
   }

   /**
    * Create a 16-bit physical address from the address components.
    * 
    * @param zone - the zone address (0..15)
    * @param line - the line address (0..15)
    * @param node - the node address (0..255)
    */
   public static int createAddr(int zone, int line, int node)
   {
      return ((zone & 15) << 12) | ((line & 15) << 8) | (node & 255);
   }

   /**
    * Test if the address components are within their allowed range.
    * 
    * @return true if all components are valid.
    * @param zone - the zone address (0..15)
    * @param line - the line address (0..15)
    * @param node - the node address (0..255)
    */
   public static boolean isValid(int zone, int line, int node)
   {
      return zone>=0 && zone<=15 && line>=0 && line<=15 && node>=0 && node<=255;
   }

   /**
    * @return a hash code for the address.
    */
   @Override
   public int hashCode()
   {
      return addr;
   }

   /**
    * Compare two addresses.
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this) return true;
      final PhysicalAddress oo = (PhysicalAddress) o;
      if (oo == null) return false;
      return oo.addr == addr;
   }

   /**
    * @return the object in human readable form: "xx.xx.xxx"
    */
   @Override
   public String toString()
   {
      return String.format("%d.%d.%d", addr >> 12, (addr >> 8) & 15, addr & 255);
   }

   /**
    * Parse the given string and return a physical-address.
    *
    * @param string - the string in the format "zone.line.node".
    * @return the group address, or null if the given string has an invalid format. 
    */
   public static PhysicalAddress valueOf(String str)
   {
      final int pos1 = str.indexOf('.');
      if (pos1 <= 0) return null;

      final int pos2 = str.indexOf('.', pos1 + 1);
      if (pos2 < 0) return null;

      return new PhysicalAddress(Integer.parseInt(str.substring(0, pos1)),
            Integer.parseInt(str.substring(pos1 + 1, pos2)), Integer.parseInt(str.substring(pos2 + 1)));
   }
}
