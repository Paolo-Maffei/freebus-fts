package org.freebus.fts.products.dao;

/**
 * Interface for factories that generate data access objects for the products
 * database.
 */
public interface ProductsDAOFactory
{
   /**
    * @return the DAO for catalog entry queries.
    */
   public CatalogEntryDAO getCatalogEntryDAO();

   /**
    * @return the DAO for functional entity queries.
    */
   public FunctionalEntityDAO getFunctionalEntityDAO();

   /**
    * @return the DAO for manufacturer queries.
    */
   public ManufacturerDAO getManufacturerDAO();

   /**
    * @return the DAO for product-description queries.
    */
   public ProductDescriptionDAO getProductDescriptionDAO();

   /**
    * @return the DAO for virtual-device queries.
    */
   public VirtualDeviceDAO getVirtualDeviceDAO();
}
