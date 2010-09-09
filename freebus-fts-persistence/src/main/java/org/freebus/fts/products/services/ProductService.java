package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.Product;

/**
 * Interface for {@link Product hardware product} data access methods.
 */
public interface ProductService
{
   /**
    * Get a product by id.
    *
    * @throws PersistenceException if the object does not exist
    */
   public Product getProduct(int id) throws PersistenceException;

   /**
    * Get all products.
    */
   public List<Product> getProducts() throws PersistenceException;

   /**
    * Persist the given product.
    */
   public void persist(Product product) throws PersistenceException;

   /**
    * Merge the state of the given product into the current persistence
    * context.
    * 
    * @return the product. The returned object may be different from the
    *         given object.
    */
   public Product merge(Product product) throws PersistenceException;

   /**
    * Delete products that no catalog entry references.
    *
    * @return the number of products that were deleted.
    */
   public int removeOrphanedProducts();
}
