package org.freebus.fts.products;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.ProductsFactory;

/**
 * Class for importing parts of a products database into FTS' internal
 * (products) database.
 */
public final class ProductsImporter
{
   private final ProductsFactory sourceFactory, destFactory;
   private final CatalogEntryService destCatalogEntryService;

   private final Set<CatalogEntry> catalogEntries = new HashSet<CatalogEntry>();

   /**
    * Create an importer that will import using sourceFactory and save the
    * objects using destFactory.
    */
   public ProductsImporter(ProductsFactory sourceFactory, ProductsFactory destFactory)
   {
      this.sourceFactory = sourceFactory;

      this.destFactory = destFactory;
      destCatalogEntryService = destFactory.getCatalogEntryService();
   }

   /**
    * Import a list of virtual devices.
    * @throws PersistenceException 
    */
   public void copy(List<VirtualDevice> devices) throws PersistenceException
   {
      for (VirtualDevice device: devices)
      {
         final CatalogEntry catalogEntry = device.getCatalogEntry();
         if (!catalogEntries.contains(catalogEntry))
            copy(catalogEntry);
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
      destCatalogEntryService.save(catalogEntry);
      catalogEntries.add(catalogEntry);
   }

   /**
    * @return the products factory from where the products are imported.
    */
   public ProductsFactory getSourceFactory()
   {
      return sourceFactory;
   }

   /**
    * @return the products factory into which the products are imported.
    */
   public ProductsFactory getDestFactory()
   {
      return destFactory;
   }
}
