package org.freebus.knxcomm.application;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Types of telegram applications. The types are stored in the APCI field of a
 * telegram.
 */
public enum ApplicationType
{
   /**
    * Read the value of the ASAP (communication object).
    * <p>
    * Multicast - To be sent to a group address.
    * <p>
    * Response: {@link #GroupValue_Response}.
    */
   GroupValue_Read(0x000, 10, 0, 0),

   /**
    * The response to the {@link #GroupValue_Read} request. The data depends on
    * the type of the communication object.
    * <p>
    * Multicast - To be sent to a group address.
    * <p>
    * 0-14 data bytes: the value of the communication object.
    */
   GroupValue_Response(0x040, 4, 0, 14),

   /**
    * Send an update of an ASAP (communication object) to all connected ASAPs.
    * The data depends on the type of the communication object.
    * <p>
    * 0-14 data bytes: data.
    */
   GroupValue_Write(0x080, 4, 1, 14),

   /**
    * Set the physical address of all devices that are in programming mode.
    * Broadcast.
    * <p>
    * 2 data bytes: the new physical address (high/low byte).
    */
   IndividualAddress_Write(0x0c0, 10, 4, 4, IndividualAddressWrite.class),

   /**
    * Read the physical address of all devices that are in programming mode.
    * <p>
    * Broadcast. No data bytes.
    * <p>
    * Response: {@link #IndividualAddress_Response}.
    */
   IndividualAddress_Read(0x100, 10, 0, 0, IndividualAddressRead.class),

   /**
    * The response to {@link #IndividualAddressRead}.
    * <p>
    * Broadcast. No data bytes.
    */
   IndividualAddress_Response(0x140, 10, 0, 0, IndividualAddressResponse.class),

   /**
    * Read the individual address of a device which is identified by a given KNX
    * serial number.
    * <p>
    * Broadcast. 6 data bytes: KNX serial number.
    * <p>
    * Response: {@link #IndividualAddressSerialNumber_Response}.
    */
   IndividualAddressSerialNumber_Read(0x3dc, 10, 6, 6),

   /**
    * The response to the {@link #IndividualAddressSerialNumber_Read} request.
    * <p>
    * 10 data bytes: 6 bytes KNX serial number, 2 bytes address high/low, 2
    * bytes reserved.
    */
   IndividualAddressSerialNumber_Response(0x3dd, 10, 10, 10),

   /**
    * Set the serial number of a device which is identified by a given KNX
    * serial number. Broadcast.
    * <p>
    * 12 data bytes: 6 bytes KNX serial number, 2 bytes new address high/low, 4
    * bytes reserved.
    */
   IndividualAddressSerialNumber_Write(0x3de, 10, 12, 12),

   /**
    * Read the value of the A/D converter.
    * <p>
    * 2 data bytes: channel number, read count.
    * <p>
    * Response: {@link #ADC_Response}.
    */
   ADC_Read(0x180, 4, 3, 3),

   /**
    * Response to the {@link #ADC_Read} request.
    * <p>
    * 4 data bytes: channel number, read count, sum of A/D converter values
    * high/low.
    */
   ADC_Response(0x1c0, 4, 4, 4),

   /**
    * Read application memory.
    * <p>
    * 3 data bytes: number of bytes to read (1..63), 2-byte address.
    * <p>
    * Response: {@link #Memory_Response}.
    */
   Memory_Read(0x200, 6, 3, 3),

   /**
    * Response to {@link #Memory_Read}.
    * <p>
    * 4+ data bytes: number of memory bytes (1..63), 2-byte address, memory
    * contents.
    */
   Memory_Response(0x240, 6, 3, 15),

   /**
    * Write application memory.
    * <p>
    * 3+ data bytes: number of bytes to read (1..63), 2-byte address, memory
    * contents.
    */
   Memory_Write(0x280, 6, 3, 15),

   /**
    * Read user-data memory.
    * <p>
    * 3 data bytes: 4-bit address extension + 4-bit number of bytes, 2-byte
    * address.
    * <p>
    * Response: {@link #UserMemory_Response}.
    */
   UserMemory_Read(0x2c0, 10, 3, 3),

   /**
    * Response to {@link #UserMemory_Read}.
    * <p>
    * 3+ data bytes: 4-bit address extension + 4-bit number of bytes, 2-byte
    * address, memory contents.
    */
   UserMemory_Response(0x2c1, 10, 3, 15),

   /**
    * Write user-data memory.
    * <p>
    * 3+ data bytes: 4-bit address extension + 4-bit number of bytes, 2-byte
    * address, memory contents.
    */
   UserMemory_Write(0x2c2, 10, 3, 15),

   /**
    * Write a bit in the user-data memory.
    *
    * @deprecated according to the KNX standard.
    */
   UserMemoryBit_Write(0x2c4, 10, -1, -1),

   /**
    * Read the manufacturer info.
    * <p>
    * Response: {@link #UserManufacturerInfo_Response}.
    */
   UserManufacturerInfo_Read(0x2c5, 10, 0, 0),

