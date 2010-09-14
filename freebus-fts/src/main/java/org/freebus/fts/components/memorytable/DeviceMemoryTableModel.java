package org.freebus.fts.components.memorytable;

import java.awt.Color;
import java.util.Set;
import java.util.TreeSet;

import org.freebus.fts.products.Mask;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;

/**
 * A {@link MemoryTableModel} that can be updated from a {@link Device}.
 */
public class DeviceMemoryTableModel extends MemoryTableModel
{
   private final Set<MemoryRange> ranges = new TreeSet<MemoryRange>();
   private final Color backgroundColor;
   private Device device;

   /**
    * Create a device memory-table model.
    * 
    * @param startAddr - the start address of the memory range.
    * @param size - the size of the shown memory range, in bytes.
    * @param backgroundColor - the default background color for memory cells.
    */
   public DeviceMemoryTableModel(int startAddr, int size, Color backgroundColor)
   {
      super(startAddr, size);
      this.backgroundColor = backgroundColor;
   }

   /**
    * Set the device to display. Calls {@link #updateContents()}.
    * 
    * @param device - the device to set.
    */
   public void setDevice(Device device)
   {
      this.device = device;
      deviceChanged();
   }

   /**
    * @return The displayed device.
    */
   public Device getDevice()
   {
      return device;
   }

   /**
    * Clear the internal set of memory ranges.
    */
   public void removeAllRanges()
   {
      ranges.clear();
   }

   /**
    * Add a memory range.
    * 
    * @param range - the range to add.
    */
   public void addRange(MemoryRange range)
   {
      ranges.add(range);
   }

   /**
    * Add a memory range.
    * 
    * @param start - the start address of the range.
    * @param size - the size of the range.
    * @param name - the name of the range.
    */
   public void addRange(int start, int size, String name)
   {
      final int idx = ranges.size();

      int r = (idx & 1) | (idx & 8);
      int g = (idx & 2) | (idx & 16);
      int b = (idx & 4) | (idx & 32);

      r = (int)(backgroundColor.getRed() * 0.8f) + (r << 6) - 32;
      if (r > 255)
         r = 255;
      else if (r < 0)
         r = 0;

      g = (int)(backgroundColor.getGreen() * 0.8f) + (g << 6) - 32;
      if (g > 255)
         g = 255;
      else if (g < 0)
         g = 0;

      b = (int)(backgroundColor.getBlue() * 0.8f) + (b << 6) - 32;
      if (b > 255)
         b = 255;
      else if (r < 0)
         b = 0;

      ranges.add(new MemoryRange(start, size, name, new Color(r, g, b)));
   }

   /**
    * Find the memory range that the given address belongs to.
    * 
    * @param addr - the memory address to search the range for.
    * 
    * @return The memory range for the address, or null if not found.
    */
   public MemoryRange getRange(int addr)
   {
      for (final MemoryRange range : ranges)
      {
         final int start = range.getStart();

         if (start <= addr && start + range.getSize() > addr)
            return range;
      }

      return null;
   }

   /**
    * Called by {@link #setDevice(Device)} when the device is changed.
    */
   public void deviceChanged()
   {
      removeAllRanges();
      clear();

      if (device == null)
      {
         fireTableChanged(0, getRowCount());
         return;
      }

      final Program program = device.getProgram();
      final Mask mask = program.getMask();
      int size;

      addRange(0, 256, "anonymous ram");
      addRange(256, 256, "anonymous eeprom");

      final byte[] eepromData = program.getEepromData();
      if (eepromData != null)
      {
         addRange(0x100, eepromData.length, "program eeprom data");

         for (int i = 0; i < eepromData.length; ++i)
            getValueAt(0x100 + i).setValue(eepromData[i] & 0xff);
      }

      addRange(program.getCommsTabAddr(), program.getCommsTabSize(), "communications table");
      addRange(program.getAssocTabAddr(), program.getAssocTabSize(), "association table");

      addRange(mask.getAddressTabAddress(), program.getAddrTabSize(), "address table");
      addRange(mask.getManufacturerDataAddress(), mask.getManufacturerDataSize(), "manufacturer data");

      size = mask.getUserEepromEnd() - mask.getUserEepromStart();
      addRange(mask.getUserEepromStart(), size, "user eeprom");

      size = mask.getUserRamEnd() - mask.getUserRamStart();
      addRange(mask.getUserRamStart(), size, "user ram");

      updateMemoryCellRanges();
      fireTableChanged(0, getRowCount());
   }

   /**
    * Update the memory ranges of all memory cells. Call when the memory ranges
    * have changed.
    */
   public void updateMemoryCellRanges()
   {
      final int addr0 = getStartAddr();
      final int sz = getSize();

      // Clear the memory range of all memory cells
      for (int i = sz - 1; i >= 0; --i)
         getValueAt(addr0 + i).setRange(null);

      // Apply the memory ranges to the corresponding memory cells
      for (final MemoryRange range : ranges)
      {
         final int start = range.getStart();
         for (int j = range.getSize() - 1; j >= 0; --j)
         {
            final MemoryCell cell = getValueAt(start + j);
            if (cell.getRange() == null)
               cell.setRange(range);
         }
      }
   }

   /**
    * Update the contents of the object. Call when the device has changed.
    */
   public void updateContents()
   {
      if (device == null)
         return;

      // TODO

      fireTableChanged(0, getRowCount());
   }
}
