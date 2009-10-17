package org.freebus.fts.products;

/**
 * A virtual-device
 */
public class VirtualDevice
{
   private final int id;
   private final String name, description;
   private final FunctionalEntity functionalEntity;

   /**
    * Create a virtual-device object.
    */
   public VirtualDevice(int id, String name, String description, FunctionalEntity functionalEntity)
   {
      this.id = id;
      this.name = name;
      this.description = description;
      this.functionalEntity = functionalEntity;
   }

   /**
    * @return the id
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @return the functionalEntity
    */
   public FunctionalEntity getFunctionalEntity()
   {
      return functionalEntity;
   }

   /**
    * Returns a hash-code for the object.
    */
   @Override
   public int hashCode()
   {
      return id;
   }
   
   /**
    * Compare two objects.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o==this) return true;
      if (!(o instanceof VirtualDevice)) return false;
      final VirtualDevice oo = (VirtualDevice)o;
      return id==oo.id && name==oo.name;
   }

   /**
    * Returns a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return name+" ["+Integer.toString(id)+"]";
   }
}
