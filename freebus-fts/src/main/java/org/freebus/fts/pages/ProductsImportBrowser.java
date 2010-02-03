package org.freebus.fts.pages;

import java.util.Set;

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
   }
}
