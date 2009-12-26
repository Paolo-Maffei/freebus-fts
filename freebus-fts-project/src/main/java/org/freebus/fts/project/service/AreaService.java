package org.freebus.fts.project.service;

import javax.persistence.PersistenceException;

import org.freebus.fts.project.Area;

/**
 * Interface for {@link Area} data access objects (DAO).
 */
public interface AreaService
{
   /**
    * Get the area with the given area-id.   
    */
   public Area getArea(int areaId) throws PersistenceException;
}
