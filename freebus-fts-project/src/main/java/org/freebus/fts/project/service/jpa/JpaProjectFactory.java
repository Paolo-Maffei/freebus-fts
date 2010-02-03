package org.freebus.fts.project.service.jpa;

import javax.persistence.EntityTransaction;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.project.service.AreaService;
import org.freebus.fts.project.service.ProjectFactory;
import org.freebus.fts.project.service.ProjectService;

/**
 * Factory for accessing data access objects for the project database
 * via JPA.
 */
public final class JpaProjectFactory implements ProjectFactory
{
   @Override
   public ProjectService getProjectService()
   {
      return new JpaProjectService(DatabaseResources.getEntityManager());
   }

   @Override
   public AreaService getAreaService()
   {
      return new JpaAreaService(DatabaseResources.getEntityManager());
   }

   @Override
   public EntityTransaction getTransaction()
   {
      return DatabaseResources.getEntityManager().getTransaction();
   }
}
