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
    * Query the functional-entities, with optional filtering.
    * 
    * @param filter - an optional filter object. Set to null if you do not want
    *           to have filtering applied.
    * @return A set with all matching catalog-groups.
    * @throws IOException 
    */
   public Set<FunctionalEntity> getFunctionalEntities(ProductFilter filter) throws IOException;

   /**
    * Query the virtual devices, with optional filtering.
    * 
    * @param filter - an optional filter object. Set to null if you do not want
    *           to have filtering applied.
    * @return A set with all matching virtual devices.
    * @throws IOException 
    */
   public Set<VirtualDevice> getVirtualDevices(ProductFilter filter) throws IOException;

   /**
    * Query a specific virtual device.
    *
    * @param id - is the id of the virtual device.
    * @return the virtual device
    * @throws IOException 
    */
   public VirtualDevice getVirtualDevice(int id) throws IOException;

   /**
    * Query the product description for the catalog-entry with the given id.
    */
   public String getProductDescription(int catalogEntryId) throws IOException;
}
