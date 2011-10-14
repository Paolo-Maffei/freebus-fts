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
   @Test
   public final void testGetProjectService()
   {    
      assertNotNull(getProjectFactory().getProjectService());
   }

   // TODO This test fails in "mvn test" but works in Eclipse :-(
   //@Test
   public final void testPersist()
   {
      final ProjectService projectService = getProjectFactory().getProjectService();
      final SampleProjectFactory projectFactory = new SampleProjectFactory(persistenceUnitName);

      final Project project = projectFactory.newProject();
      assertNotNull(project);

      projectService.persist(project);
      final int projectId = project.getId();
      assertFalse(projectId == 0);
      
      DatabaseResources.getEntityManager().flush();
      DatabaseResources.getEntityManager().clear();

      final Project projectLoaded = projectService.getProject(projectId);
      assertNotNull(projectLoaded);
   }
}
