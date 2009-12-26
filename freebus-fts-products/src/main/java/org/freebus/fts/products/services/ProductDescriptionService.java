package org.freebus.fts.products.services;

import java.util.List;

import org.freebus.fts.products.CatalogEntry;

/**
 * Interface for product description data access methods.
 */
public interface ProductDescriptionService
{
   /**
    * Get the product description lines for the given catalog entry.
    * @throws DAOException 
    */
   public List<String> getProductDescription(CatalogEntry entry) throws DAOException;
}