   /**
    * Response to the {@link #UserManufacturerInfo_Read} request.
    * <p>
    * 3 data bytes: manufacturer id, 2 bytes manufacturer specific.
    */
   UserManufacturerInfo_Response(0x2c6, 10, 3, 3),

   /**
    * Call a function property of an interface object.
    * <p>
    * 3+ data bytes: object index, property id, data.
    */
   FunctionPropertyCommand(0x2c7, 10, 3, 16),

   /**
    * Read the state of a function property of an interface object.
    * <p>
    * 3+ data bytes: object index, property id, data.
    * <p>
    * Response: {@link #FunctionPropertyState_Response}.
    */
   FunctionPropertyState_Read(0x2c8, 10, 3, 16),

   /**
    * Response to the {@link #FunctionPropertyState_Read} request.
    * <p>
    * 4+ data bytes: object index, property id, return code, data.
    */
   FunctionPropertyState_Response(0x2c9, 10, 4, 16),

   /**
    * Read the device descriptor of a remote management server.
    * <p>
    * One data byte: the descriptor type to read.
    * <p>
    * Response: {@link #DeviceDescriptor_Response}.
    */
   DeviceDescriptor_Read(0x300, 10, 1, 1, DeviceDescriptorRead.class),

   /**
    * Response to {@link #DeviceDescriptor_Read}. Also called
    * DeviceDescriptor_InfoReport.
    */
   DeviceDescriptor_Response(0x340, 10, 1, 15, DeviceDescriptorResponse.class),

   /**
    * Reset / restart the device.
    */
   Restart(0x380, 10, 0, 0, Restart.class),

