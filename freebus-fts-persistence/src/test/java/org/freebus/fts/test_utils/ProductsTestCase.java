package org.freebus.fts.test_utils;

import java.io.File;

import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.services.ProductsFactory;
import org.junit.After;
import org.junit.Before;

public abstract class ProductsTestCase extends OldPersistenceTestCase
{
   private ProductsFactory jpaProductsFactory;
   private ProductsFactory vdxProductsFactory;

   public ProductsTestCase(final String persistenceUnitName)
   {
      super(persistenceUnitName);
   }

   public ProductsTestCase()
   {
      this("test-full");
   }

   /**
    * Setup for the next test
    */
   @Before
   public synchronized final void setUpProductsTestCase()
   {
      jpaProductsFactory = null;
      vdxProductsFactory = null;
   }

   /**
    * Cleanup
    */
   @After
   public synchronized final void tearDownProductsTestCase()
   {
      jpaProductsFactory = null;
      vdxProductsFactory = null;
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
         vdxProductsFactory = ProductsManager.getFactory(new File("src/test/resources/test-device.vd_"),
               persistenceUnitName);
      }

      return vdxProductsFactory;
   }
}
