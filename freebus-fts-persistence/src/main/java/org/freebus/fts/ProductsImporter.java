package org.freebus.fts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.freebus.fts.persistence.vdx.VdxEntityManager;
import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.CatalogEntryService;
import org.freebus.fts.products.services.FunctionalEntityService;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.VirtualDeviceService;
import org.freebus.fts.products.services.vdx.VdxProductsFactory;
import org.freebus.fts.project.DeviceParameter;

/**
 * Class for importing parts of a org.freebus.fts.products database into FTS'
 * internal database.
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
    */
   public void copy(List<VirtualDevice> devices)
   {
      final VirtualDeviceService virtDevService = destFactory.getVirtualDeviceService();
      final FunctionalEntityService funcEntService = destFactory.getFunctionalEntityService();

      final boolean ownTransaction = !destFactory.getTransaction().isActive();

      if (ownTransaction)
         destFactory.getTransaction().begin();

//      fixParameters(devices);

      for (VirtualDevice device : devices)
      {
         final CatalogEntry catalogEntry = device.getCatalogEntry();
         if (catalogEntryService.getCatalogEntry(catalogEntry.getId()) == null)
            copy(catalogEntry);

         for (FunctionalEntity funcEnt = device.getFunctionalEntity(); funcEnt != null; funcEnt = funcEnt.getParent())
         {
            if (funcEntService.getFunctionalEntity(funcEnt.getId()) == null)
               funcEntService.save(funcEnt);
         }

         virtDevService.save(device);
      }

      if (ownTransaction)
         destFactory.getTransaction().commit();
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
      destFactory.getManufacturerService().saveIfMissing(catalogEntry.getManufacturer());

      catalogEntryService.save(catalogEntry);
      catalogEntries.add(catalogEntry);
   }

   /**
    * @return the org.freebus.fts.products factory from where the
    *         org.freebus.fts.products are imported.
    */
   public ProductsFactory getSourceFactory()
   {
      return sourceFactory;
   }

   /**
    * @return the org.freebus.fts.products factory into which the
    *         org.freebus.fts.products are imported.
    */
   public ProductsFactory getDestFactory()
   {
      return destFactory;
   }
//
//   /**
//    * Correct the parameter visibility for all parameters of the {@link Program
//    * application programs} of the given devices. This is required because the
//    * visibility is stored in the device's parameter values, where it does not
//    * belong.
//    *
//    * @param devices - the devices whose program's parameters are to be
//    *           processed.
//    */
//   private void fixParameters(List<VirtualDevice> devices)
//   {
//      if (!(sourceFactory instanceof VdxProductsFactory))
//         return;
//
//      final VdxEntityManager manager = ((VdxProductsFactory) sourceFactory).getEntityManager();
//
//      final Set<Program> programs = new HashSet<Program>(devices.size());
//      for (final VirtualDevice device : devices)
//         programs.add(device.getProgram());
//
//      final Map<Integer,DeviceParameter> values = new HashMap<Integer,DeviceParameter>(16738);
//      for (final Object obj: manager.fetchAll(DeviceParameter.class))
//      {
//         DeviceParameter val = (DeviceParameter) obj;
//         if (val.getParameter() != null)
//            values.put(val.getParameter().getId(), val);
//      }
//
////      for (final Program program : programs)
////      {
////         for (final Parameter param : program.getParameters())
////            param.setVisible(values.get(param.getId()).isVisible());
////      }
//   }
}
