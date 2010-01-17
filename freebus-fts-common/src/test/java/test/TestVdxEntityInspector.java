package test;

import java.util.Set;

import junit.framework.TestCase;

import org.freebus.fts.common.vdx.internal.VdxEntityInfo;
import org.freebus.fts.common.vdx.internal.VdxEntityInspector;

import test.entities.TestFunctionalEntity;
import test.entities.TestManufacturer;
import test.entities.TestManufacturer2;

public class TestVdxEntityInspector extends TestCase
{
   final public void testVdxEntityInspector()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector();
      assertNotNull(inspector);
   }

   final public void testVdxEntityInspectorString()
   {
      final VdxEntityInspector inspector = new VdxEntityInspector("default");
      assertNotNull(inspector);
   }

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
}
