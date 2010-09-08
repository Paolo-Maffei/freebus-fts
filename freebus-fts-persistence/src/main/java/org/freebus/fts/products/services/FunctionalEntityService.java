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
    * Persist a functional entity.
    * 
    * @param funcEnt - the functional entity to persist.
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
    */
   public FunctionalEntity merge(FunctionalEntity funcEnt) throws PersistenceException;
}
