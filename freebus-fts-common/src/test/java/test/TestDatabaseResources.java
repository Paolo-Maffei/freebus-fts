package test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.TestCase;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;


public class TestDatabaseResources extends TestCase
{
   public void testCreateEntityManagerFactory()
   {
      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(DriverType.HSQL_MEM, "test", "sa", "");
      assertNotNull(emf);

      EntityManager em = emf.createEntityManager();
      assertNotNull(em);
   }
}
