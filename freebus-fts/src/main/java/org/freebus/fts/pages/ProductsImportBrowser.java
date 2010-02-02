package org.freebus.fts.pages;



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
}
