package org.freebus.fts.products;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * A product database.
 * Loaded e.g. from a .vdx file.
 */
public class ProductDbOld
{
   private String creationDate, version, fileName;

   private final Vector<Product> products = new Vector<Product>();
   private final Map<Integer,Manufacturer> manufacturers = new HashMap<Integer,Manufacturer>();
   private final Vector<CatalogEntry> catalogEntries = new Vector<CatalogEntry>();

   /**
    * @return the creation date
    */
   public String getCreationDate()
   {
      return creationDate;
   }

   /**
    * @param creationDate the creation date to set
    */
   public void setCreationDate(String creationDate)
   {
      this.creationDate = creationDate;
   }

   /**
    * @return the version
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * @param version the version to set
    */
   public void setVersion(String version)
   {
      this.version = version;
   }

   /**
    * Set the file name.
    */
   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

   /**
    * @return the file name.
    */
   public String getFileName()
   {
      return fileName;
   }

   /**
    * Clear the object.
    */
   public void clear()
   {
      products.clear();
      catalogEntries.clear();
      manufacturers.clear();
   }
   
   /**
    * @return the id-th product.
    */
   public Product getProduct(int id)
   {
      return products.get(id);
   }

   /**
    * Add a product.
    * @return the id that was assigned to the product.
    */
   public int addProduct(Product product)
   {
      products.add(product);
      return products.size() - 1;
   }
   
   /**
    * @return the manufacturer with the given id.
    */
   public Manufacturer getManufacturer(int id)
   {
      return manufacturers.get(id);
   }

   /**
    * Add a manufacturer.
    */
   public void addManufacturer(Manufacturer manufacturer)
   {
      manufacturers.put(manufacturer.getId(), manufacturer);
   }

   /**
    * @return the id's of all manufacturers.
    */
   public Set<Integer> getManufacturerKeys()
   {
      return manufacturers.keySet();
   }
   
   /**
    * @return the id-th catalog-entry.
    */
   public CatalogEntry getCatalogEntry(int id)
   {
      return catalogEntries.get(id);
   }

   /**
    * Add a catalog-entry.
    * @return the id that was assigned to the catalog-entry.
    */
   public int addCatalogEntry(CatalogEntry catalogEntry)
   {
      catalogEntries.add(catalogEntry);
      return catalogEntries.size() - 1;
   }

   /**
    * @return the number of catalog-entries.
    */
   public int getNumCatalogEntries()
   {
      return catalogEntries.size();
   }
}
