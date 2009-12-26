package org.freebus.fts.project.service;


/**
 * Interface for factories that generate data access objects for a project.
 */
public interface ProjectFactory
{
   /**
    * @return the DAO for project queries.
    */
   public ProjectService getProjectService();

   /**
    * @return the DAO for area queries.
    */
   public AreaService getAreaService();
}
