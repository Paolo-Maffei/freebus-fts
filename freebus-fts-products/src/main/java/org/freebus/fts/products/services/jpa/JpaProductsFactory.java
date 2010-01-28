package org.freebus.fts.products.services.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.products.services.ProductDescriptionService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.ProgramService;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Factory for accessing data access objects for the products database
 * via JPA.
 */
public final class JpaProductsFactory implements ProductsFactory
{
   protected final EntityManager entityManager = DatabaseResources.getEntityManager();

   @Override
   public CatalogEntryService getCatalogEntryService()
   {
      return new JpaCatalogEntryService(entityManager);
   }

   @Override
   public FunctionalEntityService getFunctionalEntityService()
   {
      return new JpaFunctionalEntityService(entityManager);
   }

   @Override
   public ManufacturerService getManufacturerService()
   {
      return new JpaManufacturerService(entityManager);
   }

   @Override
   public ProductDescriptionService getProductDescriptionService()
   {
      return new JpaProductDescriptionService(entityManager);
   }

   @Override
   public VirtualDeviceService getVirtualDeviceService()
   {
      return new JpaVirtualDeviceService(entityManager);
   }

   @Override
   public ProgramService getProgramService()
   {
      return new JpaProgramService(entityManager);
   }

   @Override
   public EntityTransaction getTransaction()
   {
      return entityManager.getTransaction();
   }
}
