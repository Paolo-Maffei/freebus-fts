package org.freebus.fts.vdx;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.freebus.fts.Config;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.ProductDb;
import org.freebus.fts.products.ProductFilter;
import org.freebus.fts.products.VirtualDevice;

/**
 * A {@link ProductDb} implementation that works on vd_ files.
 */
public final class VdxProductDb implements ProductDb
{
   private final VdxFileReader reader;
   private Map<Integer, String> manufacturers = null;
   private Map<Integer, FunctionalEntity> groups = null;
   private Map<Integer, VirtualDevice> virtualDevices = null;
   private Map<Integer, String> productDescriptions = null;
   private int languageId = 0;

   /**
    * Create a new VdxProductDb object. Loads the given vd_ file.
    * 
    * @throws IOException
    */
   public VdxProductDb(String fileName) throws IOException
   {
      reader = new VdxFileReader(fileName);
      lookupLanguageId();
   }

   /**
    * @return the file-name of the loaded vd_ file.
    */
   public String getFileName()
   {
      return reader.getFileName();
   }

   @Override
   public Set<FunctionalEntity> getFunctionalEntities(ProductFilter filter) throws IOException
   {
      final Set<FunctionalEntity> matches = new HashSet<FunctionalEntity>();
      if (groups == null) updateCatalogGroups();

      for (final FunctionalEntity cat: groups.values())
      {
         if (filter.manufacturers != null &&
             Arrays.binarySearch(filter.manufacturers, cat.getManufacturerId()) < 0)
            continue;

         matches.add(cat);
      }

      return matches;
   }

   @Override
   public Map<Integer, String> getManufacturers() throws IOException
   {
      if (manufacturers == null)
      {
         manufacturers = new HashMap<Integer, String>();
         final VdxSection section = reader.getSection("manufacturer");
         final int idIdx = section.getHeader().getIndexOf("manufacturer_id");
         final int nameIdx = section.getHeader().getIndexOf("manufacturer_name");

         for (int i = section.getNumElements() - 1; i >= 0; --i)
         {
            final String[] values = section.getElementValues(i);
            manufacturers.put(Integer.parseInt(values[idIdx]), values[nameIdx]);
         }
      }

      return manufacturers;
   }

   @Override
   public Set<VirtualDevice> getVirtualDevices(ProductFilter filter) throws IOException
   {
      final Set<VirtualDevice> matches = new HashSet<VirtualDevice>();
      if (virtualDevices == null) updateVirtualDevices();
      if (groups == null) updateCatalogGroups();

      for (final VirtualDevice dev: virtualDevices.values())
      {
         if (filter.functionalEntities != null &&
             Arrays.binarySearch(filter.functionalEntities, dev.getFunctionalEntityId()) < 0)
            continue;

         if (filter.manufacturers != null)
         {
            final FunctionalEntity catGroup = groups.get(dev.getFunctionalEntityId());
            if (Arrays.binarySearch(filter.manufacturers, catGroup.getManufacturerId()) < 0)
               continue;
         }

         matches.add(dev);
      }

      return matches;
   }

   @Override
   public String getProductDescription(int catalogEntryId) throws IOException
   {
      if (productDescriptions == null) updateProductDescriptions();
      return productDescriptions.get(catalogEntryId);
   }

   @Override
   public VirtualDevice getVirtualDevice(int id) throws IOException
   {
      if (virtualDevices == null) updateVirtualDevices();
      return virtualDevices.get(id);
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
    * Update the internal list of catalog-groups.
    * @throws IOException 
    */
   protected void updateCatalogGroups() throws IOException
   {
      groups = new HashMap<Integer, FunctionalEntity>();

      final VdxSection section = reader.getSection("functional_entity");
      final int groupIdIdx = section.getHeader().getIndexOf("functional_entity_id");
      final int manuIdIdx = section.getHeader().getIndexOf("manufacturer_id");
      final int nameIdx = section.getHeader().getIndexOf("functional_entity_name");
      final int descIdx = section.getHeader().getIndexOf("functional_entity_description");
      final int parentIdIdx = section.getHeader().getIndexOf("fun_functional_entity_id");

      for (int i = section.getNumElements() - 1; i >= 0; --i)
      {
         final String[] values = section.getElementValues(i);

         final int groupId = Integer.parseInt(values[groupIdIdx]);
         final FunctionalEntity cat = new FunctionalEntity(groupId, 
               Integer.parseInt(values[manuIdIdx]), values[nameIdx], values[descIdx]);

         if (parentIdIdx >= 0)
         {
            final String val = values[parentIdIdx];
            final int parentId = val.isEmpty() ? 0 : Integer.parseInt(val);
            if (parentId > 0) cat.setParentId(parentId); 
         }

         groups.put(groupId, cat);
      }
   }

   /**
    * Update the internal list of virtual devices.
    * @throws IOException 
    */
   protected void updateVirtualDevices() throws IOException
   {
      virtualDevices = new HashMap<Integer, VirtualDevice>();

      final VdxSection section = reader.getSection("virtual_device");
      final int idIdx = section.getHeader().getIndexOf("virtual_device_id");
      final int catIdIdx = section.getHeader().getIndexOf("catalog_entry_id");
//      final int progIdIdx = section.getHeader().getIndexOf("program_id");
      final int funIdIdx = section.getHeader().getIndexOf("functional_entity_id");
      final int nameIdx = section.getHeader().getIndexOf("virtual_device_name");
      final int descIdx = section.getHeader().getIndexOf("virtual_device_description");

      for (int i = section.getNumElements() - 1; i >= 0; --i)
      {
         final VirtualDevice dev = new VirtualDevice(section.getIntValue(i, idIdx),
               section.getValue(i, nameIdx), section.getValue(i, descIdx),
               section.getIntValue(i, funIdIdx), section.getIntValue(i, catIdIdx));

         virtualDevices.put(dev.getId(), dev);
      }
   }

   /**
    * Update the internal list of product descriptions.
    * @throws IOException 
    */
   protected void updateProductDescriptions() throws IOException
   {
      productDescriptions = new HashMap<Integer, String>();

      final VdxSection section = reader.getSection("product_description");
      final int idIdx = section.getHeader().getIndexOf("catalog_entry_id");
      final int textIdx = section.getHeader().getIndexOf("product_description_text");
      final int orderIdx = section.getHeader().getIndexOf("display_order");
      final int langIdIdx = section.getHeader().getIndexOf("language_id");
      int id, order, langId;
      String val;

      // Temporary map with all lines. Key is: catalog_entry_id*256 + display_order
      final Map<Integer,String> allLines = new TreeMap<Integer,String>();

      for (int i = section.getNumElements() - 1; i >= 0; --i)
      {
         langId = section.getIntValue(i, langIdIdx);
         if (langId != languageId) continue;

         id = section.getIntValue(i, idIdx);
         order = section.getIntValue(i, orderIdx);
         allLines.put((id << 8) + order, section.getValue(i, textIdx));
      }

      // Combine the lines of the product descriptions
      for (final int key: allLines.keySet())
      {
         id = key >> 8;

         val = productDescriptions.get(id);
         if (val == null) val = allLines.get(key);
         else val += '\n' + allLines.get(key);

         productDescriptions.put(id, val);
      }
   }
}
