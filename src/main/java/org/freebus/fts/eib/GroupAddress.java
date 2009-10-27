package org.freebus.fts.eib;

/**
 * A group address on the EIB/KNX bus.
 */
public final class GroupAddress implements Address
{
   private final int addr;

   /**
    * Create an empty address object.
    */
   public GroupAddress()
   {
      addr = 0;
   }

   /**
    * Create a group address object.
    * 
    * @param main - the main-group address (0..15)
    * @param middle - the middle-group address (0..7)
    * @param sub - the sub-group address (0..255)
    */
   public GroupAddress(int main, int middle, int sub)
   {
      this.addr = createAddr(main, middle, sub);
   }

   /**
    * Create a group address object from high-byte and low-byte.
    * 
    * @param high - the high-byte of the address.
    * @param low - the low-byte of the address.
    */
   public GroupAddress(int high, int low)
   {
      this.addr = ((high & 127) << 8) | (low & 255);
   }

   /**
    * Create an address object.
    * 
    * @param addr - the address as 16-bit number.
    */
   public GroupAddress(int addr)
   {
      this.addr = addr & 0x7fff;
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
    * @return the main-group component of the address (0..15)
    */
   public int getMain()
   {
      return addr >> 11;
   }

   /**
    * @return the middle-group component of the address (0..7)
    */
   public int getMiddle()
   {
      return (addr >> 8) & 7;
   }

   /**
    * @return the sub-group component of the address (0..255)
    */
   public int getSub()
   {
      return addr & 255;
   }

   /**
    * Create a 16-bit group address from the address components.
    * 
    * @param main - the main-group address (0..15)
    * @param middle - the middle-group address (0..7)
    * @param sub - the sub-group address (0..255)
    */
   static public int createAddr(int main, int middle, int sub)
   {
      return ((main & 15) << 11) | ((middle & 7) << 8) | (sub & 255);
   }

   /**
    * Test if the address components are within their allowed range.
    * 
    * @return true if all components are valid.
    * @param main - the main-group address (0..15)
    * @param middle - the middle-group address (0..7)
    * @param sub - the sub-group address (0..255)
    */
   public static boolean isValid(int main, int middle, int sub)
   {
      return main>=0 && main<=15 && middle>=0 && middle<=7 && sub>=0 && sub<=255;
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
      final GroupAddress oo = (GroupAddress) o;
      if (oo == null) return false;
      return oo.addr == addr;
   }

   /**
    * @return the object in human readable form: "xx/x/xxx"
    */
   @Override
   public String toString()
   {
      return String.format("%d/%d/%d", addr >> 11, (addr >> 8) & 7, addr & 255);
   }
}
