package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxEntityManager;
import org.junit.Test;

import test.entities.SampleFunctionalEntity;
import test.entities.SampleFunctionalEntityTree;
import test.entities.SampleManufacturer;

public class TestVdxEntityManager
{
   @Test
   public final void testVdxEntityManager() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      assertNotNull(mgr);
   }
   
   @Test(expected = PersistenceException.class)
   public final void testVdxEntityManagerUnknownFile() throws Exception
   {
      new VdxEntityManager("unknown-file.vd_");
   }

   
   @Test(expected = PersistenceException.class)
   public final void testVdxEntityManagerUnknownUnit() throws Exception
   {
      new VdxEntityManager("src/test/resources/test-file.vd_", "unknown-unit");
   }

   
   @Test(expected = PersistenceException.class)
   public final void testVdxEntityManagerDuplicateEntities() throws Exception
   {
      new VdxEntityManager("src/test/resources/test-file.vd_", "duplicate-entities");
   }

   @Test
   public final void testFindAllSimple() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      assertNotNull(mgr);

      @SuppressWarnings("unchecked")
      List<SampleManufacturer> lst = (List<SampleManufacturer>) mgr.fetchAll(SampleManufacturer.class);
      assertNotNull(lst);
      assertEquals(3, lst.size());

      Object obj = lst.get(0);
      assertNotNull(obj);
      assertTrue(obj instanceof SampleManufacturer);
      SampleManufacturer manu = (SampleManufacturer) obj;
      assertEquals(1, manu.id);
      assertEquals("Siemens", manu.name);

      obj = lst.get(2);
      assertNotNull(obj);
      assertTrue(obj instanceof SampleManufacturer);
      manu = (SampleManufacturer) obj;
      assertEquals(7, manu.id);
      assertEquals("Busch-Jaeger Elektro", manu.name);
   }

   @Test
   public final void testFindAllUniDirectionalReference() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      assertNotNull(mgr);

      @SuppressWarnings("unchecked")
      List<SampleFunctionalEntity> lst = (List<SampleFunctionalEntity>) mgr.fetchAll(SampleFunctionalEntity.class);
      assertNotNull(lst);
      assertEquals(2, lst.size());

      Object obj = lst.get(0);
      assertNotNull(obj);
      assertTrue(obj instanceof SampleFunctionalEntity);
      SampleFunctionalEntity ent = (SampleFunctionalEntity) obj;
      assertNotNull(ent.manufacturer);
      assertEquals(2, ent.manufacturer.id);
   }

   @Test
   public final void testFindAllBiDirectionalReference() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_", "with-children");
      assertNotNull(mgr);

      @SuppressWarnings("unchecked")
      List<SampleFunctionalEntityTree> lst = (List<SampleFunctionalEntityTree>) mgr.fetchAll(SampleFunctionalEntityTree.class);
      assertNotNull(lst);
      assertEquals(2, lst.size());

      Object obj = lst.get(0);
      assertNotNull(obj);
      assertTrue(obj instanceof SampleFunctionalEntityTree);
      SampleFunctionalEntityTree ent = (SampleFunctionalEntityTree) obj;
      assertNotNull(ent.manufacturer);
      assertEquals(2, ent.manufacturer.id);

      Object childObj = lst.get(1);
      assertNotNull(childObj);
      assertTrue(childObj instanceof SampleFunctionalEntityTree);
      SampleFunctionalEntityTree childEnt = (SampleFunctionalEntityTree) childObj;
      assertNotNull(childEnt.manufacturer);
      assertEquals(2, childEnt.manufacturer.id);
      assertEquals(ent, childEnt.parent);

      final Set<SampleFunctionalEntityTree> childs = ent.childs;
      assertNotNull(childs);
      assertEquals(1, childs.size());
      assertEquals(childObj, childs.iterator().next());
   }
}
