package org.freebus.fts.backend.memory;

import java.util.Arrays;

/**
 * A memory range.
 */
public class MemoryRange
{
   private final int start;
   private final byte[] data;
   private final MemoryValueType[] types;

   /**
    * Create a memory range. The memory is initialized with zero
    * values and {@link MemoryValueType#UNSET} value type.
    *
    * @param start - the start address of the memory range.
    * @param size - the size of the memory range in bytes.
    */
   public MemoryRange(int start, int size)
   {
      this.start = start;

      data = new byte[size];
      types = new MemoryValueType[size];

      clear();
   }

   /**
    * Create a memory range.  The memory is initialized with zero
    * values and {@link MemoryValueType#INITIAl} value type.
    *
    * @param start - the start address of the memory range.
    * @param data - the initial data of the memory range in bytes.
    */
   public MemoryRange(int start, byte[] data)
   {
      this.start = start;
      this.data = data;

      types = new MemoryValueType[data.length];
      Arrays.fill(types, MemoryValueType.INITIAL);
   }

   /**
    * Get the data value of a memory cell
    *
    * @param address - the address of the memory cell
    * @return The memory value.
    *
    * @throws IllegalArgumentException if the address is out of range
    */
   public byte getValue(int address)
   {
      return data[getOffset(address)];
   }

   /**
    * Get a number of bytes of the memory range.
    *
    * @param address - the first address
    * @param count - the number of bytes to get
    *
    * @return The values.
    *
    * @throws IllegalArgumentException if the address is out of range
    */
   public byte[] getValues(int address, int count)
   {
      final int offset = getOffset(address);
      return Arrays.copyOfRange(data, offset, offset + count);
   }

   /**
    * Get the type of a memory cell's value.
    *
    * @param address - the address of the memory cell
    * @return The type of the memory cell's value.
    *
    * @throws IllegalArgumentException if the address is out of range
    */
   public MemoryValueType getValueType(int address)
   {
      return types[getOffset(address)];
   }

   /**
    * Set the data value of a memory cell. The type of the memory
    * cell is set to {@link MemoryValueType#MODIFIED}.
    *
    * @param address - the address of the memory cell
    * @param value - the value to set
    *
    * @throws IllegalArgumentException if the address is out of range
    */
   public void setValue(int address, byte value)
   {
      final int offset = getOffset(address);

      data[offset] = value;
      types[offset] = MemoryValueType.MODIFIED;
   }

   /**
    * Set the initial data value of a memory cell. The type of the memory
    * cell is set to {@link MemoryValueType#INITIAL}.
    *
    * @param address - the address of the memory cell
    * @param value - the value to set
    *
    * @throws IllegalArgumentException if the address is out of range
    */
   public void setInitialValue(int address, byte value)
   {
      final int offset = getOffset(address);

      data[offset] = value;
      types[offset] = MemoryValueType.INITIAL;
   }

   /**
    * @return The start address of the memory range.
    */
   public int getStart()
   {
      return start;
   }

   /**
    * @return The size of the memory range in bytes.
    */
   public int size()
   {
      return data.length;
   }

   /**
    * Clear the memory range. Set all values to zero and all types to
    * {@link MemoryValueType#UNSET}.
    */
   public void clear()
   {
      Arrays.fill(data, (byte) 0);
      Arrays.fill(types, MemoryValueType.UNSET);
   }

   /**
    * Translate a memory address to an internal offset for the memory range's
    * data.
    *
    * @param address - the requested address
    * @return The offset
    *
    * @throws IllegalArgumentException if the address is out of range
    */
   int getOffset(int address)
   {
      address -= start;

      if (address < 0 || address >= data.length)
         throw new IllegalArgumentException("memory address out of range");

      return address;
   }
}
