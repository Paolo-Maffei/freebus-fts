package org.freebus.fts.products.services.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.Product;
import org.freebus.fts.products.services.ProductService;

/**
 * Interface for {@link Product hardware product} data access methods using Java
 * Persistence (JPA).
 */
public final class JpaProductService implements ProductService
{
   private final EntityManager entityManager;

   /**
    * Create a product-service object.
    *
    * @param entityManager - the entity manager to use.
    */
   JpaProductService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Product getProduct(int id) throws PersistenceException
   {
      return entityManager.find(Product.class, id);
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<Product> getProducts() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select p from Product p order by p.name");

      return query.getResultList();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void persist(Product product)
   {
      entityManager.persist(product);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Product merge(Product product)
   {
      return entityManager.merge(product);
   }

   @Override
   public int removeOrphanedProducts()
   {
      final Query query = entityManager.createNativeQuery("delete from hw_product where product_id not in (select distinct product_id from catalog_entry)");
//      final Query query = entityManager.createQuery("delete from Product p where p not in (select distinct e.product from CatalogEntry e)");
      return query.executeUpdate();
   }
}
