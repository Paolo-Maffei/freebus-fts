package org.freebus.fts.vdx;

/**
 * Section types for ETS3 file format.
 */
public enum VdxSectionType
{
   /**
    * Name of the manufacturer.
    * Referred by: CATALOG_ENTRY, HW_PRODUCT
    */
   MANUFACTURER(3, "MANUFACTURER_ID", "MANUFACTURER_NAME", true),

   /**
    * Functional entry
    */
   FUNCTIONAL_ENTITY(4, "FUNCTIONAL_ENTITY_ID", "FUNCTIONAL_ENTITY_NAME", true),

   /**
    * Type of the bus coupling unit (BCU)
    */
   BCU_TYPE(5, "BCU_TYPE_NUMBER", "BCU_TYPE_NAME", true),

   /**
    * An icon.
    * Referred by: CATALOG_ENTRY, HW_PRODUCT
    */
   SYMBOL(6, "SYMBOL_ID", "SYMBOL_NAME", true),

   /**
    * Hardware product.
    * Refers to: SYMBOL
    * Referred by: CATALOG_ENTRY
    */
   HW_PRODUCT(7, "PRODUCT_ID", "PRODUCT_NAME", true),
   
   /**
    * Product catalog entry.
    * Refers to: HW_PRODUCT, MANUFACTURER, SYMBOL
    */
   CATALOG_ENTRY(8, "CATALOG_ENTRY_ID", "ENTRY_NAME", true),

   /**
    * EIB-bus medium type
    */
   MEDIUM_TYPE(9, "MEDIUM_TYPE_NUMBER", "MEDIUM_TYPE_NAME", true),

   /**
    * EEprom mask
    */
   MASK(10, "MASK_ID", null, false),

   /**
    * Assembler type
    */
   ASSEMBLER(11, "ASSEMBLER_ID", "ASSEMBLER_NAME", true),

   /**
    * Application program
    */
   APPLICATION_PROGRAM(12, "PROGRAM_ID", "PROGRAM_NAME", false),

   /**
    * Virtual device description
    */
   VIRTUAL_DEVICE(13, "VIRTUAL_DEVICE_ID", "VIRTUAL_DEVICE_NAME", true),

   /**
    * Mask entry.
    */
   MASK_ENTRY(14, "MASK_ENTRY_ID", "MASK_ENTRY_NAME", true),

   /**
    * Device information
    */
   DEVICE_INFO(17, "DEVICE_INFO_ID", "DEVICE_INFO_NAME", true),

   /**
    * Language
    */
   LANGUAGE(18, "LANGUAGE_ID", "LANGUAGE_NAME", true),

   /**
    * Device information value
    */
   DEVICE_INFO_VALUE(19, "DEVICE_INFO_VALUE_ID", null, false),

   /**
    * Atomic parameter type
    */
   PARAMETER_ATOMIC_TYPE(20, "ATOMIC_TYPE_NUMBER", "ATOMIC_TYPE_NAME", true),

   /**
    * Parameter type
    */
   PARAMETER_TYPE(21, "PARAMETER_TYPE_ID", "PARAMETER_TYPE_NAME", true),

   /**
    * Parameter value
    */
   PARAMETER_VALUE(22, "PARAMETER_VALUE_ID", "DISPLAYED_VALUE", false),
   
   /**
    * Parameter calculation
    */
   PARAMETER_CALCULATION(23, "ID", "Name", false),

   /**
    * Parameter
    */
   PARAMETER(24, "PARAMETER_ID", "PARAMETER_NAME", false),

   /**
    * Object type
    */
   OBJECT_TYPE(25, "OBJECT_TYPE_CODE", "OBJECT_TYPE_NAME", true),
   
   /**
    * Object priority
    */
   OBJECT_PRIORITY(26, "OBJECT_PRIORITY_CODE", "OBJECT_PRIORITY_NAME", true),

   /**
    * Communication object
    */
   COMMUNICATION_OBJECT(27, "OBJECT_ID", "OBJECT_NAME", true),

   /**
    * Text attribute
    */
   TEXT_ATTRIBUTE(35, "TEXT_ATTRIBUTE_ID", "TEXT_ATTRIBUTE_TEXT", false),

