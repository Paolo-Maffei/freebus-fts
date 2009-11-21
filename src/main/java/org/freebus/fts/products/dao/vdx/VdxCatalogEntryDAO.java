package org.freebus.fts.products.dao.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.dao.CatalogEntryDAO;
import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.DAONotFoundException;
import org.freebus.fts.products.dao.VirtualDeviceDAO;
import org.freebus.fts.vdx.VdxFileReader;

/**
 * Data access object for catalog-entries stored in a VDX file.
 */
public final class VdxCatalogEntryDAO implements CatalogEntryDAO
{
   private final VdxFileReader reader;
   private final VirtualDeviceDAO virtualDeviceDAO;
   private List<CatalogEntry> entries;
   private Map<Integer, CatalogEntry> entriesById;

   VdxCatalogEntryDAO(VdxFileReader reader, VirtualDeviceDAO virtualDeviceDAO)
   {
      this.reader = reader;
      this.virtualDeviceDAO = virtualDeviceDAO;
   }

   private synchronized void fetchData() throws DAOException
   {
      if (entries != null) return;

      try
      {
         final Object[] arr = reader.getSectionEntries("catalog_entry", CatalogEntry.class);
         Arrays.sort(arr, new Comparator<Object>()
         {
            @Override
            public int compare(Object a, Object b)
            {
               return ((CatalogEntry) a).getName().compareTo(((CatalogEntry) b).getName());
            }
         });

         entries = new ArrayList<CatalogEntry>(arr.length);
         entriesById = new HashMap<Integer, CatalogEntry>((arr.length << 1) + 31);

         for (Object obj : arr)
         {
            final CatalogEntry entry = (CatalogEntry) obj;

            entries.add(entry);
            entriesById.put(entry.getId(), entry);
         }
      }
      catch (IOException e)
      {
         throw new DAOException(e);
      }
   }

   @Override
   public List<CatalogEntry> getCatalogEntries() throws DAOException
   {
      if (entries == null) fetchData();
      return entries;
   }

   @Override
   public List<CatalogEntry> getCatalogEntries(Manufacturer m, FunctionalEntity[] functionalEntities)
         throws DAOException
   {
      if (entries == null) fetchData();

      final List<VirtualDevice> devices = virtualDeviceDAO.getVirtualDevices(functionalEntities);
      final Set<Integer> ids = new HashSet<Integer>();
      for (VirtualDevice device: devices)
         ids.add(device.getCatalogEntryId());

      final List<CatalogEntry> result = new LinkedList<CatalogEntry>();
      final int manufacturerId = m.getId();

      for (CatalogEntry entry: entries)
      {
         if (entry.getManufacturerId() == manufacturerId && ids.contains(entry.getId()))
            result.add(entry);
      }

      return result;
   }

   @Override
   public CatalogEntry getCatalogEntry(int id) throws DAOException
   {
      if (entries == null) fetchData();

      final CatalogEntry entry = entriesById.get(id);
      if (entry == null) throw new DAONotFoundException("Object not found, id=" + Integer.toString(id));
      return entry;
   }
}
