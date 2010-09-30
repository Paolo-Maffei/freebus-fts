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
    * @throws PersistenceException if the object does not exist
    */
   public BcuType getBcuType(int id) throws PersistenceException;

   /**
    * Get all BCU types.
    */
   public List<BcuType> getBcuTypes() throws PersistenceException;

   /**
    * Persist the given BCU type.
    */
   public void persist(BcuType bcuType) throws PersistenceException;

   /**
    * Merge the state of the given BCU type into the current persistence
    * context.
    * 
    * @return the BCU type. The returned object may be different from the
    *         given object.
    */
   public BcuType merge(BcuType bcuType) throws PersistenceException;
}
