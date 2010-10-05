package org.freebus.fts.products.services;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.ProductDescription;

/**
 * Interface for product description data access methods.
 */
public interface ProductDescriptionService
{
   /**
    * Get the product description for the given catalog entry.
    * 
    * @throws DAOException
    */
   public ProductDescription getProductDescription(CatalogEntry entry) throws DAOException;

   /**
    * Persist the given product description.
    */
   public void persist(ProductDescription desc) throws PersistenceException;

   /**
    * Merge the state of the given product description into the current
    * persistence context.
    * 
    * @return the product. The returned object may be different from the given
    *         object.
    */
   public ProductDescription merge(ProductDescription desc) throws PersistenceException;
}
