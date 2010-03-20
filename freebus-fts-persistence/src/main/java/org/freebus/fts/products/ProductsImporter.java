package org.freebus.fts.products;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Class for importing parts of a org.freebus.fts.products database into FTS' internal database.
 */
public final class ProductsImporter
{
   private final ProductsFactory sourceFactory, destFactory;
   private final CatalogEntryService catalogEntryService;

   private final Set<CatalogEntry> catalogEntries = new HashSet<CatalogEntry>();

   /**
    * Create an importer that will import using sourceFactory and save the
    * objects using destFactory.
    */
   public ProductsImporter(ProductsFactory sourceFactory, ProductsFactory destFactory)
   {
      this.sourceFactory = sourceFactory;

      this.destFactory = destFactory;
      catalogEntryService = destFactory.getCatalogEntryService();
   }

   /**
    * Import a list of virtual devices.
    * @throws PersistenceException
    */
   public void copy(List<VirtualDevice> devices) throws PersistenceException
   {
      final VirtualDeviceService virtDevService = destFactory.getVirtualDeviceService();
      final FunctionalEntityService funcEntService = destFactory.getFunctionalEntityService();

      final boolean ownTransaction = !destFactory.getTransaction().isActive();

      if (ownTransaction)
         destFactory.getTransaction().begin();

      try
      {
         for (VirtualDevice device: devices)
         {
            final CatalogEntry catalogEntry = device.getCatalogEntry();
            if (catalogEntryService.getCatalogEntry(catalogEntry.getId()) == null)
               copy(catalogEntry);

            final FunctionalEntity funcEnt = device.getFunctionalEntity();
            if (funcEntService.getFunctionalEntity(funcEnt.getId()) == null)
               funcEntService.save(funcEnt);

            virtDevService.save(device);
         }

         if (ownTransaction)
            destFactory.getTransaction().commit();
      }
      finally
      {
         if (ownTransaction)
            destFactory.getTransaction().rollback();
      }
   }

   /**
    * Import one virtual device.
    */
   public void copy(VirtualDevice device)
   {
      final List<VirtualDevice> lst = new LinkedList<VirtualDevice>();
      lst.add(device);

      copy(lst);
   }

   /**
    * Copy a catalog entry.
    */
   public void copy(CatalogEntry catalogEntry)
   {
      destFactory.getManufacturerService().save(catalogEntry.getManufacturer());
      catalogEntryService.save(catalogEntry);
      catalogEntries.add(catalogEntry);
   }

   /**
    * @return the org.freebus.fts.products factory from where the org.freebus.fts.products are imported.
    */
   public ProductsFactory getSourceFactory()
   {
      return sourceFactory;
   }

   /**
    * @return the org.freebus.fts.products factory into which the org.freebus.fts.products are imported.
    */
   public ProductsFactory getDestFactory()
   {
      return destFactory;
   }
}
