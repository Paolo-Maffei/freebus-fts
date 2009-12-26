package org.freebus.fts.products;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.jpa.JpaProductsFactory;
import org.freebus.fts.products.services.vdx.VdxProductsFactory;

/**
 * Get a DAO factory for products stored in the database or a VDX file.
 */
public final class Products
{
   private Products()
   {
   }

   /**
    * @return a products factory that accesses FTS's own products database.
    */
   public static ProductsFactory getFactory()
   {
      return new JpaProductsFactory();
   }

   /**
    * Get a products factory for the products stored in the given VDX file.
    * 
    * @param fileName - the name of the VDX file.
    * @return the DAO factory for accessing the data in the file.
    * @throws DAOException if the VDX file is not readable.
    */
   public static ProductsFactory getFactory(String fileName) throws PersistenceException
   {
      return new VdxProductsFactory(fileName);
   }
}
