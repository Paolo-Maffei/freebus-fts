package org.freebus.fts.products;

import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.ProductsDAOFactory;
import org.freebus.fts.products.dao.jpa.JpaProductsDAOFactory;
import org.freebus.fts.products.dao.vdx.VdxProductsDAOFactory;

/**
 * Get a DAO factory for products stored in the database or a VDX file.
 */
public final class Products
{
   /**
    * @return a DAO factory that accesses the products database.
    */
   public static ProductsDAOFactory getDAOFactory()
   {
      return new JpaProductsDAOFactory();
   }

   /**
    * Get a DAO factory for the products stored in the given VDX file.
    * 
    * @param fileName - the name of the VDX file.
    * @return the DAO factory for accessing the data in the file.
    * @throws DAOException if the VDX file is not readable.
    */
   public static ProductsDAOFactory getDAOFactory(String fileName) throws DAOException
   {
      return new VdxProductsDAOFactory(fileName);
   }
}
