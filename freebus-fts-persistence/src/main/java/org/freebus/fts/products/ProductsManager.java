package org.freebus.fts.products;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.freebus.fts.products.importer.ProductsImporter;
import org.freebus.fts.products.importer.RemappingProductsImporter;
import org.freebus.fts.products.services.ProductsFactory;
import org.freebus.fts.products.services.jpa.JpaProductsFactory;
import org.freebus.fts.products.services.vdx.VdxProductsFactory;

/**
 * Get a DAO factory for org.freebus.fts.products stored in the database or a
 * VDX file.
 */
public final class ProductsManager
{
   private final static Logger LOGGER = Logger.getLogger(ProductsManager.class);

   private ProductsManager()
   {
   }

   /**
    * @return A products factory that accesses FTS's own products database.
    */
   public static ProductsFactory getFactory()
   {
      return new JpaProductsFactory();
   }

   /**
    * Get a products factory for the products stored in the given VDX file. The
    * persistence unit is set to "default".
    * 
    * @param file - the VDX file.
    * 
    * @return the DAO factory for accessing the data in the file.
    */
   public static ProductsFactory getFactory(File file)
   {
      return getFactory(file, "default");
   }

   /**
    * Get a products factory for the products stored in the given VDX file.
    * 
    * @param file - the VDX file.
    * @param persistenceUnit - the persistence unit to use.
    * 
    * @return the DAO factory for accessing the data in the file.
    */
   public static ProductsFactory getFactory(File file, String persistenceUnit)
   {
      return new VdxProductsFactory(file, persistenceUnit);
   }

   /**
    * Create an importer that will import products from sourceFactory and save them
    * into destFactory.
    * 
    * @param sourceFactory - the products factory to import products from.
    * @param destFactory - the products factory to store products into.
    *
    * @return The products importer.
    */
   public static ProductsImporter getProductsImporter(ProductsFactory sourceFactory, ProductsFactory destFactory)
   {
//      return new DirectProductsImporter(sourceFactory, destFactory);
      return new RemappingProductsImporter(sourceFactory, destFactory);
   }

   /**
    * Import a products VDX file that is part of the program distribution.
    * The product is searched with {@link ClassLoader#getResourceAsStream(String)}.
    * 
    * @param resourceName - the name of the VDX resource to import.
    * @param vdxPersistenceUnitName - the name of the VDX persistence unit to use. E.g. "default"
    * @param destFactory - the products factory to store products into.
    */
   public static void importResource(String resourceName, String vdxPersistenceUnitName, ProductsFactory destFactory)
   {
      LOGGER.info("Importing " + resourceName);

      final InputStream inStream = ProductsManager.class.getClassLoader().getResourceAsStream(resourceName);
      if (inStream == null)
      {
         throw new RuntimeException("Could not find example products file \"" + resourceName
               + "\" in class path");
      }

      File tempFile = null;
      try
      {
         String baseName = new File(resourceName).getName();
         int idx = baseName.lastIndexOf('.');
         if (idx > 0) baseName = baseName.substring(0, idx);
         
         tempFile = File.createTempFile(baseName, ".vd_");
         tempFile.deleteOnExit();
         final OutputStream outStream = new FileOutputStream(tempFile);

         final byte[] buffer = new byte[8192];
         int rlen;
         while (true)
         {
            rlen = inStream.read(buffer, 0, buffer.length);
            if (rlen <= 0)
               break;
            outStream.write(buffer, 0, rlen);
         }

         outStream.close();
         inStream.close();
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not create temp file for products import");
      }

      final ProductsFactory vdxFactory = ProductsManager.getFactory(tempFile, vdxPersistenceUnitName);
      final ProductsFactory productsFactory = ProductsManager.getFactory();
      final ProductsImporter importer = ProductsManager.getProductsImporter(vdxFactory, destFactory);

      final List<VirtualDevice> devs = vdxFactory.getVirtualDeviceService().getVirtualDevices();

      final boolean ownTransaction = !productsFactory.getTransaction().isActive();

      try
      {
         if (ownTransaction)
            productsFactory.getTransaction().begin();

         importer.copy(devs);

         if (ownTransaction)
            productsFactory.getTransaction().commit();
      }
      finally
      {
         if (ownTransaction && productsFactory.getTransaction().isActive())
            productsFactory.getTransaction().rollback();

         if (tempFile != null)
            tempFile.delete();
      }
   }
}
