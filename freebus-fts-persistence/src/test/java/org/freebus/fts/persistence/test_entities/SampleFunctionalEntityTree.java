package org.freebus.fts.persistence.test_entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.freebus.fts.persistence.vdx.VdxField;
import org.junit.Ignore;


/**
 * Functional entities are for grouping of the virtual devices.
 * Used for unit-tests only!
 */
@Entity
@Table(name = "functional_entity")
@Ignore
public class SampleFunctionalEntityTree
{
   @Id
   @Column(name = "functional_entity_id", nullable = false)
   public int id;

   @ManyToOne(fetch = FetchType.EAGER, optional = false)
   @JoinColumn(name = "manufacturer_id", nullable = false)
   public SampleManufacturer manufacturer;

   @Column(name = "functional_entity_name", nullable = false)
   public String name;

   @Column(name = "functional_entity_description")
   public String description;

   @ManyToOne(fetch = FetchType.EAGER, optional = false)
   @JoinColumn(name = "parent_id")
   @VdxField(name = "fun_functional_entity_id")
   public SampleFunctionalEntityTree parent;

   @OneToMany(fetch = FetchType.EAGER)
   @JoinColumn(name = "parent_id")
   @VdxField(name = "functional_entity_id")
   public Set<SampleFunctionalEntityTree> childs;

   /**
    * @return a hash-code for the object.
    */
   @Override
   public int hashCode()
   {
      return (id << 10) | manufacturer.id;
   }
   
   /**
    * Compare two objects.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o==this) return true;
      if (!(o instanceof SampleFunctionalEntityTree)) return false;
      final SampleFunctionalEntityTree oo = (SampleFunctionalEntityTree)o;
      return id == oo.id && manufacturer == oo.manufacturer;
   }
}
