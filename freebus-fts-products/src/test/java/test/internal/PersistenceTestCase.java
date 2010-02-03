package test.internal;

import static org.junit.Assert.assertTrue;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.services.ProductsFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersistenceTestCase
{
   private static ProductsFactory jpaProductsFactory;
   private static ProductsFactory vdxProductsFactory;
   private boolean setupCalled;

   static
   {
      if (DatabaseResources.getEntityManagerFactory() == null)
         DatabaseResources.setEntityManagerFactory(DatabaseResources.createEntityManagerFactory(DriverType.HSQL_MEM,
               "test", "sa", ""));
   }

   @Before
   public void setUp() throws Exception
   {
      setupCalled = true;
      getJpaProductsFactory().getTransaction().begin();
      getJpaProductsFactory().getTransaction().setRollbackOnly();
   }

   @After
   public void tearDown() throws Exception
   {
      jpaProductsFactory.getTransaction().rollback();
   }

   @Test
   public void setupCalled()
   {
      assertTrue(setupCalled);
   }

   public static synchronized ProductsFactory getJpaProductsFactory()
   {
      if (jpaProductsFactory == null)
         jpaProductsFactory = ProductsManager.getFactory();

      return jpaProductsFactory;
   }

   public static synchronized ProductsFactory getVdxProductsFactory()
   {
      if (vdxProductsFactory == null)
         vdxProductsFactory = ProductsManager.getFactory("src/test/resources/230V in LPC_.vd_");

      return vdxProductsFactory;
   }
}
