package org.freebus.fts.products.services;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.Mask;

/**
 * Interface for {@link Mask} data access methods.
 */
public interface MaskService
{
   /**
    * Get a mask by id.
    */
   public Mask getMask(int id) throws PersistenceException;

   /**
    * Persist the given mask.
    */
   public void persist(Mask mask) throws PersistenceException;

   /**
    * Merge the state of the given mask into the current persistence
    * context.
    *
    * @return The mask. The returned object may be different from the
    *         given object.
    */
   public Mask merge(Mask mask) throws PersistenceException;
}
