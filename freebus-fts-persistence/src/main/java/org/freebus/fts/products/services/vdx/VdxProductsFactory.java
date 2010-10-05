package org.freebus.fts.products.services.vdx;

import java.io.File;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxEntityManager;
import org.freebus.fts.products.services.BcuTypeService;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.products.services.ProductDescriptionService;
import org.freebus.fts.products.services.ProductService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.ProgramDescriptionService;
import org.freebus.fts.products.services.ProgramService;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Factory for accessing data access objects for the org.freebus.fts.products
 * database that are stored in a vd_ file.
 * 
 * The access is read-only for now.
 */
public final class VdxProductsFactory implements ProductsFactory
{
   private final VdxEntityManager manager;
   private VdxEntityTransaction transaction = new VdxEntityTransaction();

   private CatalogEntryService catalogEntryService;
   private FunctionalEntityService functionalEntityService;
   private ManufacturerService manufacturerService;
   private VirtualDeviceService virtualDeviceService;
   private ProductService productService;
   private ProductDescriptionService productDescriptionService;
   private ProgramDescriptionService programDescriptionService;
   private ProgramService programService;
   private BcuTypeService bcuTypeService;

   /**
    * @return the entity manager.
    */
   public VdxEntityManager getEntityManager()
   {
      return manager;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void flushEntityManager()
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   /**
    * Create a factory for the file fileName.
    * 
    * @param file - the vd_ file that is processed.
    * @param persistenceUnitName - the name of the persistence unit.
    * 
    * @throws PersistenceException
    */
   public VdxProductsFactory(File file, String persistenceUnitName) throws PersistenceException
   {
      manager = new VdxEntityManager(file, persistenceUnitName);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CatalogEntryService getCatalogEntryService()
   {
      if (catalogEntryService == null)
         catalogEntryService = new VdxCatalogEntryService(manager, getVirtualDeviceService());
      return catalogEntryService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public FunctionalEntityService getFunctionalEntityService()
   {
      if (functionalEntityService == null)
         functionalEntityService = new VdxFunctionalEntityService(manager);
      return functionalEntityService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ManufacturerService getManufacturerService()
   {
      if (manufacturerService == null)
         manufacturerService = new VdxManufacturerService(manager, getFunctionalEntityService());
      return manufacturerService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProductService getProductService()
   {
      if (productService == null)
         productService = new VdxProductService(manager.getReader());
      return productService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProductDescriptionService getProductDescriptionService()
   {
      if (productDescriptionService == null)
         productDescriptionService = new VdxProductDescriptionService(manager.getReader());
      return productDescriptionService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VirtualDeviceService getVirtualDeviceService()
   {
      if (virtualDeviceService == null)
         virtualDeviceService = new VdxVirtualDeviceService(manager);
      return virtualDeviceService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProgramService getProgramService()
   {
      if (programService == null)
         programService = new VdxProgramService(manager.getReader());
      return programService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EntityTransaction getTransaction()
   {
      return transaction;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public BcuTypeService getBcuTypeService()
   {
      if (bcuTypeService == null)
         bcuTypeService = new VdxBcuTypeService(manager.getReader());
      return bcuTypeService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProgramDescriptionService getProgramDescriptionService()
   {
      if (programDescriptionService == null)
         programDescriptionService = new VdxProgramDescriptionService(manager.getReader());
      return programDescriptionService;
   }
}
