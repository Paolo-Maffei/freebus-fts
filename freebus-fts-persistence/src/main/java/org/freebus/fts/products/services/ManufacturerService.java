package org.freebus.fts.products.services;

import java.util.Collection;
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
    * @throws PersistenceException if the object does not exist
    */
   public Manufacturer getManufacturer(int manufacturerId) throws PersistenceException;

   /**
    * Get all manufacturers.
    */
   public List<Manufacturer> getManufacturers() throws PersistenceException;

   /**
    * Get all manufacturers that own at least one functional entity.
    */
   public List<Manufacturer> getActiveManufacturers() throws PersistenceException;

   /**
    * Save the manufacturer if no manufacturer with the same manufacturer-id
    * exists in the database.
    */
   public void saveIfMissing(Manufacturer manufacturer) throws PersistenceException;

   /**
    * Persist the given manufacturer.
    * 
    * @return the manufacturer. The returned object may be different from the
    *         given object.
    */
   public void persist(Manufacturer manufacturer) throws PersistenceException;

   /**
    * Merge the state of the given manufacturer into the current persistence
    * context.
    */
   public Manufacturer merge(Manufacturer manufacturer) throws PersistenceException;

   /**
    * Save multiple manufacturers.
    */
   @Deprecated
   public void save(Collection<Manufacturer> manufacturers) throws PersistenceException;

   /**
    * Delete a manufacturer.
    */
   public void remove(Manufacturer manufacturer) throws PersistenceException;
}
