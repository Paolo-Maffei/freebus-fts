package org.freebus.fts.products.importer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.freebus.fts.products.BcuType;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Mask;
import org.freebus.fts.products.Product;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.BcuTypeService;
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
    * 
    * @param sourceFactory - the source factory.
    * @param destFactory - the destination factory.
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

      copyBcuTypes(devices);
      // fixParameters(devices);

      for (VirtualDevice device : devices)
      {
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
    * 
    * @param catalogEntry - the catalog entry to copy.
    */
   public void copy(CatalogEntry catalogEntry)
   {
      destFactory.getManufacturerService().saveIfMissing(catalogEntry.getManufacturer());

      catalogEntryService.persist(catalogEntry);
      catalogEntries.add(catalogEntry);
   }

   /**
    * Import all BCU types of the {@link Product hardware products} and
    * {@link Program application programs} of the virtual devices.
    * 
    * @param devices - the virtual devices to process.
    */
   public void copyBcuTypes(List<VirtualDevice> devices)
   {
      final BcuTypeService bcuTypeService = destFactory.getBcuTypeService();

      for (final VirtualDevice device : devices)
      {
         final Product product = device.getCatalogEntry().getProduct();
         BcuType bcuType = product.getBcuType();
         if (bcuType != null)
         {
            final BcuType bt = bcuTypeService.getBcuType(bcuType.getId());
            if (bt == null) bcuTypeService.persist(bcuType);
            else product.setBcuType(bt);
         }

         final Mask mask = device.getProgram().getMask();
         bcuType = mask.getBcuType();
         if (bcuType != null)
         {
            final BcuType bt = bcuTypeService.getBcuType(bcuType.getId());
            if (bt == null) bcuTypeService.persist(bcuType);
            else mask.setBcuType(bt);
         }
      }
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
