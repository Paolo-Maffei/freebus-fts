package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.BcuType;

/**
 * Interface for {@link BcuType BCU type} data access methods.
 */
public interface BcuTypeService
{
   /**
    * Get a BCU type by id.
    * 
    * @param id - the id to get.
    * @return The BCU type.
    * 
    * @throws PersistenceException if the object does not exist
    */
   BcuType getBcuType(int id) throws PersistenceException;

   /**
    * Get all BCU types.
    * 
    * @return A list with all BCU types.
    * @throws PersistenceException if the objects cannot be read.
    */
   List<BcuType> getBcuTypes() throws PersistenceException;

   /**
    * Persist the given BCU type.
    * 
    * @param bcuType - the BCU type to persist.
    * @throws PersistenceException if the object cannot be persisted.
    */
   void persist(BcuType bcuType) throws PersistenceException;

   /**
    * Merge the state of the given BCU type into the current persistence
    * context.
    * 
    * @param bcuType - the BCU type to merge.
    * 
    * @return the BCU type. The returned object may be different from the given
    *         object.
    * @throws PersistenceException if the object cannot be merged.
    */
   BcuType merge(BcuType bcuType) throws PersistenceException;
}
