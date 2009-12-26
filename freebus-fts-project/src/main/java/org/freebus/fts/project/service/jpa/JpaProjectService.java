package org.freebus.fts.project.service.jpa;

import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.project.Project;
import org.freebus.fts.project.service.ProjectService;


public final class JpaProjectService implements ProjectService
{
   private final EntityManager entityManager;

   JpaProjectService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public Project getProject(int id)
   {
      return entityManager.find(Project.class, id);
   }

   @SuppressWarnings("unchecked")
   protected int getFreeProjectId()
   {
      try
      {
         final Query query = entityManager.createNativeQuery("select max(project_id) from project");
         final Object obj = ((Vector<Object>) query.getSingleResult()).get(0);
         if (obj == null) return 1;
         return ((Integer) obj) + 1;
      }
      catch (NoResultException e)
      {
         return 1;
      }
   }

   @Override
   public void save(Project project) throws PersistenceException
   {
      try
      {
         if (project.getId() <= 0)
            project.setId(getFreeProjectId());

         entityManager.getTransaction().begin();
         entityManager.persist(project);
         entityManager.getTransaction().commit();
      }
      catch (PersistenceException e)
      {
         throw new PersistenceException("Cannot save project", e);
      }
   }
}
