package org.freebus.fts.products.services;

import javax.persistence.EntityTransaction;

/**
 * Interface for factories that generate data access objects for the products
 * database.
 */
public interface ProductsFactory
{
   /**
    * @return the service for catalog entry queries.
    */
   public CatalogEntryService getCatalogEntryService();

   /**
    * @return the service for functional entity queries.
    */
   public FunctionalEntityService getFunctionalEntityService();

   /**
    * @return the service for manufacturer queries.
    */
   public ManufacturerService getManufacturerService();

   /**
    * @return the service for product-description queries.
    */
   public ProductDescriptionService getProductDescriptionService();

   /**
    * @return the service for virtual-device queries.
    */
   public VirtualDeviceService getVirtualDeviceService();

   /**
    * @return the service for application-program queries.
    */
   public ProgramService getProgramService();

   /**
    * Return the resource-level <code>EntityTransaction</code> object. 
    * The <code>EntityTransaction</code> instance may be used serially to 
    * begin and commit multiple transactions.
    * @return EntityTransaction instance
    * @throws IllegalStateException if invoked on a JTA
    *         entity manager
    */
   public EntityTransaction getTransaction();
}
