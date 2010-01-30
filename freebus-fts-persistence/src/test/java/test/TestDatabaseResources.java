package test;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;
import org.junit.Test;


public class TestDatabaseResources
{
   @Test
   public void testCreateEntityManagerFactory()
   {
      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(DriverType.HSQL_MEM, "test", "sa", "");
      assertNotNull(emf);

      EntityManager em = emf.createEntityManager();
      assertNotNull(em);
   }
}
