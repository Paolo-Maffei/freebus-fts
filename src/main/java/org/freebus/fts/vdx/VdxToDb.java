package org.freebus.fts.vdx;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EntityManager;

import org.freebus.fts.Config;
import org.freebus.fts.db.DatabaseResources;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Product;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ListenableWorker;

/**
 * Copy the contents of a VDX (vd_) file into the products database.
 */
public final class VdxToDb extends ListenableWorker
{
   private final String fileName;
   private VdxFileReader reader;
   private final EntityManager em = DatabaseResources.getEntityManager();
   private int stepOffset;
   private int languageId;

   /**
    * Create a new object that copies the vd_ file fileName.
    */
   public VdxToDb(String fileName)
   {
      this.fileName = fileName;
   }

   /**
    * Copy the vd_ file fileName into the products database.
    * @throws IOException 
    */
   public void run() throws IOException
   {
      final int stepGroups = 7; // number of "copyXY" methods that get called below +2
      final int totalSteps = stepGroups * 10;
      setTotalSteps(totalSteps);

      stepOffset = 0;

      progress(0, I18n.getMessage("VdxToDb_Preparing"));
      reader = new VdxFileReader(fileName);
      lookupLanguageId();
      stepOffset += 10;

      em.getTransaction().begin();

      copyManufacturers();
      stepOffset += 10;

      em.getTransaction().commit();
      em.getTransaction().begin();

      copyFunctionalEntities();
      stepOffset += 10;

      em.getTransaction().commit();
      em.getTransaction().begin();

      copyVirtualDevices();
      stepOffset += 10;

      em.getTransaction().commit();
      em.getTransaction().begin();

      copyCatalogEntries();
      stepOffset += 10;

      em.getTransaction().commit();
      em.getTransaction().begin();

      copyProducts();
      stepOffset += 10;

      em.getTransaction().commit();
      progress(totalSteps, I18n.getMessage("VdxToDb_Done"));
   }

   /**
    * Read a section.
    * 
    * @param name - the name of the section.
    * @throws IOException - if the section is not found.
    */
   protected VdxSection getSection(String name) throws IOException
   {
      final VdxSection section = reader.getSection(name);
      if (section == null) throw new IOException(I18n.getMessage("VdxToDb_SectionNotFound").replace("%1", name));
      return section;
   }

   /**
    * Lookup the id of the preferred language.
    * @throws IOException 
    */
   protected void lookupLanguageId() throws IOException
   {
      final String cfgLang = Config.getInstance().getLanguage(); 
      languageId = 0;

      final VdxSection section = reader.getSection("ete_language");
      for (int i = section.getNumElements() - 1; i >= 0; --i)
      {
         final String lang = section.getValue(i, "language_name");
         final int langId = section.getIntValue(i, "language_id");
         if (cfgLang.equals(lang))
         {
            languageId = langId;
            return;
         }
         if (cfgLang.equals("English"))
         {
            // Remember English as fallback, in case that the configured language is not found.
            languageId = langId;
         }
      }
   }

   /**
    * Load the VDX section sectionName. For each record, creates an object of
    * the type entryClass.
    * 
    * @param sectionName - the name of the VDX section to process.
    * @param entryClass - the class for the entries.
    * @return the list of created entries.
    * @throws IOException
    */
   protected Object[] getVdxEntries(String sectionName, Class<?> entryClass) throws IOException
   {
      final VdxSection section = getSection(sectionName);
      final VdxSectionHeader header = section.getHeader();
      final int num = section.getNumElements();
      final Map<Integer,Field> fieldMappings = new HashMap<Integer,Field>();
      String fieldName;

      for (final Field field: entryClass.getDeclaredFields())
      {
         Annotation a = field.getAnnotation(VdxField.class);
         if (a != null)
         {
            fieldName = ((VdxField) a).name();
         }
         else
         {
            a = field.getAnnotation(Column.class);
            if (a == null) continue;
            fieldName = ((Column) a).name();
         }

         int fieldIdx = header.getIndexOf(fieldName);
         field.setAccessible(true);
         if (fieldIdx >= 0) fieldMappings.put(fieldIdx, field);
      }

      final Object[] objs = new Object[num];
      final Set<Integer> fieldIdxs = fieldMappings.keySet();

      try
      {
         for (int i = 0; i < num; ++i)
         {
            final Object obj = entryClass.newInstance();
            final String values[] = section.getElementValues(i);
   
            for (int fieldIdx: fieldIdxs)
            {
               final Field field = fieldMappings.get(fieldIdx);
               final Type type = field.getGenericType();
               String val = values[fieldIdx];

               if (type == String.class) field.set(obj, val.trim());
               else if (type == int.class)
               {
                  int pos = val.indexOf('.');
                  if (pos >= 0) val = val.substring(0, pos);
                  field.setInt(obj, val.isEmpty() ? 0 : Integer.parseInt(val));                  
               }
               else if (type == boolean.class) field.setBoolean(obj, val.isEmpty() ? false : Integer.parseInt(val) != 0);
               else throw new Exception("Variable type not supported by mapper: " + type.toString());
            }

            objs[i] = obj;
         }
      }
      catch (Exception e)
      {
         throw new IOException(e);
      }

      return objs;
   }

   /**
    * Copy the manufacturers.
    * 
    * @throws IOException
    */
   protected void copyManufacturers() throws IOException
   {
      final String name = "manufacturer";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, Manufacturer.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }

   /**
    * Copy the functional entities.
    * 
    * @throws IOException
    */
   protected void copyFunctionalEntities() throws IOException
   {
      final String name = "functional_entity";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, FunctionalEntity.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }

   /**
    * Copy the virtual devices.
    * 
    * @throws IOException
    */
   protected void copyVirtualDevices() throws IOException
   {
      final String name = "virtual_device";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, VirtualDevice.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }

   /**
    * Copy the catalog entries and product descriptions.
    * 
    * @throws IOException
    */
   protected void copyCatalogEntries() throws IOException
   {
      final String name = "catalog_entry";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, CatalogEntry.class);

      final VdxSection section = getSection("product_description");
      final VdxSectionHeader header = section.getHeader();
      final int idIdx = header.getIndexOf("catalog_entry_id");
      final int textIdx = header.getIndexOf("product_description_text");
      final int langIdIdx = header.getIndexOf("language_id");
      final int num = section.getNumElements();
      final Map<Integer, String> descs = new HashMap<Integer, String>();

      for (int i = 0; i < num; ++i)
      {
         if (section.getIntValue(i, langIdIdx) != languageId) continue;
         final int id = section.getIntValue(i, idIdx); 

         String val = descs.get(id);
         if (val == null) val = section.getValue(i, textIdx);
         else val = val + '\n' + section.getValue(i, textIdx);

         descs.put(id, val);
      }

      progress(stepOffset + 5, null);
      for (Object obj: objs)
      {
         final CatalogEntry ent = (CatalogEntry) obj;
         final String desc = descs.get(ent.getId());
//         if (desc != null && !desc.isEmpty()) ent.setDescription(desc);

         em.merge(ent);
      }

      System.out.printf("Processed %d %s\n", objs.length, name);
   }

   /**
    * Copy the hardware products.
    * 
    * @throws IOException
    */
   protected void copyProducts() throws IOException
   {
      final String name = "hw_product";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, Product.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }
}
