package org.freebus.fts.products.services.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxFileReader;
import org.freebus.fts.products.Product;
import org.freebus.fts.products.services.ProductService;

/**
 * Data access object for hardware products stored in a VD_ file.
 */
public final class VdxProductService implements ProductService
{
   private final VdxFileReader reader;
   private Object[] rawProducts;
   private List<Product> products;

   VdxProductService(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws PersistenceException
   {
      if (products != null) return;

      try
      {
         rawProducts = reader.getSectionEntries("hw_product", Product.class);
         Arrays.sort(rawProducts, new Comparator<Object>()
         {
            @Override
            public int compare(Object a, Object b)
            {
               return ((Product) a).getName().compareTo(((Product) b).getName());
            }
         });
         products = new ArrayList<Product>(rawProducts.length);
         for (Object obj : rawProducts)
            products.add((Product) obj);
      }
      catch (IOException e)
      {
         throw new PersistenceException(e);
      }
   }

   @Override
   public Product getProduct(int id) throws PersistenceException
   {
      if (products == null) fetchData();

      for (Product product: products)
         if (product.getId() == id) return product;

      return null;
   }

   @Override
   public List<Product> getProducts() throws PersistenceException
   {
      if (products == null) fetchData();
      return products;
   }

   @Override
   public void persist(Product product)
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public Product merge(Product product)
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public int removeOrphanedProducts()
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
