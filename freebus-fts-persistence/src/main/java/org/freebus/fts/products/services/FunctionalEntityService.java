package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;

/**
 * Interface for {@link FunctionalEntity} functional entities data access methods.
 */
public interface FunctionalEntityService
{
   /**
    * Get a functional entity by id.
    */
   public FunctionalEntity getFunctionalEntity(int id) throws PersistenceException;

   /**
    * Get all functional entities, sorted by name.
    */
   public List<FunctionalEntity> getFunctionalEntities() throws PersistenceException;

   /**
    * Get all functional entities of the given manufacturer, sorted by name.
    */
   public List<FunctionalEntity> getFunctionalEntities(Manufacturer m) throws PersistenceException;

   /**
    * Save a functional entity.
    */
   public void save(FunctionalEntity funcEnt);
}
