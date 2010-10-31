package org.freebus.fts.products.services.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.products.services.BcuTypeService;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.products.services.MaskService;
import org.freebus.fts.products.services.ProductDescriptionService;
import org.freebus.fts.products.services.ProductService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.ProgramDescriptionService;
import org.freebus.fts.products.services.ProgramService;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Factory for accessing data access objects for the org.freebus.fts.products database
 * via JPA.
 */
public final class JpaProductsFactory implements ProductsFactory
{
   private final EntityManager entityManager = DatabaseResources.getEntityManager();

   /**
    * {@inheritDoc}
    */
   @Override
   public EntityTransaction getTransaction()
   {
      return entityManager.getTransaction();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void flushEntityManager()
   {
      entityManager.flush();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CatalogEntryService getCatalogEntryService()
   {
      return new JpaCatalogEntryService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public FunctionalEntityService getFunctionalEntityService()
   {
      return new JpaFunctionalEntityService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ManufacturerService getManufacturerService()
   {
      return new JpaManufacturerService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProductService getProductService()
   {
      return new JpaProductService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProductDescriptionService getProductDescriptionService()
   {
      return new JpaProductDescriptionService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VirtualDeviceService getVirtualDeviceService()
   {
      return new JpaVirtualDeviceService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProgramService getProgramService()
   {
      return new JpaProgramService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public BcuTypeService getBcuTypeService()
   {
      return new JpaBcuTypeService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProgramDescriptionService getProgramDescriptionService()
   {
      return new JpaProgramDescriptionService(entityManager);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MaskService getMaskService()
   {
      return new JpaMaskService(entityManager);
   }
}
