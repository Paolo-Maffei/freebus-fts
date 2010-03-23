package org.freebus.fts.products;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * A virtual device.
 */
@Entity
@Table(name = "virtual_device")
public class VirtualDevice
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence",  name = "GenVirtualDeviceId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "virtual_device_id", nullable = false)
   private int id;

   @Column(name = "virtual_device_name", nullable = false, length = 50)
   private String name = "";

   @Column(name = "virtual_device_description", length = 80)
   private String description;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "catalog_entry_id", nullable = false)
   public CatalogEntry catalogEntry;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "functional_entity_id", nullable = false, referencedColumnName = "functional_entity_id")
   public FunctionalEntity functionalEntity;

   @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "program_id", nullable = true)
   private Program program;

   // Column "PRODUCT_TYPE_ID" is the "ENTITY_ID" of VDX table "TEXT_ATTRIBUTE"

   /**
    * Create an empty virtual-device object.
    */
   public VirtualDevice()
   {
   }

   /**
    * Create a virtual-device object.
    */
   public VirtualDevice(int id, String name, String description, FunctionalEntity functionalEntity, CatalogEntry catalogEntry)
   {
      this.id = id;
      this.name = name;
      this.description = description;
      this.functionalEntity = functionalEntity;
      this.catalogEntry = catalogEntry;
   }

   /**
    * @return the id
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the functional entity.
    */
   public FunctionalEntity getFunctionalEntity()
   {
      return functionalEntity;
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
    * @return the catalog entry.
    */
   public CatalogEntry getCatalogEntry()
   {
      return catalogEntry;
   }

   /**
    * Set the program.
    */
   public void setProgram(Program program)
   {
      this.program = program;
   }

   /**
    * @return the program.
    */
   public Program getProgram()
   {
      return program;
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
      return id == oo.id && catalogEntry == oo.catalogEntry;
   }

   /**
    * Returns a human readable representation of the object.
    */
   @Override
   public String toString()
   {
      return getClass().getSimpleName() + " #" + id + " \"" + name + "\"";
   }
}
