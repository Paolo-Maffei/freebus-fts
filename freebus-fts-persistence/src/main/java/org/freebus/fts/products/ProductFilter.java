package org.freebus.fts.products;

import java.util.Arrays;

/**
 * A set of filter criteria, used for querying a products database.
 */
public final class ProductFilter
{
   /**
    * Manufacturer id, or zero if no filtering by manufacturer shall occur.
    */
   public int manufacturer = 0;

   /**
    * Id's of functional-entities for filtering, or null.
    */
   public int[] functionalEntities = null;

   /**
    * Optimize the contents of the object. The arrays are sorted afterwards, so
    * that binary search, like {@link Arrays#binarySearch(int[], int)}, can be
    * used on them.
    */
   public void optimize()
   {
      if (functionalEntities != null)
         Arrays.sort(functionalEntities);
   }

   /**
    * @return the functional entities as comma separated string, or null if no
    *         functional entities are set.
    */
   public String getFunctionalEntitiesString()
   {
      if (functionalEntities == null || functionalEntities.length < 1)
         return null;

      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < functionalEntities.length; ++i)
      {
         if (i > 0)
            sb.append(',');
         sb.append(functionalEntities[i]);
      }

      return sb.toString();
   }
}
