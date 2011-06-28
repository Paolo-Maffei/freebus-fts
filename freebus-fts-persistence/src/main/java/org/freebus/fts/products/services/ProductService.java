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
    * @param id - the id of the product to get.
    * @return The product with the id.
    *
    * @throws PersistenceException if the object does not exist
    */
   public Product getProduct(int id) throws PersistenceException;

   /**
    * @return Get all products.
    *
    * @throws PersistenceException if the object does not exist
    */
   public List<Product> getProducts() throws PersistenceException;

   /**
    * Persist the given product.
    * 
    * @param product - the product to persist.
    *
    * @throws PersistenceException if the object does not exist
    */
   public void persist(Product product) throws PersistenceException;

   /**
    * Merge the state of the given product into the current persistence
    * context.
    * 
    * @param product - the product to merge.
    * 
    * @return the product. The returned object may be different from the
    *         given object.
    *
    * @throws PersistenceException if the object does not exist
    */
   public Product merge(Product product) throws PersistenceException;

   /**
    * Delete products that no catalog entry references.
    *
    * @return The number of products that were deleted.
    */
   public int removeOrphanedProducts();
}
