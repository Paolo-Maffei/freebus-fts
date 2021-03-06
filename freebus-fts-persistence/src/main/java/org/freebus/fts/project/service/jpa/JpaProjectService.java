package org.freebus.fts.project.service.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
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

   /**
    * {@inheritDoc}
    */
   @Override
   public void persist(Project project)
   {
      entityManager.persist(project);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Project merge(Project project)
   {
      return entityManager.merge(project);
   }

   @Override
   public Map<Integer, String> getProjectNames()
   {
      final Query query = entityManager.createNativeQuery("select project_id,project_name from project");
      final Map<Integer, String> map = new HashMap<Integer, String>();

      @SuppressWarnings("unchecked")
      Vector<Object[]> results = (Vector<Object[]>) query.getResultList();
      for (Object[] fields : results)
      {
         map.put((Integer) fields[0], (String) fields[1]);
      }

      return map;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<Project> getProjects()
   {
      final Query query = entityManager.createQuery("select p from Project p", Project.class);

      final List<Project> result = query.getResultList();

      return result;
   }
}
