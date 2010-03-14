package org.freebus.fts.project;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.fts.project.service.ProjectService;
import org.freebus.fts.test_utils.ProjectTestCase;
import org.junit.Test;


public class TestProjectService extends ProjectTestCase
{
   @Test
   public final void getProjectNamesGetProjects()
   {
      final ProjectService projectService = getProjectFactory().getProjectService();

      final Project project1 = new Project();
      project1.setName("Project-1");
      projectService.save(project1);

      final Project project2 = new Project();
      project2.setName("Project-2");
      projectService.save(project2);
      assertNotSame(project1.getId(), project2.getId());

      final Project project3 = new Project();
      project3.setName("Project-3");
      projectService.save(project3);
      assertNotSame(project1.getId(), project3.getId());
      assertNotSame(project2.getId(), project3.getId());

      DatabaseResources.getEntityManager().flush();

      final Map<Integer,String> projectNames = projectService.getProjectNames();
      assertNotNull(projectNames);

      assertTrue(projectNames.containsKey(project1.getId()));
      assertTrue(projectNames.containsKey(project2.getId()));
      assertTrue(projectNames.containsKey(project3.getId()));

      assertEquals("Project-1", projectNames.get(project1.getId()));
      assertEquals("Project-2", projectNames.get(project2.getId()));
      assertEquals("Project-3", projectNames.get(project3.getId()));

      final List<Project> projects = projectService.getProjects();
      assertTrue(projects.contains(project1));
      assertTrue(projects.contains(project2));
      assertTrue(projects.contains(project3));
   }

   @Test
   public final void saveGetProject()
   {
      final ProjectService projectService = getProjectFactory().getProjectService();

      final Project project = new Project();
      project.setName("Empty Project");
      projectService.save(project);
      DatabaseResources.getEntityManager().flush();
      assertTrue(project.getId() != 0);

      final Project loadedProject = projectService.getProject(project.getId());
      assertNotNull(loadedProject);
      assertSame(project, loadedProject);
   }

   @Test
   public final void saveGetSampleProject()
   {
      final ProjectService projectService = getProjectFactory().getProjectService();

      final Project project = SampleProjectFactory.newProject(persistenceUnitName);
      project.setName("Sample Project 1");
      projectService.save(project);
      DatabaseResources.getEntityManager().flush();

      final int projectId = project.getId();
      assertTrue(projectId != 0);
      DatabaseResources.getEntityManager().clear();

      final Project loadedProject = projectService.getProject(projectId);
      assertNotNull(loadedProject);
      assertEquals(project, loadedProject);
   }
}
