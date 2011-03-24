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

import org.freebus.fts.interfaces.Named;

/**
 * A virtual device. This is a device as it could be added to a project.
 */
@Entity
@Table(name = "virtual_device")
public class VirtualDevice implements Named
{
   @Id
   @TableGenerator(name = "VirtualDevice", initialValue = 1, allocationSize = 10)
   @GeneratedValue(strategy = GenerationType.TABLE, generator = "VirtualDevice")
   @Column(name = "virtual_device_id", nullable = false)
   private int id;

   @Column(name = "virtual_device_name", nullable = false, length = 50)
   private String name = "";

   @Column(name = "virtual_device_description", length = 80)
   private String description;

   @Column(name = "virtual_device_number")
   private int number;

   @Column(name = "product_type_id", nullable = false)
   private int productTypeId;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "catalog_entry_id", nullable = false)
   public CatalogEntry catalogEntry;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "functional_entity_id", nullable = false, referencedColumnName = "functional_entity_id")
   public FunctionalEntity functionalEntity;

   @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.PERSIST)
   @JoinColumn(name = "program_id", nullable = true)
   private Program program;

   /**
    * Create an empty virtual-device object.
    */
   public VirtualDevice()
   {
   }

   /**
    * Create a virtual-device object.
    * 
    * @param id - the database ID of the object.
    * @param name - the name of the object.
    * @param description - the description.
    * @param functionalEntity - the functional entity.
    * @param catalogEntry - the catalog entry.
    */
   public VirtualDevice(int id, String name, String description, FunctionalEntity functionalEntity,
         CatalogEntry catalogEntry)
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
    * Set the id.
    * 
    * @param id - the id to set.
    */
   public void setId(int id)
   {
      this.id = id;
   }

   /**
    * @return the functional entity.
    */
   public FunctionalEntity getFunctionalEntity()
   {
      return functionalEntity;
   }

   /**
    * Set the functional entity.
    * 
    * @param functionalEntity - the functional entity to set.
    */
   public void setFunctionalEntity(FunctionalEntity functionalEntity)
   {
      this.functionalEntity = functionalEntity;
   }

   /**
    * @return the name
    */
   @Override
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
    * Set the catalog entry.
    * 
    * @param catalogEntry - the catalog entry to set.
    */
   public void setCatalogEntry(CatalogEntry catalogEntry)
   {
      this.catalogEntry = catalogEntry;
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
    * 
    * @param program - the program to set.
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
    * Set the number of the virtual device. This seems to be a version number
    * for the device, starting with 1, and incrementing when the virtual device
    * is changed (by the manufacturer, in the VD_).
    * 
    * @param number - the number to set.
    */
   public void setNumber(int number)
   {
      this.number = number;
   }

   /**
    * Get the number of the virtual device. This seems to be a version number
    * for the device, starting with 1, and incrementing when the virtual device
    * is changed (by the manufacturer, in the VD_).
    * 
    * @return The number of the virtual device.
    */
   public int getNumber()
   {
      return number;
   }

   /**
    * Set the product type ID.
    *
    * @param productTypeId - the id to set.
    */
   public void setProductTypeId(int productTypeId)
   {
      this.productTypeId = productTypeId;
   }

   /**
    * @return The product type ID.
    */
   public int getProductTypeId()
   {
      return productTypeId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o == this)
         return true;
      if (!(o instanceof VirtualDevice))
         return false;
      final VirtualDevice oo = (VirtualDevice) o;
      return id == oo.id && (catalogEntry == null ? oo.catalogEntry == null : catalogEntry.equals(oo.catalogEntry))
            && (program == null ? oo.program == null : program.equals(oo.program));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return getClass().getSimpleName() + " #" + id + " \"" + name + "\"";
   }
}
