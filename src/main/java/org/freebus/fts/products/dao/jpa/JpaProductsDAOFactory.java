package org.freebus.fts.products.dao.jpa;

import javax.persistence.EntityManager;

import org.freebus.fts.db.DatabaseResources;
import org.freebus.fts.products.dao.CatalogEntryDAO;
import org.freebus.fts.products.dao.FunctionalEntityDAO;
import org.freebus.fts.products.dao.ManufacturerDAO;
import org.freebus.fts.products.dao.ProductDescriptionDAO;
import org.freebus.fts.products.dao.ProductsDAOFactory;
import org.freebus.fts.products.dao.VirtualDeviceDAO;

/**
 * Factory for accessing data access objects for the products database
 * via JPA.
 */
public final class JpaProductsDAOFactory implements ProductsDAOFactory
{
   protected final EntityManager entityManager = DatabaseResources.getEntityManager();

   @Override
   public CatalogEntryDAO getCatalogEntryDAO()
   {
      return new JpaCatalogEntryDAO(entityManager);
   }

   @Override
   public FunctionalEntityDAO getFunctionalEntityDAO()
   {
      return new JpaFunctionalEntityDAO(entityManager);
   }

   @Override
   public ManufacturerDAO getManufacturerDAO()
   {
      return new JpaManufacturerDAO(entityManager);
   }

   @Override
   public ProductDescriptionDAO getProductDescriptionDAO()
   {
      return new JpaProductDescriptionDAO(entityManager);
   }

   @Override
   public VirtualDeviceDAO getVirtualDeviceDAO()
   {
      return new JpaVirtualDeviceDAO(entityManager);
   }
}
