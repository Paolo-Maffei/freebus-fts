package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.Manufacturer;
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
    * Find a program by manufacturer and device type.
    * 
    * @param manufacturer - the manufacturer of the program
    * @param deviceType - the device type id
    * 
    * @return A list of matching programs
    */
   public List<Program> findProgram(Manufacturer manufacturer, int deviceType);
   
   /**
    * Persist the given program.
    */
   public void persist(Program program) throws PersistenceException;

   /**
    * Merge the state of the given program into the current persistence
    * context.
    * 
    * @return the program. The returned object may be different from the
    *         given object.
    */
   public Program merge(Program program) throws PersistenceException;
}
