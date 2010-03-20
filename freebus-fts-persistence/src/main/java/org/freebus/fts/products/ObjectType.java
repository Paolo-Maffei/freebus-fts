package org.freebus.fts.products;


/**
 * Types of communication objects.
 */
public enum ObjectType
{
   /**
    * 1 bit
    */
   BITS_1("1 Bit", 1),

   /**
    * 2 bits
    */
   BITS_2("2 Bit", 2),

   /**
    * 3 bits
    */
   BITS_3("3 Bit", 3),

   /**
    * 4 bits
    */
   BITS_4("4 Bit", 4),

   /**
    * 5 bits
    */
   BITS_5("5 Bit", 5),

   /**
    * 6 bits
    */
   BITS_6("6 Bit", 6),

   /**
    * 7 bits
    */
   BITS_7("7 Bit", 7),

   /**
    * 1 byte
    */
   BYTES_1("1 Byte", 8),

   /**
    * 1 byte
    */
   BYTES_2("2 Bytes", 16),

   /**
    * 3 bytes
    */
   BYTES_3("3 Bytes", 24),

   /**
    * 4 bytes
    */
   BYTES_4("4 Bytes", 32),

   /**
    * 6 bytes
    */
   BYTES_6("6 Bytes", 48),

   /**
    * 8 bytes
    */
   BYTES_8("8 Bytes", 64),

   /**
    * 10 bytes
    */
   BYTES_10("10 Bytes", 80),

   /**
    * 14 bytes
    */
   BYTES_14("14 Bytes", 112);


   private final String name;
   private final int bitLength;

   /**
    * @return the id
    */
   public int getId()
   {
      return ordinal();
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the bitLength
    */
   public int getBitLength()
   {
      return bitLength;
   }

   /*
    * Internal constructor
    */
   private ObjectType(String name, int bitLength)
   {
      this.name = name;
      this.bitLength = bitLength;
   }
}
