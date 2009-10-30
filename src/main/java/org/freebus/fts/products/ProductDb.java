package org.freebus.fts.products;

import java.io.IOException;
import java.util.*;

/**
 * Interface that defines product databases / catalogs. A specific
 * implementation can be database based, or vd_ file based.
 */
public interface ProductDb
{
   /**
    * @return A map with all manufacturers. Key is the manufacturer-id, value is
    *         the name of the manufacturer.
    * @throws IOException 
    */
   public Map<Integer, String> getManufacturers() throws IOException;

   /**
    * Query the catalog-groups, with optional filtering.
    * 
    * @param filter - an optional filter object. Set to null if you do not want
    *           to have filtering applied.
    * @return A set with all matching catalog-groups.
    * @throws IOException 
    */
   public Set<CatalogGroup> getCatalogGroups(ProductFilter filter) throws IOException;

   /**
    * Query the virtual devices, with optional filtering.
    * 
    * @param filter - an optional filter object. Set to null if you do not want
    *           to have filtering applied.
    * @return A set with all matching virtual devices.
    * @throws IOException 
    */
   public Set<VirtualDevice> getVirtualDevices(ProductFilter filter) throws IOException;
}
