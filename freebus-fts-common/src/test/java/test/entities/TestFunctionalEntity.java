package test.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Functional entities are for grouping of the virtual devices.
 * Used for unit-tests only!
 */
@Entity
@Table(name = "functional_entity")
public class TestFunctionalEntity
{
   @Id
   @Column(name = "functional_entity_id", nullable = false)
   public int id;
   
   @Column(name = "manufacturer_id", nullable = false)
   public TestManufacturer manufacturer;

   @Column(name = "functional_entity_name", nullable = false)
   public String name;

   @Column(name = "functional_entity_description")
   public String description;

   @Column(name = "fun_functional_entity_id")
   public int parentId = 0;

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
      if (!(o instanceof TestFunctionalEntity)) return false;
      final TestFunctionalEntity oo = (TestFunctionalEntity)o;
      return id == oo.id && manufacturer == oo.manufacturer;
   }
}
