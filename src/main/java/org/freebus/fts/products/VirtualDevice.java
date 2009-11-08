package org.freebus.fts.products;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * A virtual-device.
 */
@Entity
@Table(name = "virtual_device", uniqueConstraints = @UniqueConstraint(columnNames = "virtual_device_id"))
public class VirtualDevice
{
   @Id
   @Column(name = "virtual_device_id", columnDefinition = "INT", unique = true, nullable = false)
   private int id;

   @Column(name = "catalog_entry_id", columnDefinition = "INT", nullable = false)
   private int catalogEntryId;

   @Column(name = "functional_entity_id", columnDefinition = "INT", nullable = false)
   private int functionalEntityId;

   @Column(name = "program_id", columnDefinition = "INT")
   private int programId;

   @Column(name = "virtual_device_name", length = 50)
   private String name;

   @Column(name = "virtual_device_description", length = 80)
   private String description;

   /**
    * Create an empty virtual-device object.
    */
   public VirtualDevice()
   {
   }

   /**
    * Create a virtual-device object.
    */
   public VirtualDevice(int id, String name, String description, int functionalEntityId, int catalogEntryId)
   {
      this.id = id;
      this.name = name;
      this.description = description;
      this.functionalEntityId = functionalEntityId;
      this.catalogEntryId = catalogEntryId;
   }

   /**
    * @return the id
    */
   public int getId()
   {
      return id;
   }
   
   /**
    * @return the functional entity id.
    */
   public int getFunctionalEntityId()
   {
      return functionalEntityId;
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
    * @return the id of the catalog entry.
    */
   public int getCatalogEntryId()
   {
      return catalogEntryId;
   }

   /**
    * Set the program id.
    */
   public void setProgramId(int programId)
   {
      this.programId = programId;
   }

   /**
    * @return the program id.
    */
   public int getProgramId()
   {
      return programId;
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
      if (o == this) return true;
      if (!(o instanceof VirtualDevice)) return false;
      final VirtualDevice oo = (VirtualDevice) o;
      return id == oo.id && catalogEntryId == oo.catalogEntryId;
   }

   /**
    * Returns a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return name + " [" + Integer.toString(id) + "]";
   }
}
