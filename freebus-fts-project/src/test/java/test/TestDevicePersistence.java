package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestDevicePersistence
{
   private EntityManager entityManager;
   private CatalogEntry catalogEntry;
   private Manufacturer manufacturer;
   private Program program;
   private Parameter param1, param2;

   static
   {
      if (DatabaseResources.getEntityManagerFactory() == null)
         DatabaseResources.setEntityManagerFactory(DatabaseResources.createEntityManagerFactory(DriverType.HSQL_MEM, "test", "sa", ""));
   }

   @Before
   public final void setUp()
   {
      entityManager = DatabaseResources.getEntityManager();
      entityManager.getTransaction().begin();
      entityManager.getTransaction().setRollbackOnly();

      manufacturer = new Manufacturer(1, "manu-1");
      entityManager.persist(manufacturer);

      catalogEntry = new CatalogEntry(1, "cat-ent-1", manufacturer, null);
      entityManager.persist(catalogEntry);

      program = Utils.createProgram(1, "hello-bus", manufacturer);
      entityManager.persist(program);

      final Set<Parameter> params = program.getParameters();
      final Iterator<Parameter> paramsIt = params.iterator();
      param1 = paramsIt.next();
      param2 = paramsIt.next();

      entityManager.flush();
   }

   @After
   public final void tearDown()
   {
      entityManager.getTransaction().rollback();
   }

   @Test
   public void testDeviceWithProgram()
   {
      final Device dev = new Device(catalogEntry, program);
      dev.setId(17);
      dev.setAddress(88);

      entityManager.persist(dev);      
      entityManager.flush();
      entityManager.clear();

      final Device loadedDev = entityManager.find(Device.class, 17);
      assertNotNull(loadedDev);
      assertEquals(dev.getId(), loadedDev.getId());
      assertEquals(dev.getAddress(), loadedDev.getAddress());
      assertEquals(program, loadedDev.getProgram());
      assertEquals(dev, loadedDev);
   }

   @Test
   public void testDeviceWithProgramAndParameterValues()
   {
      final Device dev = new Device(catalogEntry, program);
      dev.setId(18);

      dev.setParameterValue(param1, 1001);
      dev.setParameterValue(param2, "a string value");

      entityManager.persist(dev);
      entityManager.flush();
      entityManager.clear();

      final Device loadedDev = entityManager.find(Device.class, 18);
      assertNotNull(loadedDev);

      assertEquals(1001, loadedDev.getIntParameterValue(param1));
      assertEquals("a string value", loadedDev.getParameterValue(param2));
   }
}