   /**
    * Product description
    */
   PRODUCT_DESCRIPTION(36, "PRODUCT_DESCRIPTION_ID", null, true),

   /**
    * Help file-name
    */
   HELP_FILE(38, "HELP_FILE_ID", "HELP_FILE_NAME", false),

   /**
    * Specification text
    */
   SPECIFICATION_TEXT(39, "SPEC_TEXT_ID", "SPEC_TEXT_NAME", true),

   /**
    * Text to catent
    */
   TEXT_TO_CATENT(40, "TEXT_TO_CATENT_ID", null, true),
   
   /**
    * Product/program connector
    */
   PRODUCT_TO_PROGRAM(41, "PROD2PROG_ID", null, false),

   /**
    * Channel list
    */
   CHANNEL_LIST(42, "CHANNEL_LIST_ID", "CHANNEL_LIST_NAME", false),

   /**
    * Product/program/MT connector
    */
   PRODUCT_TO_PROGRAM_TO_MT(43, "PROD2PROG2MT_ID", null, false),

   /**
    * Program description
    */
   PROGRAM_DESCRIPTION(44, "PROGRAM_DESCRIPTION_ID", null, true),

   /**
    * S19 block
    */
   S19_BLOCK(45, "BLOCK_ID", "BLOCK_NAME", false),

   /**
    * S19 block paragraph
    */
   S19_BLOCK_PARAGRAPH(46, "S19_BLOCK_PARAGRAPH_ID", null, false),

   /**
    * Address fixup
    */
   ADDRESS_FIXUP(47, "FIXUP_ID", "FIXUP_NAME", false),

   /**
    * Device parameter
    */
   DEVICE_PARAMETER(51, "DEVICE_PARAMETER_ID", null, false),

   /**
    * Device object
    */
   DEVICE_OBJECT(49, "DEVICE_OBJECT_ID", null, false),

   /**
    * Mask feature
    */
   MASK_FEATURE(53, "MASK_FEATURE_ID", "MASK_FEATURE_NAME", false),

   /**
    * Mask/mask feature
    */
   MASK_TO_MASK_FEATURE(54, "MASK_TO_MASK_FEATURE_ID", null, false),

   /**
    * Program/mask feature
    */
   PROGRAM_TO_MASK_FEATURE(55, "PROGRAM_TO_MASK_FEATURE_ID", null, false),

   /**
    * Medium channel
    */
   MEDIUM_CHANNEL(56, "MEDIUM_CHANNEL_ID", null, true),

   /**
    * Program plugin
    */
   PROGRAM_PLUGIN(59, "PROGRAM_PLUGIN_ID", null, false),

   /**
    * Application program attributes
    */
   APPLICATION_PROGRAM_ATTRIBUTES(64, "ID", "AttributeName", false),

   /**
    * Baggage
    */
   BAGGAGE(65, "ID", null, false),

   /**
    * Baggage
    */
   APPLICATION_PROGRAM_BAGGAGE(66, "ID", null, false),

   /**
    * An unknown type
    */
   UNKNOWN(-1, null, null, true);
   
   /**
    * Id of the vdx section.
    */
   public final int id;
   
   /**
    * Name of the key field.
    */
   public final String keyField;

   /**
    * Name of the name field. May contain null.
    */
   public final String nameField;

   /**
    * Shall the section be read by VdxLoader?
    */
   public final boolean load;

   /**
    * @return the constant for the given section-id.
    */
   static public VdxSectionType valueOf(int sectionId)
   {
      for (VdxSectionType e: values())
         if (e.id==sectionId) return e;

      throw new IllegalArgumentException(
            "No vdx section enum const for section-id " + Integer.toString(sectionId));
   }

   /**
    * Test if sectionId is a known section-id.
    */
   static public boolean isValid(int sectionId)
   {
      for (VdxSectionType e: values())
         if (e.id==sectionId) return true;
      return false;
   }

   /*
    * Internal constructor.
    */
   private VdxSectionType(int id, String keyField, String nameField, boolean load)
   {
      this.id = id;
      this.keyField = keyField;
      this.nameField = nameField;
      this.load = load;
   }
}
