package org.freebus.fts.products;

import java.util.Arrays;

/**
 * A set of filter criteria, used for querying a {@link ProductDb} product
 * database.
 */
public final class ProductFilter
{
   /**
    * Id's of manufacturers, or null if no filtering by manufacturer shall occur.
    */
   public int[] manufacturers = null;

   /**
    * Id's of functional-entities for filtering, or null.
    */
   public int[] functionalEntities = null;

   /**
    * Optimize the contents of the object. The arrays are sorted afterwards,
    * so that binary search, like {@link Arrays#binarySearch(int[], int)},
    * can be used on them.
    */
   public void optimize()
   {
      if (manufacturers != null) Arrays.sort(manufacturers);
      if (functionalEntities != null) Arrays.sort(functionalEntities);
   }
}
