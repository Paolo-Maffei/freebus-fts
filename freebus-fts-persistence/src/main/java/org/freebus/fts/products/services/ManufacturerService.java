package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.Manufacturer;

/**
 * Interface for {@link Manufacturer} manufacturer data access methods.
 */
public interface ManufacturerService
{
   /**
    * Get a manufacturer by id.
    * 
    * @param id - the ID of the manufacturer to get.
    * 
    * @return The manufacturer
    * @throws PersistenceException if the object does not exist
    */
   Manufacturer getManufacturer(int id) throws PersistenceException;

   /**
    * Get a manufacturer by name.
    * 
    * @param name - the name of the manufacturer to get.
    * 
    * @return The manufacturer
    * @throws PersistenceException if the object does not exist
    */
   Manufacturer getManufacturer(String name) throws PersistenceException;

   /**
    * @return Get all manufacturers.
    * @throws PersistenceException in case of problems.
    */
   List<Manufacturer> getManufacturers() throws PersistenceException;

   /**
    * @return Get all manufacturers that own at least one functional entity.
    * @throws PersistenceException in case of problems.
    */
   List<Manufacturer> getActiveManufacturers() throws PersistenceException;

   /**
    * Save the manufacturer if no manufacturer with the same manufacturer-id
    * exists in the database.
    * 
    * @param manufacturer - the manufacturer to save.
    * 
    * @throws PersistenceException in case of problems.
    */
   void saveIfMissing(Manufacturer manufacturer) throws PersistenceException;

   /**
    * Persist the given manufacturer.
    * 
    * @param manufacturer - the manufacturer to persist.
    * 
    * @throws PersistenceException in case of problems.
    */
   void persist(Manufacturer manufacturer) throws PersistenceException;

   /**
    * Merge the state of the given manufacturer into the current persistence
    * context.
    * 
    * @param manufacturer - the manufacturer to merge.
    * 
    * @return The manufacturer. The returned object may be different from the
    *         given object.
    * @throws PersistenceException in case of problems.
    */
   public Manufacturer merge(Manufacturer manufacturer) throws PersistenceException;

   /**
    * Delete a manufacturer.
    * 
    * @param manufacturer - the manufacturer to delete.
    * @throws PersistenceException in case of problems.
    */
   public void remove(Manufacturer manufacturer) throws PersistenceException;
}
