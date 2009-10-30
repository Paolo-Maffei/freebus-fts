package org.freebus.fts.vdx;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.freebus.fts.products.CatalogGroup;
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
   private Map<Integer, CatalogGroup> groups = null;
   private Map<Integer, VirtualDevice> virtualDevices = null;

   /**
    * Create a new VdxProductDb object. Loads the given vd_ file.
    * 
    * @throws IOException
    */
   public VdxProductDb(String fileName) throws IOException
   {
      reader = new VdxFileReader(fileName);
   }

   /**
    * @return the file-name of the loaded vd_ file.
    */
   public String getFileName()
   {
      return reader.getFileName();
   }

   @Override
   public Set<CatalogGroup> getCatalogGroups(ProductFilter filter) throws IOException
   {
      final Set<CatalogGroup> matches = new HashSet<CatalogGroup>();
      if (groups == null) updateCatalogGroups();

      for (final CatalogGroup cat: groups.values())
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
         if (filter.catalogGroups != null &&
             Arrays.binarySearch(filter.catalogGroups, dev.getCatalogGroupId()) < 0)
            continue;

         if (filter.manufacturers != null)
         {
            final CatalogGroup catGroup = groups.get(dev.getCatalogGroupId());
            if (Arrays.binarySearch(filter.manufacturers, catGroup.getManufacturerId()) < 0)
               continue;
         }

         matches.add(dev);
      }

      return matches;
   }

   /**
    * Update the internal list of catalog-groups.
    * @throws IOException 
    */
   protected void updateCatalogGroups() throws IOException
   {
      groups = new HashMap<Integer, CatalogGroup>();

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
         final CatalogGroup cat = new CatalogGroup(groupId, 
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
      final int groupIdIdx = section.getHeader().getIndexOf("functional_entity_id");
      final int nameIdx = section.getHeader().getIndexOf("virtual_device_name");
      final int descIdx = section.getHeader().getIndexOf("virtual_device_description");

      for (int i = section.getNumElements() - 1; i >= 0; --i)
      {
         final VirtualDevice dev = new VirtualDevice(section.getIntValue(i, idIdx),
               section.getValue(i, nameIdx), section.getValue(i, descIdx),
               section.getIntValue(i, groupIdIdx), section.getIntValue(i, catIdIdx));

         virtualDevices.put(dev.getId(), dev);
      }
   }
}
