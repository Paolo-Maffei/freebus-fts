package org.freebus.fts.products.services;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.Program;
import org.freebus.fts.products.ProgramDescription;

/**
 * Interface for application program description data access methods.
 */
public interface ProgramDescriptionService
{
   /**
    * Get the program description for the given application program.
    * 
    * @throws DAOException
    */
   public ProgramDescription getProgramDescription(Program prog) throws DAOException;

   /**
    * Persist the given program description.
    */
   public void persist(ProgramDescription desc) throws PersistenceException;

   /**
    * Merge the state of the given application program description into the current
    * persistence context.
    * 
    * @return the application program. The returned object may be different from the given
    *         object.
    */
   public ProgramDescription merge(ProgramDescription desc) throws PersistenceException;
}
