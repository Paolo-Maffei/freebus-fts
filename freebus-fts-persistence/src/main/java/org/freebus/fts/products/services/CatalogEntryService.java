package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;

/**
 * Interface for {@link CatalogEntry} catalog entry data access methods.
 */
public interface CatalogEntryService
{
   /**
    * Get a catalog entry by id.
    */
   public CatalogEntry getCatalogEntry(int id) throws PersistenceException;

   /**
    * Get all catalog entries.
    */
   public List<CatalogEntry> getCatalogEntries() throws PersistenceException;

   /**
    * Get all catalog entries of the given manufacturer that belong to one of
    * the given functional entities.
    */
   public List<CatalogEntry> getCatalogEntries(Manufacturer m, FunctionalEntity[] functionalEntities)
         throws PersistenceException;

   /**
    * Save a catalog entry.
    */
   public void save(CatalogEntry catalogEntry) throws PersistenceException;
}
