package org.freebus.fts.project.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.fts.project.service.ProjectService;
import org.freebus.fts.test_utils.ProjectTestCase;
import org.junit.Test;

public class TestJpaProjectService extends ProjectTestCase
{
   public TestJpaProjectService()
   {
      super("test-full");
   }

   @Test
   public final void testPersist()
   {
      final ProjectService projectService = getProjectFactory().getProjectService();

      final Project project = SampleProjectFactory.newProject(persistenceUnitName);
      assertNotNull(project);

      projectService.persist(project);
      assertFalse(project.getId() == 0);

      DatabaseResources.getEntityManager().flush();
      DatabaseResources.getEntityManager().clear();

      final Project projectLoaded = projectService.getProject(project.getId());
      assertNotNull(projectLoaded);
   }
}
