package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.Program;

/**
 * Interface for {@link Program} application-program data access methods.
 */
public interface ProgramService
{
   /**
    * Get a program by id.
    *
    * @throws PersistenceException if the object does not exist
    */
   public Program getProgram(int id) throws PersistenceException;

   /**
    * Get all programs.
    */
   public List<Program> getPrograms() throws PersistenceException;

   /**
    * Save a program.
    */
   public void save(Program program);
}
