package org.freebus.fts.products;

import java.util.*;

/**
 * Interface that defines product databases / catalogs. A specific
 * implementation can be database based, or vd_ file based.
 */
public interface ProductDb
{
   /**
    * @return a map with all manufacturers. Key is the manufacturer-id, value is
    *         the name of the manufacturer.
    */
   public Map<Integer, String> getManufacturers();

   /**
    * Query the catalog-groups, with optional filtering.
    * 
    * @param filter is an optional filter object. Set to null if you do not want
    *           to have filtering applied.
    * @return a list with all catalog-groups.
    */
   public Set<CatalogGroup> getCatalogGroups(ProductFilter filter);

}
