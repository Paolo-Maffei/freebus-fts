package org.freebus.fts.products.importer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Class for importing parts of a products database into FTS'
 * internal database. The IDs are copied as is.
 * 
 * @see RemappingProductsImporter
 */
public final class DirectProductsImporter implements ProductsImporter
{
   private final ProductsFactory sourceFactory, destFactory;
   private final CatalogEntryService catalogEntryService;

   private final Set<CatalogEntry> catalogEntries = new HashSet<CatalogEntry>();

   /**
    * Create an importer that will import using sourceFactory and save the
    * objects using destFactory.
    */
   public DirectProductsImporter(ProductsFactory sourceFactory, ProductsFactory destFactory)
   {
      this.sourceFactory = sourceFactory;

      this.destFactory = destFactory;
      catalogEntryService = destFactory.getCatalogEntryService();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void copy(List<VirtualDevice> devices)
   {
      final VirtualDeviceService virtDevService = destFactory.getVirtualDeviceService();
      final FunctionalEntityService funcEntService = destFactory.getFunctionalEntityService();

      final boolean ownTransaction = !destFactory.getTransaction().isActive();

      if (ownTransaction)
         destFactory.getTransaction().begin();

      // fixParameters(devices);

      for (VirtualDevice device : devices)
      {
         device.setId(0);

         final CatalogEntry catalogEntry = device.getCatalogEntry();
         if (catalogEntryService.getCatalogEntry(catalogEntry.getId()) == null)
            copy(catalogEntry);

         for (FunctionalEntity funcEnt = device.getFunctionalEntity(); funcEnt != null; funcEnt = funcEnt.getParent())
         {
            if (funcEntService.getFunctionalEntity(funcEnt.getId()) == null)
               funcEntService.persist(funcEnt);
         }

         virtDevService.merge(device);
      }

      if (ownTransaction)
         destFactory.getTransaction().commit();
   }

   /**
    * Copy a catalog entry.
    */
   public void copy(CatalogEntry catalogEntry)
   {
      destFactory.getManufacturerService().saveIfMissing(catalogEntry.getManufacturer());

      catalogEntryService.persist(catalogEntry);
      catalogEntries.add(catalogEntry);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProductsFactory getSourceFactory()
   {
      return sourceFactory;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ProductsFactory getDestFactory()
   {
      return destFactory;
   }
}
