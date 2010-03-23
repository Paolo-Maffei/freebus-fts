package org.freebus.fts.persistence.vdx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.test_entities.SampleBaggage;
import org.freebus.fts.persistence.test_entities.SampleFunctionalEntity;
import org.freebus.fts.persistence.test_entities.SampleManufacturer;
import org.freebus.fts.test_utils.PersistenceTestCase;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestVdxNewEntityManager extends PersistenceTestCase
{
   public TestVdxNewEntityManager()
   {
      super("test-entities");
   }

   protected VdxNewEntityManager createVdxEntityManager(String fileName)
   {
      final EntityManagerFactory emf = DatabaseResources.getEntityManagerFactory();
      final EntityManager em = emf.createEntityManager();
      final VdxNewEntityManager vem = new VdxNewEntityManager(new File(fileName), em);
      return vem;
   }

   @Test
   public final void testVdxNewEntityManager()
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");
      assertNotNull(vem);

      assertNotNull(vem.getReader());
      assertNotNull(vem.getEntityManager());
   }

   @Test
   public final void testCreateFieldMap()
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");
      final Class<?> entityClass = SampleManufacturer.class;

      final EntityType<?> entityType = vem.getEntityManager().getMetamodel().entity(entityClass);
      assertNotNull(entityType);

      final VdxSectionHeader tableHeader = vem.getReader().getSectionHeader(vem.vdxTableNameOf(entityClass));
      assertNotNull(tableHeader);

      final List<Attribute<?, ?>> fieldMap = vem.createFieldMap(entityType, tableHeader);
      assertNotNull(fieldMap);

      assertEquals(tableHeader.fields.length, fieldMap.size());

      Attribute<?, ?> attr;

      attr = fieldMap.get(0);
      assertNotNull(attr);
      assertEquals("id", attr.getName());

      attr = fieldMap.get(1);
      assertNotNull(attr);
      assertEquals("name", attr.getName());
   }

   @Test
   public final void testMaterialize() throws IOException
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");

      final Class<?> entityClass = SampleManufacturer.class;
      final VdxSection table = vem.getReader().getSection(vem.vdxTableNameOf(entityClass));
      final EntityType<?> entityType = vem.getEntityManager().getMetamodel().entity(entityClass);

      final List<Attribute<?, ?>> fieldMap = vem.createFieldMap(entityType, table.getHeader());
      assertNotNull(fieldMap);

      final Object obj = vem.materialize(entityType, fieldMap, table, 0);
      assertNotNull(obj);
      assertTrue(obj instanceof SampleManufacturer);

      assertEquals(1, ((SampleManufacturer) obj).id);
      assertEquals("Siemens", ((SampleManufacturer) obj).name);
   }

   @Test
   public final void testNewEntity() throws PersistenceException
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");
      final Class<?> entityClass = SampleManufacturer.class;
      final EntityType<?> entityType = vem.getEntityManager().getMetamodel().entity(entityClass);

      assertNotNull(vem.newEntity(entityType));
   }

   @Test
   public final void testLoadEntitiesSimple()
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");
      HashMap<?, ?> entities = vem.loadEntities(SampleManufacturer.class);
      assertNotNull(entities);

      Object obj, key;
      SampleManufacturer manu;

      key = Integer.valueOf(1);
      assertTrue(entities.containsKey(key));
      obj = entities.get(key);
      assertTrue(obj instanceof SampleManufacturer);
      manu = (SampleManufacturer) obj;
      assertEquals(key, manu.id);
      assertEquals("Siemens", manu.name);

      assertTrue(entities.containsKey(Integer.valueOf(2)));
      assertTrue(entities.containsKey(Integer.valueOf(7)));
   }

   @Test
   public final void testLoadEntitiesManyToOne()
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");
      HashMap<?, ?> entities = vem.loadEntities(SampleFunctionalEntity.class);
      assertNotNull(entities);

      Object obj, key;
      SampleFunctionalEntity funcEnt;

      key = Integer.valueOf(24378);
      assertTrue(entities.containsKey(key));
      obj = entities.get(key);
      assertTrue(obj instanceof SampleManufacturer);
      funcEnt = (SampleFunctionalEntity) obj;
      assertEquals(key, funcEnt.id);
      assertEquals("Beleuchtung", funcEnt.name);

      assertTrue(entities.containsKey(Integer.valueOf(24379)));
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testLoadEntitiesNoEntityClass()
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");
      vem.loadEntities(Object.class);
   }

   @Test
   public final void testVdxTableNameOf()
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");
      assertEquals("manufacturer", vem.vdxTableNameOf(SampleManufacturer.class));
      assertEquals("functional_entity", vem.vdxTableNameOf(SampleFunctionalEntity.class));
      assertEquals("ApplicationProgramBaggage", vem.vdxTableNameOf(SampleBaggage.class));
   }

   @Test(expected = IllegalArgumentException.class)
   public final void testVdxTableNameOfNoEntity()
   {
      final VdxNewEntityManager vem = createVdxEntityManager("src/test/resources/test-file.vd_");
      vem.vdxTableNameOf(Object.class);
   }
}
