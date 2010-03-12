package org.freebus.fts.products;

import java.io.File;

import org.freebus.fts.products.services.DAOException;
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
    * @return a org.freebus.fts.products factory that accesses FTS's own
    *         org.freebus.fts.products database.
    */
   public static ProductsFactory getFactory()
   {
      return new JpaProductsFactory();
   }

   /**
    * Get a org.freebus.fts.products factory for the org.freebus.fts.products
    * stored in the given VDX file. The persistence unit is set to "default".
    * 
    * @param file - the VDX file.
    * 
    * @return the DAO factory for accessing the data in the file.
    * @throws DAOException if the VDX file is not readable.
    */
   public static ProductsFactory getFactory(File file)
   {
      return getFactory(file, "default");
   }

   /**
    * Get a org.freebus.fts.products factory for the org.freebus.fts.products
    * stored in the given VDX file.
    * 
    * @param file - the VDX file.
    * @param persistenceUnit - the persistence unit to use.
    * 
    * @return the DAO factory for accessing the data in the file.
    * @throws DAOException if the VDX file is not readable.
    */
   public static ProductsFactory getFactory(File file, String persistenceUnit)
   {
      return new VdxProductsFactory(file, persistenceUnit);
   }
}
