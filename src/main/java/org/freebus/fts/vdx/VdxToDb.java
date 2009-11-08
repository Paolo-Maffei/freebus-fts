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
import javax.persistence.EntityTransaction;

import org.freebus.fts.Config;
import org.freebus.fts.db.DatabaseResources;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.ParameterType;
import org.freebus.fts.products.ParameterValue;
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
      final int stepGroups = 12; // number of "copyXY" methods that get called below +2
      final int totalSteps = stepGroups * 10;
      final EntityTransaction transaction = em.getTransaction();
      setTotalSteps(totalSteps);

      stepOffset = 0;

      progress(0, I18n.getMessage("VdxToDb_Preparing"));
      reader = new VdxFileReader(fileName);
      lookupLanguageId();
      stepOffset += 10;

      transaction.begin();

      copyManufacturers();
      stepOffset += 10;

      copyFunctionalEntities();
      stepOffset += 10;

      transaction.commit();
      transaction.begin();

      copyVirtualDevices();
      stepOffset += 10;

      transaction.commit();
      transaction.begin();

      copyCatalogEntries();
      stepOffset += 10;

      transaction.commit();
      transaction.begin();

      copyProducts();
      stepOffset += 10;

      copyParameterAtomicTypes();
      stepOffset += 10;

      transaction.commit();
      transaction.begin();

      copyParameterTypes();
      stepOffset += 10;

      transaction.commit();
      transaction.begin();

      copyParameterValues();
      stepOffset += 10;

      transaction.commit();
      transaction.begin();

      copyParameters();
      stepOffset += 10;

      transaction.commit();
      transaction.begin();

      copyCommunicationObjects();
      stepOffset += 10;

      progress(totalSteps-2, I18n.getMessage("VdxToDb_Commit"));
      transaction.commit();

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
               else if (type == double.class) field.setDouble(obj, val.isEmpty() ? 0.0 : Double.parseDouble(val));
               else if (type == boolean.class) field.setBoolean(obj, val.isEmpty() ? false : Integer.parseInt(val) != 0);
               else throw new Exception("Variable type not supported by vdx-to-db mapper: " + type.toString());
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
    * Copy the catalog entries.
    * 
    * @throws IOException
    */
   protected void copyCatalogEntries() throws IOException
   {
      final String name = "catalog_entry";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, CatalogEntry.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

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

   /**
    * Copy the parameter atomic types.
    * 
    * @throws IOException
    */
   protected void copyParameterAtomicTypes() throws IOException
   {
      final String name = "parameter_atomic_type";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, ParameterAtomicType.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }

   /**
    * Copy the parameter types.
    * 
    * @throws IOException
    */
   protected void copyParameterTypes() throws IOException
   {
      final String name = "parameter_type";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, ParameterType.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }

   /**
    * Copy the parameter type values.
    * 
    * @throws IOException
    */
   protected void copyParameterValues() throws IOException
   {
      final String name = "parameter_list_of_values";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, ParameterValue.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }

   /**
    * Copy the parameters.
    * 
    * @throws IOException
    */
   protected void copyParameters() throws IOException
   {
      final String name = "parameter";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, Parameter.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }

   /**
    * Copy the communication objects.
    * 
    * @throws IOException
    */
   protected void copyCommunicationObjects() throws IOException
   {
      final String name = "communication_object";
      progress(stepOffset, I18n.getMessage("VdxToDb_Processing").replace("%1", name));
      final Object objs[] = getVdxEntries(name, CommunicationObject.class);

      progress(stepOffset + 5, null);
      for (Object obj: objs)
         em.merge(obj);

      System.out.printf("Processed %d %s\n", objs.length, name);
   }
}
