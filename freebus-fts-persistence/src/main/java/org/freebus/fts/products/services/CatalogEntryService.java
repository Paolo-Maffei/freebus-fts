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
    * 
    * @param id - the ID of the catalog entry to get.
    * @return The catalog entry.
    * 
    * @throws PersistenceException if the object cannot be read.
    */
   public CatalogEntry getCatalogEntry(int id) throws PersistenceException;

   /**
    * Get all catalog entries.
    * 
    * @return A list of all catalog entries.
    * @throws PersistenceException if the objects cannot be read.
    */
   public List<CatalogEntry> getCatalogEntries() throws PersistenceException;

   /**
    * Get all catalog entries of the given manufacturer that belong to one of
    * the given functional entities.
    * 
    * @param m - the manufacturer of the catalog entries to get.
    * @param functionalEntities - 
    * 
    * @return The list of catalog entries.
    * @throws PersistenceException if the objects cannot be read.
    */
   public List<CatalogEntry> getCatalogEntries(Manufacturer m, FunctionalEntity[] functionalEntities) throws PersistenceException;

   /**
    * Persist the given catalog entry.
    * 
    * @param catalogEntry - the catalog entry to persist.
    * 
    * @throws PersistenceException if the object cannot be persisted.
    */
   public void persist(CatalogEntry catalogEntry) throws PersistenceException;

   /**
    * Merge the state of the given catalog entry into the current persistence
    * context.
    *
    * @param catalogEntry - the catalog entry to merge.
    * 
    * @return the catalog entry. The returned object may be different from the
    *         given object.
    * @throws PersistenceException if the object cannot be merged.
    */
   public CatalogEntry merge(CatalogEntry catalogEntry) throws PersistenceException;
}
