package org.freebus.fts.project.service;

import javax.persistence.EntityTransaction;


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
    * Return the resource-level <code>EntityTransaction</code> object. 
    * The <code>EntityTransaction</code> instance may be used serially to 
    * begin and commit multiple transactions.
    * @return EntityTransaction instance
    * @throws IllegalStateException if invoked on a JTA
    *         entity manager
    */
   public EntityTransaction getTransaction();
}
