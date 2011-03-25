package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;

/**
 * Interface for {@link FunctionalEntity} functional entities data access
 * methods.
 */
public interface FunctionalEntityService
{
   /**
    * Get a functional entity by id.
    * 
    * @param id - the id to get.
    * @return The functional entity.
    * @throws PersistenceException if the entity could not be read.
    */
   public FunctionalEntity getFunctionalEntity(int id) throws PersistenceException;

   /**
    * Get all functional entities, sorted by name.
    * 
    * @return A list of all functional entities.
    * @throws PersistenceException if the entity could not be read.
    */
   public List<FunctionalEntity> getFunctionalEntities() throws PersistenceException;

   /**
    * Get all functional entities of the given manufacturer, sorted by name.
    * 
    * @param m - the manufacturer to get the functional entities for.
    * 
    * @return A list of all functional entities.
    * @throws PersistenceException if the entity could not be read.
    */
   public List<FunctionalEntity> getFunctionalEntities(Manufacturer m) throws PersistenceException;

   /**
    * Persist a functional entity.
    * 
    * @param funcEnt - the functional entity to persist.
    * @throws PersistenceException if the entity could not be persisted.
    */
   public void persist(FunctionalEntity funcEnt) throws PersistenceException;

   /**
    * Merge the state of the given functional entity into the current
    * persistence context.
    * 
    * @param funcEnt - the functional entity to merge.
    * 
    * @return the functional entity. The returned object may be different from
    *         the given object.
    * @throws PersistenceException if the entity could not be merged.
    */
   public FunctionalEntity merge(FunctionalEntity funcEnt) throws PersistenceException;
}
