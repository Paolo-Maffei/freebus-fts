package org.freebus.fts.test_utils;

import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectFactory;

public abstract class ProjectTestCase extends PersistenceTestCase
{
   private ProjectFactory projectFactory;
   private ProductsFactory productsFactory;

   public ProjectTestCase(final String persistenceUnitName)
   {
      super(persistenceUnitName);
   }

   public ProjectTestCase()
   {
      this("test-full");
   }

   /**
    * @return the unit-test project factory
    */
   public ProjectFactory getProjectFactory()
   {
      if (projectFactory == null)
         projectFactory = ProjectManager.getProjectFactory();

      return projectFactory;
   }

   /**
    * @return the unit-test products factory
    */
   public ProductsFactory getProductsFactory()
   {
      if (productsFactory == null)
         productsFactory = ProductsManager.getFactory();

      return productsFactory;
   }
}
