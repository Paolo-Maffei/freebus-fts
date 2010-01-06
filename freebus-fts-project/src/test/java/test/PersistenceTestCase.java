package test;

import junit.framework.TestCase;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectFactory;

public class PersistenceTestCase extends TestCase
{
   private static ProjectFactory projectFactory;

   static
   {
      if (DatabaseResources.getEntityManagerFactory() == null)
         DatabaseResources.setEntityManagerFactory(DatabaseResources.createEntityManagerFactory(DriverType.HSQL_MEM, "test", "sa", ""));
   }

   public static synchronized ProjectFactory getProjectFactory()
   {
      if (projectFactory == null) projectFactory = ProjectManager.getDAOFactory();
      return projectFactory;
   }
}
