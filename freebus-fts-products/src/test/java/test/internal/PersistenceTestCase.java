package test.internal;

import junit.framework.TestCase;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.products.Products;
import org.freebus.fts.products.services.ProductsFactory;

public class PersistenceTestCase extends TestCase
{
   private static ProductsFactory jpaProductsFactory;
   private static ProductsFactory vdxProductsFactory;

   static
   {
      if (DatabaseResources.getEntityManagerFactory() == null)
         DatabaseResources.setEntityManagerFactory(DatabaseResources.createEntityManagerFactory(DriverType.HSQL_MEM, "test", "sa", ""));
   }

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      getJpaProductsFactory().getTransaction().begin();
   }

   @Override
   protected void tearDown() throws Exception
   {
      jpaProductsFactory.getTransaction().rollback();
      super.tearDown();
   }

   public static synchronized ProductsFactory getJpaProductsFactory()
   {
      if (jpaProductsFactory == null)
         jpaProductsFactory = Products.getFactory();

      return jpaProductsFactory;
   }

   public static synchronized ProductsFactory getVdxProductsFactory()
   {
      if (vdxProductsFactory == null)
         vdxProductsFactory = Products.getFactory("src/test/resources/230V in LPC_.vd_");

      return vdxProductsFactory;
   }
}
