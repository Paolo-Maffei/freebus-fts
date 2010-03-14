package org.freebus.fts.test_utils;

import org.freebus.fts.common.Environment;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectFactory;

public abstract class ProjectTestCase extends PersistenceTestCase
{
   private ProjectFactory projectFactory;

   static
   {
      Environment.init();
   }

   public ProjectTestCase(final String persistenceUnitName)
   {
      super(persistenceUnitName);
   }

   public ProjectTestCase()
   {
      this("test-full");
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
}
