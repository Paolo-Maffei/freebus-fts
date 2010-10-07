package org.freebus.fts.test_utils;

import static org.junit.Assert.assertNotNull;

import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.SampleProjectFactory;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.service.ProjectFactory;

public abstract class ProjectTestCase extends PersistenceTestCase
{
   private ProjectFactory projectFactory;
   private Project project;

   public ProjectTestCase(final String persistenceUnitName)
   {
      super(persistenceUnitName);
   }

   public ProjectTestCase()
   {
      this("default");
   }

   /**
    * @return the unit-test project factory
    */
   public ProjectFactory getProjectFactory()
   {
      if (projectFactory == null)
         projectFactory = ProjectManager.getProjectFactory();

      return projectFactory;
   }

   /**
    * @return the unit-test project
    */
   public Project getProject()
   {
      if (project == null)
      {
         project = SampleProjectFactory.newProject(persistenceUnitName);
         assertNotNull(project);

         final Device dev = project.getAreas().get(0).getLines().get(0).getDevices().get(0);
         assertNotNull(dev);

         final SubGroup subGroup1 = project.getMainGroups().get(0).getMidGroups().get(0).getSubGroups().get(0);
         subGroup1.add(dev.getDeviceObjects().get(0));

         final SubGroup subGroup2 = project.getMainGroups().get(0).getMidGroups().get(0).getSubGroups().get(1);
         subGroup2.add(dev.getDeviceObjects().get(1));
      }

      return project;
   }
}
