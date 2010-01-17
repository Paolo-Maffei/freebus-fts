package test;

import java.util.List;

import junit.framework.TestCase;

import org.freebus.fts.common.vdx.VdxEntityManager;

import test.entities.TestFunctionalEntity;
import test.entities.TestFunctionalEntityTree;
import test.entities.TestManufacturer;

public class TestVdxEntityManager extends TestCase
{
   public final void testVdxEntityManager() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      assertNotNull(mgr);

      boolean exceptionThrown = false;
      try
      {
         new VdxEntityManager("unknown-file.vd_");
      }
      catch (Exception e)
      {
         exceptionThrown = true;
      }
      assertTrue(exceptionThrown);

      exceptionThrown = false;
      try
      {
         new VdxEntityManager("src/test/resources/test-file.vd_", "unknown-unit");
      }
      catch (Exception e)
      {
         exceptionThrown = true;
      }
      assertTrue(exceptionThrown);

      exceptionThrown = false;
      try
      {
         new VdxEntityManager("src/test/resources/test-file.vd_", "duplicate-entities");
      }
      catch (Exception e)
      {
         exceptionThrown = true;
      }
      assertTrue(exceptionThrown);
   }

   public final void testFindAllSimple() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      assertNotNull(mgr);

      @SuppressWarnings("unchecked")
      List<TestManufacturer> lst = (List<TestManufacturer>) mgr.fetchAll(TestManufacturer.class);
      assertNotNull(lst);
      assertEquals(3, lst.size());

      Object obj = lst.get(0);
      assertNotNull(obj);
      assertTrue(obj instanceof TestManufacturer);
      TestManufacturer manu = (TestManufacturer) obj;
      assertEquals(1, manu.id);
      assertEquals("Siemens", manu.name);

      obj = lst.get(2);
      assertNotNull(obj);
      assertTrue(obj instanceof TestManufacturer);
      manu = (TestManufacturer) obj;
      assertEquals(7, manu.id);
      assertEquals("Busch-Jaeger Elektro", manu.name);
   }

   public final void testFindAllUniDirectionalReference() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      assertNotNull(mgr);

      @SuppressWarnings("unchecked")
      List<TestFunctionalEntity> lst = (List<TestFunctionalEntity>) mgr.fetchAll(TestFunctionalEntity.class);
      assertNotNull(lst);
      assertEquals(2, lst.size());

      Object obj = lst.get(0);
      assertNotNull(obj);
      assertTrue(obj instanceof TestFunctionalEntity);
      TestFunctionalEntity ent = (TestFunctionalEntity) obj;
      assertNotNull(ent.manufacturer);
      assertEquals(2, ent.manufacturer.id);
   }

   public final void testFindAllBiDirectionalReference() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_", "with-children");
      assertNotNull(mgr);

      @SuppressWarnings("unchecked")
      List<TestFunctionalEntityTree> lst = (List<TestFunctionalEntityTree>) mgr.fetchAll(TestFunctionalEntityTree.class);
      assertNotNull(lst);
      assertEquals(2, lst.size());

      Object obj = lst.get(0);
      assertNotNull(obj);
      assertTrue(obj instanceof TestFunctionalEntityTree);
      TestFunctionalEntityTree ent = (TestFunctionalEntityTree) obj;
      assertNotNull(ent.manufacturer);
      assertEquals(2, ent.manufacturer.id);

      Object childObj = lst.get(1);
      assertNotNull(childObj);
      assertTrue(childObj instanceof TestFunctionalEntityTree);
      TestFunctionalEntityTree childEnt = (TestFunctionalEntityTree) childObj;
      assertNotNull(childEnt.manufacturer);
      assertEquals(2, childEnt.manufacturer.id);
      assertEquals(ent, childEnt.parent);
   }
}
