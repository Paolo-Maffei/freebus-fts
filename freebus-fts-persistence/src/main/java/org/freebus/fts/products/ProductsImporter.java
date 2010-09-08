package org.freebus.fts.products;

import java.util.List;

import org.freebus.fts.products.services.ProductsFactory;

/**
 * Interface for products importer. Products importer
 * import virtual devices from one products factory into
 * another products factory.
 */
public interface ProductsImporter
{
   /**
    * Import a list of virtual devices.
    * 
    * @param devices - the devices to import.
    */
   public void copy(List<VirtualDevice> devices);

   /**
    * @return The products factory from where the products are imported.
    */
   public ProductsFactory getSourceFactory();

   /**
    * @return The products factory into which the products are imported.
    */
   public ProductsFactory getDestFactory();

}
