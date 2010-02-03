package org.freebus.fts.pages;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.freebus.fts.products.ProductsImporter;
import org.freebus.fts.products.ProductsManager;
import org.freebus.fts.products.VirtualDevice;

/**
 * A products browser that allows to mark products for import.
 */
public class ProductsImportBrowser extends ProductsBrowser
{
   private static final long serialVersionUID = 5704775166874780673L;

   public ProductsImportBrowser()
   {
      super();
      enableImportMode();
   }

   /**
    * Import the marked virtual devices.
    */
   protected void importProducts(Set<VirtualDevice> virtualDevices)
   {
      final List<VirtualDevice> virtDevsList = new Vector<VirtualDevice>(virtualDevices.size());

      final Iterator<VirtualDevice> it = virtualDevices.iterator();
      while (it.hasNext())
         virtDevsList.add(it.next());

      final ProductsImporter importer = new ProductsImporter(getProductsFactory(), ProductsManager.getFactory());
      importer.copy(virtDevsList);
   }
}
