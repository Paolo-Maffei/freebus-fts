package test;

import java.util.Map;

import org.freebus.fts.project.Project;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.fts.project.service.ProjectService;

public class TestProjectService extends PersistenceTestCase
{
   //
   // Must be the first test - depends on an empty projects table.
   //
   public final void testGetProjectNames()
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

      final Map<Integer,String> projectNames = projectService.getProjectNames();
      assertNotNull(projectNames);

      assertTrue(projectNames.containsKey(project1.getId()));
      assertTrue(projectNames.containsKey(project2.getId()));
      assertTrue(projectNames.containsKey(project3.getId()));

      assertEquals("Project-1", projectNames.get(project1.getId()));
      assertEquals("Project-2", projectNames.get(project2.getId()));
      assertEquals("Project-3", projectNames.get(project3.getId()));
   }

   public final void testSaveGetProject()
   {
      final ProjectService projectService = getProjectFactory().getProjectService();

      final Project project = new Project();
      project.setName("Empty Project");
      projectService.save(project);
      assertTrue(project.getId() != 0);

      final Project loadedProject = projectService.getProject(project.getId());
      assertNotNull(loadedProject);
      assertSame(project, loadedProject);
   }

   public final void testSaveGetSampleProject()
   {
      final ProjectService projectService = getProjectFactory().getProjectService();

      final Project project = SampleProjectFactory.newProject();
      project.setName("Sample Project");
      projectService.save(project);
      assertTrue(project.getId() != 0);

      final Project loadedProject = projectService.getProject(project.getId());
      assertNotNull(loadedProject);
      assertSame(project, loadedProject);
   }
}
