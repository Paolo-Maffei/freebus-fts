package org.freebus.fts.components.memorytable;

/**
 * A memory cell of the {@link MemoryTableModel}.
 */
public class MemoryCell
{
   private int value;
   private boolean modified;
   private MemoryRange range;

   /**
    * Create an empty memory cell.
    */
   public MemoryCell()
   {
   }

   /**
    * @return The value of the memory cell (0..255)
    */
   public int getValue()
   {
      return value;
   }

   /**
    * Set the value of the memory cell
    * 
    * @param value - the value to set (0..255)
    */
   public void setValue(int value)
   {
      if (value < 0 || value > 255)
         throw new IllegalArgumentException("value is not in the range 0..255: " + value);

      this.value = value;
      modified = true;
   }

   /**
    * @return The memory range that the memory cell belongs to.
    */
   public MemoryRange getRange()
   {
      return range;
   }

   /**
    * Set the memory range that the memory cell belongs to.
    * 
    * @param range - the memory range to set.
    */
   public void setRange(MemoryRange range)
   {
      this.range = range;
   }

   /**
    * @return True if the memory cell was modified.
    */
   public boolean isModified()
   {
      return modified;
   }

   /**
    * Set the modified flag.
    * 
    * @param modified - the modified flag to set.
    */
   public void setModified(boolean modified)
   {
      this.modified = modified;
   }

   /**
    * Clear the contents of the memory cell.
    */
   public void clear()
   {
      value = 0;
      modified = false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return String.format("%1$02X", new Object[] { value });
   }
}