   /** @deprecated according to the KNX standard. */
   Open_Routing_Table_Request(0x3c0, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Read_Routing_Table_Request(0x3c1, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Read_Routing_Table_Response(0x3c2, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Write_Routing_Table_Request(0x3c3, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Read_Router_Memory_Request(0x3c8, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Read_Router_Memory_Response(0x3c9, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Write_Router_Memory_Request(0x3ca, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Read_Router_Status_Request(0x3cd, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Read_Router_Status_Response(0x3ce, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   Write_Router_Status_Request(0x3cf, 10, -1, -1),

   /** @deprecated according to the KNX standard. */
   MemoryBit_Write(0x3d0, 10, -1, -1),

   /**
    * Send a key to the communication partner to gain access.
    * <p>
    * 5 data bytes: zero, 4-byte key.
    * <p>
    * Response: {@link #Authorize_Response}.
    */
   Authorize_Request(0x3d1, 10, 5, 5),

   /**
    * Response to the {@link #Authorize_Request} request.#
    * <p>
    * 1 data byte: access level.
    */
   Authorize_Response(0x3d2, 10, 1, 1),

   /**
    * Modify or delete a key which is associated to a certain access level in
    * the communication parner.
    * <p>
    * 5 data bytes: access level, 4-byte key.
    */
   Key_Write(0x3d3, 10, -1, -1),

   /**
    * Response to {@link #Key_Write}.
    * <p>
    * 1 data byte: access level.
    */
   Key_Response(0x3d4, 10, -1, -1),

   /**
    * Read the value of a property of an interface object.
    * <p>
    * 4 data bytes: object index, property id, 4bit number of requested elems,
    * 12bit start index
    */
   PropertyValue_Read(0x3d5, 10, 4, 4),

   /**
    * Response to the {@link #PropertyValue_Read} request.
    * <p>
    * 4+ data bytes: object index, property id, 4bit number of requested elems,
    * 12bit start index, the data.
    */
   PropertyValue_Response(0x3d6, 10, 4, 16),

   /**
    * Set the value of a property of an interface object.
    * <p>
    * 4+ data bytes: object index, property id, 4bit number of requested elems,
    * 12bit start index, the data.
    */
   PropertyValue_Write(0x3d7, 10, 4, 16),

   /**
    * Read the description of the property of an interface object.
    * <p>
    * 3 data bytes: object index, property id, property index.
    */
   PropertyDescription_Read(0x3d8, 10, 3, 3),

   /**
    * Response to the {@link #PropertyDescription_Read} request.
    * <p>
    * Data bytes: object index, property id, property index, property type,
    * 4bits reserved, 12 bits max number of elems, 4bits read-level access,
    * 4bits write-level access.
    */
   PropertyDescription_Response(0x3d9, 10, 7, 7),

   /**
    * Read the configuration of a network parameter.
    * <p>
    * Broadcast.
    */
   NetworkParameter_Read(0x3da, 10, 4, 16),

   /**
    * The response to the {@link #NetworkParameter_Read} request.
    */
   NetworkParameter_Response(0x3da, 10, 4, 16),

   /**
    * Set a network configuration parameter on one or multiple management
    * servers. Either point-to-point connection-less or broadcast.
    */
   NetworkParameter_Write(0x3e4, 10, 4, 16),

   /**
    * Reserved for coupler-specific use.
    */
   Coupler_Specific_Reserved(0x3df, 10, -1, -1),

   /**
    * Set the domain address of a communication partner. 2+ data bytes: the
    * domain address (high to low byte).
    */
   DomainAddress_Write(0x3e0, 10, 2, 16),

   /**
    * Read the domain address of a communication partner.
    */
   DomainAddress_Read(0x3e1, 10, 0, 0),

   /**
    * Response to the {@link #DomainAddress_Read} request. 2+ data bytes: the
    * domain address (high to low byte).
    */
   DomainAddress_Response(0x3e2, 10, 2, 16),

   /**
    * A {@link #DomainAddress_Read} request, but more specific. Read the domain
    * address of a communication partner that is identified within a service.
    * <p>
    * Broadcast.
    */
   DomainAddressSelective_Read(0x3e3, 10, 2, 16),

   /**
    * Read the domain address of a communication partner. The communication
    * partner is identified by its KNX serial number.
    * <p>
    * 6 data bytes: the KNX serial number.
    */
   DomainAddressSerialNumber_Read(0x3ec, 10, 6, 6),

   /**
    * Response to {@link #DomainAddressSerialNumber_Read}.
    * <p>
    * 8+ data bytes: 6 bytes KNX serial number, rest is domain address (high to
    * low byte).
    */
   DomainAddressSerialNumber_Response(0x3ed, 10, -1, -1),

   /**
    * Set the domain address of a communication partner which is identified by
    * its KNX serial number.
    * <p>
    * 8+ data bytes: 6 bytes KNX serial number, rest is domain address (high to
    * low byte).
    */
   DomainAddressSerialNumber_Write(0x3ee, 10, -1, -1),

   /**
    * Read the links to a specific group object in a communication partner.
    * <p>
    * 2 data bytes: group object number, 4bits zero, 4bits start index.
    */
   Link_Read(0x3e5, 10, -1, -1),

   /**
    * Response to {@link #Link_Read}.
    * <p>
    * Data bytes: group object number, 4bit sending address, 4bit start address,
    * 0..14 bytes group address list.
    */
   Link_Response(0x3e6, 10, 3, 14),

   /**
    * Add a single group address to a group object or remove a group address
    * from a group object.
    * <p>
    * 4 data bytes: group object number, flags, group address high/low.
    */
   Link_Write(0x3e7, 10, 4, 4),

   /**
    * Group properties value-read.
    */
   GroupPropValue_Read(0x3e8, 10, -1, -1),

   /**
    * Response to {@link #GroupPropValue_Read}.
    */
   GroupPropValue_Response(0x3e9, 10, -1, -1),

   /**
    * Group properties value-write
    */
   GroupPropValue_Write(0x3ea, 10, -1, -1),

   /**
    * Group properties information report.
    */
   GroupPropValue_InfoReport(0x3eb, 10, -1, -1),

   /**
    * FTS internal value, to indicate an invalid or empty application type.
    */
   None(0xffff, 10, 0, 0);

   /**
    * The contents of the APCI field.
    */
   public final int apci;

   /**
    * Number of bits that the APCI type requires (1..10).
    */
   public final int bits;

   /**
    * Minimum length for data in the telegram in bytes. -1 if unspecified.
    */
   public final int minData;

   /**
    * Maximum length for data in the telegram in bytes. -1 if unspecified.
    */
   public final int maxData;

   /**
    * The class for objects of this application type.
    */
   public final Class<? extends Application> clazz;

   // The bit-masks for the values of the APCI-bits
   private static final int[] apciMasks = new int[] { 0, 0x200, 0x300, 0x380, 0x3c0, 0x3e0, 0x3f0, 0x3f8, 0x3fc, 0x3fe,
         0x3ff };

   /**
    * @return the bit-mask for the APCI field.
    */
   public int getMask()
   {
      return apciMasks[bits];
   }

   /**
    * @return the bit-mask for the data area in the APCI bytes. Zero indicates
    *         that no data can be stored in the APCI byte.
    */
   public int getDataMask()
   {
      return 0x3ff & ~apciMasks[bits];
   }

   /**
    * @return the application type in human readable form.
    */
   @Override
   public String toString()
   {
      return name() + String.format(" [%03x]", apci);
   }

   /**
    * @return the application type for the given APCI field.
    * @throws InvalidDataException
    */
   public static ApplicationType valueOf(int apci) throws InvalidDataException
   {
      for (ApplicationType a : values())
      {
         if ((apci & apciMasks[a.bits]) == a.apci)
            return a;
      }

      throw new InvalidDataException("No telegram application type found that matches the APCI field contents", apci);
   }

   /*
    * Internal constructor.
    */
   private ApplicationType(int apci, int apciBits, int minData, int maxData)
   {
      this(apci, apciBits, minData, maxData, null);
   }

   /*
    * Internal constructor.
    */
   private ApplicationType(int apci, int apciBits, int minData, int maxData, Class<? extends Application> clazz)
   {
      this.apci = apci;
      this.bits = apciBits;
      this.minData = minData;
      this.maxData = maxData;
      this.clazz = clazz;
   }
}
