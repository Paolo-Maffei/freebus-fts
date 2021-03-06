package org.freebus.fts.products.services.vdx;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxEntityManager;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Data access object for catalog-entries stored in a VD_ file.
 */
public final class VdxCatalogEntryService implements CatalogEntryService
{
   private final VdxEntityManager manager;
   private final VirtualDeviceService virtualDeviceService;

   VdxCatalogEntryService(VdxEntityManager manager, VirtualDeviceService virtualDeviceService)
   {
      this.manager = manager;
      this.virtualDeviceService = virtualDeviceService;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<CatalogEntry> getCatalogEntries() throws PersistenceException
   {
      return (List<CatalogEntry>) manager.fetchAll(CatalogEntry.class);
   }

   @Override
   public List<CatalogEntry> getCatalogEntries(Manufacturer m, FunctionalEntity[] functionalEntities) throws PersistenceException
   {
      @SuppressWarnings("unchecked")
      List<CatalogEntry> entries = (List<CatalogEntry>) manager.fetchAll(CatalogEntry.class);

      final List<VirtualDevice> devices = virtualDeviceService.getVirtualDevices(functionalEntities);
      final Set<CatalogEntry> ents = new HashSet<CatalogEntry>();
      for (VirtualDevice device: devices)
         ents.add(device.getCatalogEntry());

      final List<CatalogEntry> result = new LinkedList<CatalogEntry>();

      for (CatalogEntry entry: entries)
      {
         if (entry.getManufacturer() == m && ents.contains(entry))
            result.add(entry);
      }

      return result;
   }

   @Override
   public CatalogEntry getCatalogEntry(int id) throws PersistenceException
   {
      return manager.fetch(CatalogEntry.class, id);
   }

   @Override
   public void persist(CatalogEntry catalogEntry) throws PersistenceException
   {
      throw new PersistenceException("Sorry not implemented");
   }

   @Override
   public CatalogEntry merge(CatalogEntry catalogEntry) throws PersistenceException
   {
      throw new PersistenceException("Sorry not implemented");
   }
}
