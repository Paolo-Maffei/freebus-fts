package org.freebus.fts.persistence.db;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
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
