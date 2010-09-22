package org.freebus.fts.products;

import java.io.File;

import org.freebus.fts.products.importer.RemappingProductsImporter;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.jpa.JpaProductsFactory;
import org.freebus.fts.products.services.vdx.VdxProductsFactory;

/**
 * Get a DAO factory for org.freebus.fts.products stored in the database or a
 * VDX file.
 */
public final class ProductsManager
{
   private ProductsManager()
   {
   }

   /**
    * @return A products factory that accesses FTS's own products database.
    */
   public static ProductsFactory getFactory()
   {
      return new JpaProductsFactory();
   }

   /**
    * Get a products factory for the products stored in the given VDX file. The
    * persistence unit is set to "default".
    * 
    * @param file - the VDX file.
    * 
    * @return the DAO factory for accessing the data in the file.
    */
   public static ProductsFactory getFactory(File file)
   {
      return getFactory(file, "default");
   }

   /**
    * Get a products factory for the products stored in the given VDX file.
    * 
    * @param file - the VDX file.
    * @param persistenceUnit - the persistence unit to use.
    * 
    * @return the DAO factory for accessing the data in the file.
    */
   public static ProductsFactory getFactory(File file, String persistenceUnit)
   {
      return new VdxProductsFactory(file, persistenceUnit);
   }

   /**
    * Create an importer that will import products from sourceFactory and save them
    * into destFactory.
    * 
    * @param sourceFactory - the products factory to import products from.
    * @param destFactory - the products factory to store products into.
    */
   public static ProductsImporter getProductsImporter(ProductsFactory sourceFactory, ProductsFactory destFactory)
   {
//      return new DirectProductsImporter(sourceFactory, destFactory);
      return new RemappingProductsImporter(sourceFactory, destFactory);
   }

}
