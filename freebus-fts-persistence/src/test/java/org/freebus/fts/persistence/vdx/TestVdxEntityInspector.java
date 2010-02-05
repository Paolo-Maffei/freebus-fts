package org.freebus.fts.persistence.vdx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.Vector;

import org.freebus.fts.persistence.test_entities.SampleFunctionalEntity;
import org.freebus.fts.persistence.test_entities.SampleFunctionalEntityTree;
import org.freebus.fts.persistence.test_entities.SampleManufacturer;
import org.freebus.fts.persistence.vdx.internal.VdxAssociation;
import org.freebus.fts.persistence.vdx.internal.VdxEntityInfo;
import org.freebus.fts.persistence.vdx.internal.VdxEntityInspector;
import org.junit.Test;


public class TestVdxEntityInspector
{
   @Test
   final public void testVdxEntityInspector()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector();
      assertNotNull(inspector);
   }

   @Test
   final public void testVdxEntityInspectorString()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector("default");
      assertNotNull(inspector);
   }

   @Test
   final public void testGetClasses()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector("default");
      assertNotNull(inspector);

      Set<Class<?>> classes = inspector.getClasses();
      assertNotNull(classes);

      assertTrue(classes.contains(SampleFunctionalEntity.class));
      assertTrue(classes.contains(SampleManufacturer.class));
      assertFalse(classes.contains(Object.class));
   }

   @Test
   final public void testGetInfo()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector("default");
      assertNotNull(inspector);

      VdxEntityInfo info = inspector.getInfo(SampleManufacturer.class);
      assertNotNull(info);
      assertTrue(info.isInspected());
      assertEquals("manufacturer", info.getName());

      info = inspector.getInfo(SampleFunctionalEntity.class);
      assertNotNull(info);
      assertTrue(info.isInspected());
      assertEquals("functional_entity", info.getName());
   }

   @Test
   final public void testGetInfoOneToMany()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector("with-children");
      assertNotNull(inspector);

      VdxEntityInfo info = inspector.getInfo(SampleFunctionalEntityTree.class);
      assertNotNull(info);
      assertTrue(info.isInspected());
      assertEquals("functional_entity", info.getName());

      final Vector<VdxAssociation> assocs = info.getAssociations();
      assertEquals(3, assocs.size());

      // Expected member:  Manufacturer manufacturer
      VdxAssociation assoc = assocs.get(0);
      assertEquals("manufacturer", assoc.getField().getName());
      assertEquals("manufacturer_id", assoc.getVdxFieldName());
      assertEquals(SampleManufacturer.class, assoc.getTargetClass());

      // Expected member:  TestFunctionalEntityTree parent
      assoc = assocs.get(1);
      assertEquals("parent", assoc.getField().getName());
      assertEquals("fun_functional_entity_id", assoc.getVdxFieldName());
      assertEquals(SampleFunctionalEntityTree.class, assoc.getTargetClass());

      // Expected member:  Set<TestFunctionalEntityTree> childs
      assoc = assocs.get(2);
      assertEquals("childs", assoc.getField().getName());
      assertEquals("functional_entity_id", assoc.getVdxFieldName());
      assertEquals(SampleFunctionalEntityTree.class, assoc.getTargetClass());
      assertNotNull(assoc.getTargetField());
      assertEquals("parent", assoc.getTargetField().getName());
   }
}
