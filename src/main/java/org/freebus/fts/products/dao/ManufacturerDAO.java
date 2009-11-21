package org.freebus.fts.products.dao;

import java.util.List;

import org.freebus.fts.products.Manufacturer;

/**
 * Interface for {@link Manufacturer} manufacturer data access methods.
 */
public interface ManufacturerDAO
{
   /**
    * Get a manufacturer by id.
    * 
    * @throws DAONotFoundException if the object does not exist
    */
   public Manufacturer getManufacturer(int manufacturerId) throws DAOException;

   /**
    * Get all manufacturers.
    */
   public List<Manufacturer> getManufacturers() throws DAOException;

   /**
    * Get all manufacturers that own at least one functional entity. 
    */
   public List<Manufacturer> getActiveManufacturers() throws DAOException;
}
