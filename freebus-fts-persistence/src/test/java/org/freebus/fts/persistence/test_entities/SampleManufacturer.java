package org.freebus.fts.persistence.test_entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.freebus.fts.persistence.vdx.VdxEntity;

/**
 * A manufacturer. Used for unit-tests only!
 */
@Entity
@VdxEntity(name = "manufacturer")
@Table(name = "manu")
public class SampleManufacturer
{
   @Id
   @TableGenerator(initialValue = 1, allocationSize = 5, table = "sequence",  name = "GenManufacturerId")
   @GeneratedValue(strategy = GenerationType.TABLE)
   @Column(name = "manufacturer_id", nullable = false)
   public int id;

   @Column(name = "manufacturer_name", length = 50, nullable = false)
   public String name;

   /**
    * Returns a hash-code for the object.
    */
   @Override
   public int hashCode()
   {
      return id;
   }

   /**
    * Compare two manufacturer by id.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o == this) return true;
      if (!(o instanceof SampleManufacturer)) return false;
      final SampleManufacturer oo = (SampleManufacturer) o;
      return id == oo.id;
   }
}
