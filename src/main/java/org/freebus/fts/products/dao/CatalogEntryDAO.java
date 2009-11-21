package org.freebus.fts.products.dao;

import java.util.List;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;

/**
 * Interface for {@link CatalogEntry} catalog entry data access methods.
 */
public interface CatalogEntryDAO
{
   /**
    * Get a catalog entry by id.
    */
   public CatalogEntry getCatalogEntry(int id) throws DAOException;

   /**
    * Get all catalog entries.
    */
   public List<CatalogEntry> getCatalogEntries() throws DAOException;

   /**
    * Get all catalog entries of the given manufacturer that belong to one of
    * the given functional entities.
    */
   public List<CatalogEntry> getCatalogEntries(Manufacturer m, FunctionalEntity[] functionalEntities)
         throws DAOException;
}
