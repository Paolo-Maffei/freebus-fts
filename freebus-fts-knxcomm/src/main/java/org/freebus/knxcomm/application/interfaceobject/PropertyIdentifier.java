package org.freebus.knxcomm.application.interfaceobject;

/**
 * Interface object property identifiers.
 *
 * @see in KNX 3/5/1 Resources
 */
public enum PropertyIdentifier
{
   /*
    *   Device Descriptor Type 2 Properties 
    */

   /**
    * Object type.
    * Data type: unsigned int.
    */
   ObjectType(1),

   /**
    * Object name.
    * Data type: string
    */
   ObjectName(2),

   /**
    * Load state control.
    * Data type: pdt_control.
    */
   LoadStateControl(5),

   /**
    * Run state control.
    * Data type: pdt_control.
    */
   RunStateControl(6),

   /**
    * Table reference.
    * Data type: unsigned int.
    */
   TableReference(7),

   /**
    * Service control.
    * Data type: pdt_control.
    */
   ServiceControl(8),

   /**
    * Firmware revision.
    * Data type: unsigned char.
    */
   FirmwareRevision(9),

   /**
    * Supported services.
    * Data type: none.
    */
   ServicesSupported(10),

   /**
    * Serial number.
    * Data type: 6 bytes.
    */
   SerialNumber(11),

   /**
    * Manufacturer ID.
    * Data type: unsigned int.
    */
   ManufacturerId(12),

   /**
    * Program version.
    * Data type: unsigned int.
    */
   ProgramVersion(13),

   /**
    * Device control field.
    * Data type: 1 byte.
    */
   DeviceControl(14),

   /**
    * Manufacturer specific order information.
    * Data type: 10 bytes.
    */
   OrderInfo(15),

   /**
    * PEI type.
    * Data type: 1 byte.
    */
   PeiType(16),

   /**
    * Settings of the hardware port configuration.
    * Data type: 1 byte.
    */
   PortConfiguration(17),

   /**
    * Poll group settings.
    * Data type: poll_group_settings.
    */
   PollGroupSettings(18),

   /**
    * Manufacturer data.
    * Data type: variable bytes.
    */
   ManufacturerData(19),

   /**
    * Descriptive information about a device.
    */
   Description(21),

   /**
    * Table.
    * Data type: interface object dependent.
    */
   Table(23),

   /**
    * Version information related to the interface object.
    * Data type: version (2 bytes).
    */
   Version(25),

   /**
    * Memory control block.
    * Data type: 8 bytes.
    */
   McbTable(27),

   /**
    * Error code.
    * Data type: 8 bit enum.
    */
   ErrorCode(28),

   /**
    * Object index.
    */
   ObjectIndex(29),

   /*
    * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    */

   /**
    * Extended frame format.
    */
   ExtendedFrameFormat(51),

   /**
    * Address Table Format 1.
    */
   AddrTab1(52),

   ;

   final int id;
   
   /*
    * Internal constructor
    */
   private PropertyIdentifier(int id)
   {
      this.id = id;
   }
}
