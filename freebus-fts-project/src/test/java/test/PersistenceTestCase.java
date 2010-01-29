package test;

import static org.junit.Assert.assertTrue;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersistenceTestCase
{
   private static ProjectFactory projectFactory;
   private boolean setupCalled;

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

   @Before
   public void setUp() throws Exception
   {
      setupCalled = true;
      getProjectFactory().getTransaction().begin();
      getProjectFactory().getTransaction().setRollbackOnly();
   }

   @After
   public void tearDown() throws Exception
   {
      getProjectFactory().getTransaction().rollback();
   }

   @Test
   public void setupCalled()
   {
      assertTrue(setupCalled);
   }
}
