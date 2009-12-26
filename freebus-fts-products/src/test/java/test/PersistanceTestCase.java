package test;

import junit.framework.TestCase;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.products.Products;
import org.freebus.fts.products.services.ProductsFactory;

public class PersistanceTestCase extends TestCase
{
   private static ProductsFactory productsFactory;

   static
   {
      if (DatabaseResources.getEntityManagerFactory() == null)
         DatabaseResources.setEntityManagerFactory(DatabaseResources.createEntityManagerFactory(DriverType.HSQL_MEM, "test", "sa", ""));
   }

   public static synchronized ProductsFactory getProductsFactory()
   {
      if (productsFactory == null) productsFactory = Products.getFactory();
      return productsFactory;
   }
}
