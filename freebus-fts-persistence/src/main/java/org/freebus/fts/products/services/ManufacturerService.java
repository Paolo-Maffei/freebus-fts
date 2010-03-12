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
    * @throws DAONotFoundException if the object does not exist
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
    * Save a manufacturer.
    */
   public void save(Manufacturer manufacturer) throws PersistenceException;

   /**
    * Save multiple manufacturers.
    */
   public void save(List<Manufacturer> manufacturers) throws PersistenceException;

   /**
    * Delete a manufacturer.
    */
   public void remove(Manufacturer manufacturer) throws PersistenceException;
}
