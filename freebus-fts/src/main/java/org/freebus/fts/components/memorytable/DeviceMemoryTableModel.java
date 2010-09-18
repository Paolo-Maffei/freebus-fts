package org.freebus.fts.components.memorytable;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.freebus.fts.bus.DeviceMemoryAdapter;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.core.I18n;
import org.freebus.fts.products.Mask;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceParameter;

/**
 * A {@link MemoryTableModel} that can be updated from a {@link Device}.
 */
public class DeviceMemoryTableModel extends MemoryTableModel
{
   private final Set<MemoryRange> ranges = new TreeSet<MemoryRange>();
   private final Map<Integer, Integer> oldValues = new HashMap<Integer, Integer>();
   private final Color backgroundColor;
   private final DeviceMemoryAdapter deviceMemoryAdapter = new DeviceMemoryAdapter();
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
      deviceMemoryAdapter.setDevice(device);

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
    * Create a memory range and add it to the memory ranges.
    * 
    * @param start - the start address of the range.
    * @param size - the size of the range.
    * @param name - the name of the range.
    * @return The created memory range.
    */
   public MemoryRange createRange(int start, int size, String name)
   {
      final int idx = ranges.size();

      int r = (idx & 1) | ((idx & 8) >> 2) | (idx & 64) >> 4;
      int g = ((idx & 2) >> 1) | ((idx & 16) >> 3) | (idx & 128) >> 5;
      int b = ((idx & 4) >> 2) | ((idx & 32) >> 4) | (idx & 256) >> 6;

      r = (int) (backgroundColor.getRed() * 0.8f) + (r << 6) - 32;
      if (r > 255)
         r -= 255;
      else if (r < 0)
         r = 0;

      g = (int) (backgroundColor.getGreen() * 0.8f) + (g << 6) - 32;
      if (g > 255)
         g = 255;
      else if (g < 0)
         g = 0;

      b = (int) (backgroundColor.getBlue() * 0.8f) + (b << 6) - 32;
      if (b > 255)
         b = 255;
      else if (r < 0)
         b = 0;

      final MemoryRange range = new MemoryRange(start, size, name, new Color(r, g, b));
      ranges.add(range);
      return range;
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
    * Clear the labels for all memory cells.
    */
   public void clearCellLabels()
   {
      final int addr0 = getStartAddr();
      for (int i = getRowCount() - 1; i >= 0; --i)
         getValueAt(addr0 + i).setLabel(null);
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
      int size, start;

      createRange(0, 256, I18n.getMessage("DeviceMemoryTableModel.Ram"));

      final byte[] eepromData = program.getEepromData();
      if (eepromData != null)
      {
         createRange(256, eepromData.length, I18n.getMessage("DeviceMemoryTableModel.ProgramEeprom"));

         for (int i = 0; i < eepromData.length; ++i)
            getValueAt(256 + i).setValue(eepromData[i] & 0xff);
      }

      createRange(mask.getManufacturerDataAddress(), mask.getManufacturerDataSize(),
            I18n.getMessage("DeviceMemoryTableModel.ManufacturerData"));

      size = mask.getUserRamEnd() - mask.getUserRamStart();
      createRange(mask.getUserRamStart(), size, I18n.getMessage("DeviceMemoryTableModel.UserRam"));

      size = mask.getUserEepromEnd() - mask.getUserEepromStart();
      createRange(mask.getUserEepromStart(), size, I18n.getMessage("DeviceMemoryTableModel.UserEeprom"));

      createRange(program.getCommsTabAddr(), program.getCommsTabSize(),
            I18n.getMessage("DeviceMemoryTableModel.CommunicationsTable"));
      createRange(program.getAssocTabAddr(), program.getAssocTabSize(),
            I18n.getMessage("DeviceMemoryTableModel.AssociationTable"));
      createRange(mask.getManufacturerIdAddress(), 2,
            I18n.getMessage("DeviceMemoryTableModel.ManufacturerId"));
      createRange(mask.getRouteCountAddress(), 1,
            I18n.getMessage("DeviceMemoryTableModel.RouteCount"));
      createRange(mask.getRunErrorAddress(), 1,
            I18n.getMessage("DeviceMemoryTableModel.RunError"));

      start = mask.getAddressTabAddress();
      createRange(start, program.getAddrTabSize(), I18n.getMessage("DeviceMemoryTableModel.AddressTable"));
      getValueAt(start).setLabel(I18n.getMessage("DeviceMemoryTableModel.AddressTableSize"));
      getValueAt(start + 1).setLabel(I18n.getMessage("DeviceMemoryTableModel.PhysicalAddress"));
      getValueAt(start + 2).setLabel(I18n.getMessage("DeviceMemoryTableModel.PhysicalAddress"));

      updateMemoryCellRanges();

      final String paramsTmpl = I18n.getMessage("DeviceMemoryTableModel.MemoryCellParameter");
      final MemoryRange parameterRange = createRange(0, 0, I18n.getMessage("DeviceMemoryTableModel.Parameter"));
      for (final Parameter param : device.getProgram().getParameters())
      {
         final Integer addr = param.getAddress();
         if (addr == null || addr == 0)
            continue;

         final MemoryCell cell = getValueAt(addr);
         cell.setRange(parameterRange);

         String oldLbl = cell.getLabel();
         if (oldLbl == null)
            oldLbl = "Address: " + addr;
         final String paramLbl = String.format("<br>" + paramsTmpl, new Object[] { param.getId(), param.getSize(),
               param.getBitOffset() });
         cell.setLabel(oldLbl + paramLbl);
      }

      updateContents();
      unsetModified();
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
    * Set a value and remember the old value.
    * 
    * @param addr - the address to set the value into.
    * @param value - the value to set.
    * 
    * @return true if the value differs from the current value
    */
   public boolean setValue(int addr, int value)
   {
      final MemoryCell cell = getValueAt(addr);

      int oldValue = cell.getValue();
      boolean undefinedValue = false;

      if (oldValue == -1)
      {
         oldValue = 0;
         undefinedValue = true;
      }

      value &= 255;

      if (value == oldValue && !undefinedValue)
         return false;

      if (!oldValues.containsKey(addr))
         oldValues.put(addr, oldValue);

      cell.setValue(value);
      return true;
   }

   /**
    * Update the contents of the object. Call when the device has changed.
    */
   public void updateContents()
   {
      if (device == null)
         return;

      Logger.getLogger(getClass()).debug("updateContents");
      unsetModified();

      oldValues.clear();
      deviceMemoryAdapter.update();

      final Program prog = device.getProgram();
      final Mask mask = prog.getMask();
      int pos;

      for (final Parameter param : device.getProgram().getParameters())
      {
         final DeviceParameter devParam = device.getDeviceParameter(param);
         if (!devParam.isUsed())
            continue;

         final Integer addr = param.getAddress();
         if (addr == null || addr == 0)
            continue;

         final int bits = param.getSize();
         final int bitOffset = param.getBitOffset();

         int bitMask = (1 << bits) - 1;
         bitMask <<= bitOffset;

         final MemoryCell cell = getValueAt(addr);

         int oldValue = cell.getValue();
         if (oldValue == -1)
            oldValue = 0;

         final int paramValue = devParam.getIntValue() & 255;
         final int newValue = (oldValue & ~bitMask) | ((paramValue << bitOffset) & bitMask);

         if (setValue(addr, newValue))
         {
            Logger.getLogger(getClass()).debug(
                  "@" + addr + ": " + oldValue + "->" + newValue + " (param #" + param.getId() + " value " + paramValue
                        + ", " + bits + " bits, offset " + bitOffset + ")");
         }
      }

      pos = prog.getCommsTabAddr();
      final List<ObjectDescriptor> objDescs = deviceMemoryAdapter.getObjectDescriptors();
      setValue(pos, objDescs.size());

      int ramFlagTablePtr = deviceMemoryAdapter.getRamFlagTablePtr();
      setValue(++pos, ramFlagTablePtr);
      for (int i = (objDescs.size() + 1) >> 1; i > 0; --i)
         setValue(ramFlagTablePtr++, 0xcf);

      int idx = 0xc0;
      for (final ObjectDescriptor objDesc : objDescs)
      {
         final byte[] data = objDesc.toByteArray();
         setValue(++pos, data[0]); 
         setValue(++pos, data[1]); 
         setValue(++pos, data[2]); 
         setValue(objDesc.getDataPointer(), ++idx);
      }

      final PhysicalAddress physicalAddress = device.getPhysicalAddress();
      final Collection<GroupAddress> groupAddresses = deviceMemoryAdapter.getGroupAddresses();
      pos = mask.getAddressTabAddress();
      setValue(pos, groupAddresses.size() + 1);
      setValue(++pos, physicalAddress.getBytes()[0]);
      setValue(++pos, physicalAddress.getBytes()[1]);
      for (final GroupAddress addr : groupAddresses)
      {
         setValue(++pos, addr.getBytes()[0]);
         setValue(++pos, addr.getBytes()[1]);
      }

      for (Integer addr : oldValues.keySet())
      {
         final MemoryCell cell = getValueAt(addr);
         if (oldValues.get(addr) != cell.getValue())
            Logger.getLogger(getClass()).debug(
                  "@" + addr + ": modified " + oldValues.get(addr) + "->" + cell.getValue());
         else cell.setModified(false);
      }

      fireTableChanged(0, getRowCount());
   }
}
