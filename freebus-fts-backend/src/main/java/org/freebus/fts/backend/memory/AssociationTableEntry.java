package org.freebus.fts.backend.memory;

/**
 * An entry for the device association table.
 */
public class AssociationTableEntry
{
   private int connectionIndex, deviceObjectIndex;

   /**
    * Create an empty association table entry.
    */
   public AssociationTableEntry()
   {
   }

   /**
    * Create an association table entry.
    * 
    * @param connectionIndex - the index of the {@link GroupAddress} in the
    *           connection table.
    * @param deviceObjectIndex - the index of the {@link DeviceObject} in the
    *           communications table.
    */
   public AssociationTableEntry(int connectionIndex, int deviceObjectIndex)
   {
      this.connectionIndex = connectionIndex;
      this.deviceObjectIndex = deviceObjectIndex;
   }

   /**
    * @return The index of the {@link GroupAddress} in the connection table.
    */
   public int getConnectionIndex()
   {
      return connectionIndex;
   }

   /**
    * Set the index of the {@link GroupAddress} in the connection table.
    * 
    * @param connectionIndex - the index to set.
    */
   public void setConnectionIndex(int connectionIndex)
   {
      this.connectionIndex = connectionIndex;
   }

   /**
    * @return The index of the {@link DeviceObject} in the communications table.
    */
   public int getDeviceObjectIndex()
   {
      return deviceObjectIndex;
   }

   /**
    * Set the index of the {@link DeviceObject} in the communications table.
    * 
    * @param deviceObjectIndex - the index to set.
    */
   public void setDeviceObjectIndex(int deviceObjectIndex)
   {
      this.deviceObjectIndex = deviceObjectIndex;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return (deviceObjectIndex << 8) | connectionIndex;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;
      if (!(o instanceof AssociationTableEntry))
         return false;

      final AssociationTableEntry oo = (AssociationTableEntry) o;
      return deviceObjectIndex == oo.deviceObjectIndex && connectionIndex == oo.connectionIndex;
   }
}
