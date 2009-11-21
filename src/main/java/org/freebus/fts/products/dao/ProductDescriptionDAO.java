package org.freebus.fts.products.dao;

import java.util.List;

import org.freebus.fts.products.CatalogEntry;

/**
 * Interface for product description data access methods.
 */
public interface ProductDescriptionDAO
{
   /**
    * Get the product description lines for the given catalog entry.
    */
   public List<String> getProductDescription(CatalogEntry entry);
}
