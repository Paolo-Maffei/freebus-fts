package org.freebus.fts.project.service;

import javax.persistence.PersistenceException;

import org.freebus.fts.project.Project;

/**
 * Interface for project data access objects (DAO).
 */
public interface ProjectService
{
   /**
    * Get the project with the given project-id.   
    */
   public Project getProject(int id) throws PersistenceException;

   /**
    * Save the project.
    */
   public void save(Project project) throws PersistenceException;
}
