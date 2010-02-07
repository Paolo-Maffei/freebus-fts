package org.freebus.fts.persistence.vdx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.test_entities.SampleBaggage;
import org.freebus.fts.persistence.test_entities.SampleFunctionalEntity;
import org.freebus.fts.persistence.test_entities.SampleFunctionalEntityTree;
import org.freebus.fts.persistence.test_entities.SampleManufacturer;
import org.junit.Test;


public class TestVdxEntityManager
{
   @Test
   public final void testVdxEntityManager() throws Exception
   {
      assertNotNull(new VdxEntityManager("src/test/resources/test-file.vd_"));
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

   @Test(expected = PersistenceException.class)
   public final void testFetchAllNull() throws PersistenceException
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      mgr.fetchAll(null);
   }

   @Test(expected = PersistenceException.class)
   public final void testFetchAllInvalidClass() throws PersistenceException
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      mgr.fetchAll(Object.class);
   }

   @Test
   public final void testFetchAllSimple() throws Exception
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
   public final void testFetchAllLocalized() throws Exception
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      assertNotNull(mgr);

      @SuppressWarnings("unchecked")
      List<SampleBaggage> lst = (List<SampleBaggage>) mgr.fetchAll(SampleBaggage.class);
      assertNotNull(lst);
      assertEquals(3, lst.size());
   }

   @Test
   public final void testFetchAllUniDirectionalReference() throws Exception
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
   public final void testFetchAllBiDirectionalReference() throws Exception
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

   @Test(expected = PersistenceException.class)
   public final void testFetchNull() throws PersistenceException
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      mgr.fetch(null, null);
   }

   @Test(expected = PersistenceException.class)
   public final void testFetchInvalidClass() throws PersistenceException
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_");
      mgr.fetch(Object.class, null);
   }

   @Test
   public final void testFetchNullCollection() throws PersistenceException
   {
      final VdxEntityManager mgr = new VdxEntityManager("src/test/resources/test-file.vd_", "with-children");

      final SampleFunctionalEntityTree ent = mgr.fetch(SampleFunctionalEntityTree.class, 24378);
      assertNotNull(ent);
      assertEquals(1, ent.childs.size());
   }
}
