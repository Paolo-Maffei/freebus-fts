package org.freebus.fts.products.dao;

import java.util.List;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;

/**
 * Interface for {@link FunctionalEntity} functional entities data access methods.
 */
public interface FunctionalEntityDAO
{
   /**
    * Get a functional entity by id.
    */
   public FunctionalEntity getFunctionalEntity(int id) throws DAOException;

   /**
    * Get all functional entities, sorted by name.
    */
   public List<FunctionalEntity> getFunctionalEntities() throws DAOException;

   /**
    * Get all functional entities of the given manufacturer, sorted by name.
    */
   public List<FunctionalEntity> getFunctionalEntities(Manufacturer m) throws DAOException;
}
