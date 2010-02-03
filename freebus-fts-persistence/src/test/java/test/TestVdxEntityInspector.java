package test;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.Vector;

import org.freebus.fts.persistence.vdx.internal.VdxAssociation;
import org.freebus.fts.persistence.vdx.internal.VdxEntityInfo;
import org.freebus.fts.persistence.vdx.internal.VdxEntityInspector;
import org.junit.Test;

import test.entities.TestFunctionalEntity;
import test.entities.TestFunctionalEntityTree;
import test.entities.TestManufacturer;
import test.entities.TestManufacturer2;

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

      assertTrue(classes.contains(TestFunctionalEntity.class));
      assertTrue(classes.contains(TestManufacturer.class));
      assertFalse(classes.contains(TestManufacturer2.class));
   }

   @Test
   final public void testGetInfo()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector("default");
      assertNotNull(inspector);

      VdxEntityInfo info = inspector.getInfo(TestManufacturer.class);
      assertNotNull(info);
      assertTrue(info.isInspected());
      assertEquals("manufacturer", info.getName());

      info = inspector.getInfo(TestFunctionalEntity.class);
      assertNotNull(info);
      assertTrue(info.isInspected());
      assertEquals("functional_entity", info.getName());
   }

   @Test
   final public void testGetInfoOneToMany()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector("with-children");
      assertNotNull(inspector);

      VdxEntityInfo info = inspector.getInfo(TestFunctionalEntityTree.class);
      assertNotNull(info);
      assertTrue(info.isInspected());
      assertEquals("functional_entity", info.getName());

      final Vector<VdxAssociation> assocs = info.getAssociations();
      assertEquals(3, assocs.size());

      // Expected member:  Manufacturer manufacturer
      VdxAssociation assoc = assocs.get(0);
      assertEquals("manufacturer", assoc.getField().getName());
      assertEquals("manufacturer_id", assoc.getVdxFieldName());
      assertEquals(TestManufacturer.class, assoc.getTargetClass());

      // Expected member:  TestFunctionalEntityTree parent
      assoc = assocs.get(1);
      assertEquals("parent", assoc.getField().getName());
      assertEquals("fun_functional_entity_id", assoc.getVdxFieldName());
      assertEquals(TestFunctionalEntityTree.class, assoc.getTargetClass());

      // Expected member:  Set<TestFunctionalEntityTree> childs
      assoc = assocs.get(2);
      assertEquals("childs", assoc.getField().getName());
      assertEquals("functional_entity_id", assoc.getVdxFieldName());
      assertEquals(TestFunctionalEntityTree.class, assoc.getTargetClass());
      assertNotNull(assoc.getTargetField());
      assertEquals("parent", assoc.getTargetField().getName());
   }
}
