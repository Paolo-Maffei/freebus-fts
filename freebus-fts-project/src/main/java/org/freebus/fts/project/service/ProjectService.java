package org.freebus.fts.project.service;

import java.util.Map;

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

   /**
    * Get a list of all available projects.
    * 
    * @return a list of project names and project ids. Key is the project-id,
    *         value is the project name.
    */
   public Map<Integer, String> getProjectNames();
}
