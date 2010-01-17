package test.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.freebus.fts.common.vdx.VdxField;


/**
 * Functional entities are for grouping of the virtual devices.
 * Used for unit-tests only!
 */
@Entity
@Table(name = "functional_entity")
public class TestFunctionalEntityTree
{
   @Id
   @Column(name = "functional_entity_id", nullable = false)
   public int id;

   @ManyToOne(fetch = FetchType.EAGER, optional = false)
   @JoinColumn(name = "manufacturer_id", nullable = false)
   public TestManufacturer manufacturer;

   @Column(name = "functional_entity_name", nullable = false)
   public String name;

   @Column(name = "functional_entity_description")
   public String description;

   @VdxField(name = "fun_functional_entity_id")
   @ManyToOne(fetch = FetchType.EAGER, optional = false)
   @JoinColumn(name = "parent_id", nullable = true)
   public TestFunctionalEntityTree parent;

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
      if (!(o instanceof TestFunctionalEntityTree)) return false;
      final TestFunctionalEntityTree oo = (TestFunctionalEntityTree)o;
      return id == oo.id && manufacturer == oo.manufacturer;
   }
}
