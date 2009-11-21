package org.freebus.fts.products.dao.vdx;

import java.io.IOException;

import org.freebus.fts.products.dao.CatalogEntryDAO;
import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.FunctionalEntityDAO;
import org.freebus.fts.products.dao.ManufacturerDAO;
import org.freebus.fts.products.dao.ProductDescriptionDAO;
import org.freebus.fts.products.dao.ProductsDAOFactory;
import org.freebus.fts.products.dao.VirtualDeviceDAO;
import org.freebus.fts.vdx.VdxFileReader;

/**
 * Factory for accessing data access objects for the products database
 * that are stored in a vd_ file.
 *
 * The access is read-only for now.
 */
public final class VdxProductsDAOFactory implements ProductsDAOFactory
{
   private VdxFileReader reader;

   private CatalogEntryDAO catalogEntryDAO;
   private FunctionalEntityDAO functionalEntityDAO;
   private ManufacturerDAO manufacturerDAO;
   private VirtualDeviceDAO virtualDeviceDAO;

   /**
    * Create a factory for the file fileName.
    * 
    * @param fileName - the name of the vd_ file that is processed.
    * @throws DAOException
    */
   public VdxProductsDAOFactory(String fileName) throws DAOException
   {
      try
      {
         reader = new VdxFileReader(fileName);
      }
      catch (IOException e)
      {
         throw new DAOException(e);
      }
   }

   @Override
   public CatalogEntryDAO getCatalogEntryDAO()
   {
      if (catalogEntryDAO == null) catalogEntryDAO = new VdxCatalogEntryDAO(reader, getVirtualDeviceDAO());
      return catalogEntryDAO;
   }

   @Override
   public FunctionalEntityDAO getFunctionalEntityDAO()
   {
      if (functionalEntityDAO == null) functionalEntityDAO = new VdxFunctionalEntityDAO(reader);
      return functionalEntityDAO;
   }

   @Override
   public ManufacturerDAO getManufacturerDAO()
   {
      if (manufacturerDAO == null) manufacturerDAO = new VdxManufacturerDAO(reader, getFunctionalEntityDAO());
      return manufacturerDAO;
   }

   @Override
   public ProductDescriptionDAO getProductDescriptionDAO()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public VirtualDeviceDAO getVirtualDeviceDAO()
   {
      if (virtualDeviceDAO == null) virtualDeviceDAO = new VdxVirtualDeviceDAO(reader);
      return virtualDeviceDAO;
   }
}
