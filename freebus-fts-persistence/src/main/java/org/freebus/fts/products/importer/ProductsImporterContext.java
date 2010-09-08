package org.freebus.fts.products.importer;

import java.util.HashMap;
import java.util.Map;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ProductsFactory;

/**
 * Internal context for products importers.
 */
public final class ProductsImporterContext
{
   final public ProductsFactory sourceFactory;
   final public ProductsFactory destFactory;

   final private Map<Integer, Manufacturer> manufacturers = new HashMap<Integer, Manufacturer>();

   /**
    * Create a products importer context object.
    */
   public ProductsImporterContext(ProductsFactory sourceFactory, ProductsFactory destFactory)
   {
      this.sourceFactory = sourceFactory;
      this.destFactory = destFactory;
   }

   /**
    * Clear all temporary objects and containers.
    */
   public void clear()
   {
      manufacturers.clear();
   }

   /**
    * Add a manufacturer. Automatically merges the manufacturer to the
    * destination persistence context. Does nothing if the manufacturer is null.
    * 
    * @param manufacturer - the manufacturer to add.
    */
   public void add(Manufacturer manufacturer)
   {
      if (manufacturer == null || manufacturers.containsKey(manufacturer.getId()))
         return;

      manufacturer = destFactory.getManufacturerService().merge(manufacturer);
      manufacturers.put(manufacturer.getId(), manufacturer);
   }

   /**
    * Get a manufacturer by id. Only the manufacturers that were previously
    * {@link #add(Manufacturer) added} can be accessed.
    * 
    * @param id - the id of the requested manufacturer.
    * @return The manufacturer, or null if the manufacturer was not found.
    */
   public Manufacturer getManufacturer(int id)
   {
      return manufacturers.get(id);
   }
}
