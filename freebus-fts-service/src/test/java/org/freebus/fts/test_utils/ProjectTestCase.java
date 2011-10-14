package org.freebus.fts.test_utils;

import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

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

   public ProjectTestCase()
   {
      this("default");
   }

   public ProjectTestCase(final String persistenceUnitName)
   {
      super(persistenceUnitName);
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
         final SampleProjectFactory projectFactory = new SampleProjectFactory(persistenceUnitName);
         project = projectFactory.newProject();
         assertNotNull(project);

         final Device dev = project.getAreas().iterator().next().getLines().iterator().next().getDevices().iterator().next();
         assertNotNull(dev);

         final Iterator<SubGroup> sgIterator = project.getMainGroups().iterator().next().getMidGroups().iterator().next().getSubGroups().iterator();

         final SubGroup subGroup1 = sgIterator.next();
         subGroup1.add(dev.getDeviceObjects().get(0));

         final SubGroup subGroup2 = sgIterator.next();
         subGroup2.add(dev.getDeviceObjects().get(1));
      }

      return project;
   }
}
