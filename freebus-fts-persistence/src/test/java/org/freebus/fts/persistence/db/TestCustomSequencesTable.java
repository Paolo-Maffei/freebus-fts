package org.freebus.fts.persistence.db;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.freebus.fts.persistence.test_entities.SampleEntity;
import org.junit.Test;

/**
 * This test tests the creation of a simple database schema with non-JPA
 * methods, especially a sequences table. And then the use of the created schema
 * with an entity manager.
 * <p>
 * The not so obvious part here is that (at least) EclipseLink requires that the
 * sequences table is populated with an initial record "SEQ_GEN_TABLE". If this
 * record is missing, EclipseLink will fail with an exception:
 * "The sequence table information is not complete".
 */
public class TestCustomSequencesTable
{
   @Test
   public void testCustomSequencesTable() throws Exception
   {
      final ConnectionDetails conDetails = new ConnectionDetails(DriverType.HSQL_MEM, "TestCustomSequencesTable");

      final Connection con = DatabaseResources.createConnection(conDetails);
      assertNotNull(con);
      con.setAutoCommit(true);

      PreparedStatement stmt = con.prepareStatement("create table SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, "
            + "SEQ_COUNT NUMERIC(38), PRIMARY KEY (SEQ_NAME))");
      stmt.execute();

      stmt = con.prepareStatement("create table sample_entity (project_id INT NOT NULL, name VARCHAR(255), PRIMARY KEY (project_id))");
      stmt.execute();
      stmt.close();

      stmt = con.prepareStatement("insert into SEQUENCE values('SEQ_GEN_TABLE', 0)");
      stmt.execute();
      stmt.close();

      final EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("custom-seq-table", conDetails);
      assertNotNull(emf);

      final EntityManager em = emf.createEntityManager();
      assertNotNull(em);

      con.close();

      final SampleEntity obj1 = new SampleEntity();
      obj1.name = "obj-1";
      em.persist(obj1);
   }
}
