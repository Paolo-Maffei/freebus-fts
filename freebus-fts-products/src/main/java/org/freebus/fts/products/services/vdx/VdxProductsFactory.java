package org.freebus.fts.products.services.vdx;

import java.io.IOException;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxEntityManager;
import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.DAOException;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ManufacturerService;
import org.freebus.fts.products.services.ProductDescriptionService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.ProgramService;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Factory for accessing data access objects for the products database
 * that are stored in a vd_ file.
 *
 * The access is read-only for now.
 */
public final class VdxProductsFactory implements ProductsFactory
{
   private VdxFileReader reader;
   private VdxEntityManager manager;
   private VdxEntityTransaction transaction = new VdxEntityTransaction();

   private CatalogEntryService catalogEntryService;
   private FunctionalEntityService functionalEntityService;
   private ManufacturerService manufacturerService;
   private VirtualDeviceService virtualDeviceService;
   private ProductDescriptionService productDescriptionService;
   private ProgramService programService;

   /**
    * Create a factory for the file fileName.
    * 
    * @param fileName - the name of the vd_ file that is processed.
    * @throws DAOException
    */
   public VdxProductsFactory(String fileName) throws PersistenceException
   {
      try
      {
         manager = new VdxEntityManager(fileName);
         reader = new VdxFileReader(fileName);
      }
      catch (IOException e)
      {
         throw new PersistenceException(e);
      }
   }

   @Override
   public CatalogEntryService getCatalogEntryService()
   {
      if (catalogEntryService == null) catalogEntryService = new VdxCatalogEntryService(manager, getVirtualDeviceService());
      return catalogEntryService;
   }

   @Override
   public FunctionalEntityService getFunctionalEntityService()
   {
      if (functionalEntityService == null) functionalEntityService = new VdxFunctionalEntityService(manager);
      return functionalEntityService;
   }

   @Override
   public ManufacturerService getManufacturerService()
   {
      if (manufacturerService == null) manufacturerService = new VdxManufacturerService(manager, getFunctionalEntityService());
      return manufacturerService;
   }

   @Override
   public ProductDescriptionService getProductDescriptionService()
   {
      if (productDescriptionService == null) productDescriptionService = new VdxProductDescriptionService(reader);
      return productDescriptionService;
   }

   @Override
   public VirtualDeviceService getVirtualDeviceService()
   {
      if (virtualDeviceService == null) virtualDeviceService = new VdxVirtualDeviceService(manager);
      return virtualDeviceService;
   }

   @Override
   public ProgramService getProgramService()
   {
      if (programService == null) programService = new VdxProgramService(reader);
      return programService;
   }

   @Override
   public EntityTransaction getTransaction()
   {
      return transaction;
   }
}
