package org.freebus.fts.project;

/**
 * An EIB bus device address.
 * Every device on the EIB bus as a unique physical address.
 */
public final class PhysicalAddress
{
   public static final PhysicalAddress NULL = new PhysicalAddress(0);
   
   public final int addr;
   
   /**
    * Create a bus address object.
    * Throws a runtime exception if the address is invalid.
    *
    * @param areaId is the area identifier (0..13)
    * @param lineId is the line identifier (0..15)
    * @param deviceId is the device identifier (0..255)
    */
   public PhysicalAddress(int areaId, int lineId, int deviceId)
   {
      if (!isValid(areaId, lineId, deviceId))
         throw new RuntimeException("Invalid physical address: "+Integer.toString(areaId)+'.'+Integer.toString(lineId)+'.'+Integer.toString(deviceId));

      addr = (areaId<<12) | (lineId<<8) | deviceId;
   }

   /**
    * Create a bus address object from the given 2-byte address.
    * 
    * @param addr is the 2-byte bus address.
    */
   public PhysicalAddress(int addr)
   {
      this.addr = addr;
   }
   
   /**
    * @return the area-id of the address.
    */
   public int getAreaId()
   {
      return (addr>>12) & 15;
   }

   /**
    * @return the line-id of the address.
    */
   public int getLineId()
   {
      return (addr>>8) & 15;
   }

   /**
    * @return the device-id of the address.
    */
   public int getDeviceId()
   {
      return addr & 255;
   }

   /**
    * Test if two objects are equal
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this) return true;
      if (!(o instanceof PhysicalAddress)) return false;
      final PhysicalAddress oo = (PhysicalAddress)o;
      return oo.addr == addr;
   }

   /**
    * @return the address in human readable form: "areaId.lineId.deviceId"
    */
   @Override
   public String toString()
   {
      return Integer.toString(getAreaId()) + '.' +
             Integer.toString(getLineId()) + '.' +
             Integer.toString(getDeviceId());
   }
   
   /**
    * Create a physical address.
    * Throws a runtime exception if the address is invalid.
    *
    * @param areaId is the area identifier (0..13)
    * @param lineId is the line identifier (0..15)
    * @param deviceId is the device identifier (0..255)
    * 
    * @return the 2-byte bus address.
    */
   public static int create(int areaId, int lineId, int deviceId)
   {
      if (!isValid(areaId, lineId, deviceId))
         throw new RuntimeException("Invalid physical address: "+Integer.toString(areaId)+'.'+Integer.toString(lineId)+'.'+Integer.toString(deviceId));

      return (areaId<<12) | (lineId<<8) | deviceId;
   }

   /**
    * Test if the given bus address is valid.
    *
    * @param areaId is the area identifier (0..15)
    * @param lineId is the line identifier (0..15)
    * @param deviceId is the device identifier (0..255)
    */
   public static boolean isValid(int areaId, int lineId, int deviceId)
   {
      return areaId>=0 && areaId<=15 && lineId>=0 && lineId<=15 && deviceId>=0 && deviceId<=255;
   }
}
