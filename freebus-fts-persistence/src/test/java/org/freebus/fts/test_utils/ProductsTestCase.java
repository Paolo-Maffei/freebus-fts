package org.freebus.fts.test_utils;

import java.io.File;

import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.services.ProductsFactory;

public abstract class ProductsTestCase extends PersistenceTestCase
{
   private static ProductsFactory jpaProductsFactory;
   private static ProductsFactory vdxProductsFactory;

   public ProductsTestCase(final String persistenceUnitName)
   {
      super(persistenceUnitName);
   }

   public ProductsTestCase()
   {
      this("test-full");
   }

   /**
    * @return a JPA products factory.
    */
   public synchronized ProductsFactory getJpaProductsFactory()
   {
      if (jpaProductsFactory == null)
         jpaProductsFactory = ProductsManager.getFactory();

      return jpaProductsFactory;
   }

   /**
    * @return a VDX products factory.
    */
   public synchronized ProductsFactory getVdxProductsFactory()
   {
      if (vdxProductsFactory == null)
      {
         vdxProductsFactory = ProductsManager.getFactory(new File("src/test/resources/230V in LPC_.vd_"),
               persistenceUnitName);
      }

      return vdxProductsFactory;
   }
}
