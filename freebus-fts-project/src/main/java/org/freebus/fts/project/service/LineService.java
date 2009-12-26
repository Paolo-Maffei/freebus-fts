package org.freebus.fts.project.service;

import javax.persistence.PersistenceException;

import org.freebus.fts.project.Line;

/**
 * Interface for {@link Line} data access objects (DAO).
 */
public interface LineService
{
   /**
    * Get the line with the given line-id.   
    */
   public Line getLine(int id) throws PersistenceException;
}
