package org.freebus.fts.project.service;

import java.util.List;
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
    * Persist the given project.
    */
   public void persist(Project project) throws PersistenceException;

   /**
    * Merge the state of the given project into the current persistence
    * context.
    * 
    * @return the project. The returned object may be different from the
    *         given object.
    */
   public Project merge(Project project) throws PersistenceException;

   /**
    * Get all available projects.
    */
   public List<Project> getProjects();

   /**
    * Get a list of all available projects.
    * 
    * @return a list of project names and project ids. Key is the project-id,
    *         value is the project name.
    */
   public Map<Integer, String> getProjectNames();
}
