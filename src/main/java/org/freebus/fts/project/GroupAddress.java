package org.freebus.fts.project;

/**
 * An EIB bus group address.
 * Every device on the EIB bus can have none, one, or more group addresses.
 * Group addresses are not unique.
 */
public final class GroupAddress
{
   public final int addr;
   
   /**
    * Create a group address object.
    * Throws a runtime exception if the address is invalid.
    *
    * @param mainId is the main group identifier (0..15)
    * @param subId is the sub group identifier (0..15)
    * @param deviceId is the group identifier (0..127)
    */
   public GroupAddress(int zoneId, int lineId, int deviceId)
   {
      if (!isValid(zoneId, lineId, deviceId))
         throw new RuntimeException("Invalid group address: "+Integer.toString(zoneId)+"/"+Integer.toString(lineId)+"/"+Integer.toString(deviceId));

      addr = (zoneId<<11) | (lineId<<7) | deviceId;
   }

   /**
    * Create a group address object from the given 2-byte address.
    * 
    * @param addr is the 2-byte group address.
    */
   public GroupAddress(int addr)
   {
      this.addr = addr;
   }
   
   /**
    * @return the zone-id of the address.
    */
   public int getAreaId()
   {
      return (addr>>11) & 15;
   }

   /**
    * @return the line-id of the address.
    */
   public int getLineId()
   {
      return (addr>>7) & 15;
   }

   /**
    * @return the device-id of the address.
    */
   public int getDeviceId()
   {
      return addr & 127;
   }

   /**
    * Test if two objects are equal
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this) return true;
      if (!(o instanceof GroupAddress)) return false;
      final GroupAddress oo = (GroupAddress)o;
      return oo.addr == addr;
   }

   /**
    * @return the address in human readable form: "zoneId.lineId.deviceId"
    */
   @Override
   public String toString()
   {
      return Integer.toString(getAreaId()) + "/" +
             Integer.toString(getLineId()) + "/" +
             Integer.toString(getDeviceId());
   }
   
   /**
    * Create a group address.
    * Throws a runtime exception if the address is invalid.
    *
    * @param zoneId is the zone identifier (0..15)
    * @param lineId is the line identifier (0..15)
    * @param deviceId is the device identifier (0..127)
    * 
    * @return the 2-byte group address.
    */
   public static int create(int zoneId, int lineId, int deviceId)
   {
      if (!isValid(zoneId, lineId, deviceId))
         throw new RuntimeException("Invalid group address: "+Integer.toString(zoneId)+"/"+Integer.toString(lineId)+"/"+Integer.toString(deviceId));

      return (zoneId<<11) | (lineId<<7) | deviceId;
   }

   /**
    * Test if the given group address is valid.
    *
    * @param zoneId is the zone identifier (0..15)
    * @param lineId is the line identifier (0..15)
    * @param deviceId is the device identifier (0..127)
    */
   public static boolean isValid(int zoneId, int lineId, int deviceId)
   {
      return zoneId>=0 && zoneId<=15 && lineId>=0 && lineId<=15 && deviceId>=0 && deviceId<=127;
   }
}
