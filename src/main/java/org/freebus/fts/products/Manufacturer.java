package org.freebus.fts.products;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A manufacturer.
 */
public class Manufacturer
{
   public final static Manufacturer NOBODY = new Manufacturer(0, "Nobody");

   private final int id;
   private final String name;
   private final Map<Integer,CatalogGroup> catalogGroups = new HashMap<Integer,CatalogGroup>();

   /**
    * Create a manufacturer.
    */
   public Manufacturer(int id, String name)
   {
      this.id = id;
      this.name = name;
   }

   /**
    * @return the id of the manufacturer.
    */
   public int getId()
   {
      return id;
   }

   /**
    * @return the name of the manufacturer.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Add a functional-entity.
    */
   public void addFunctionalEntity(CatalogGroup catalogGroup)
   {
      catalogGroups.put(catalogGroup.getId(), catalogGroup);
   }

   /**
    * @return the functional-entity with the given id.
    */
   public CatalogGroup getFunctionalEntity(int id)
   {
      return catalogGroups.get(id);
   }
   
   /**
    * @return all id's of the functional-entities.
    */
   public Set<Integer> getFunctionalEntityKeys()
   {
      return catalogGroups.keySet();
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
    * Compare two manufacturers by id.
    */
   @Override
   public boolean equals(final Object o)
   {
      if (o==this) return true;
      if (!(o instanceof Manufacturer)) return false;
      final Manufacturer oo = (Manufacturer)o;
      return id==oo.id;
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
