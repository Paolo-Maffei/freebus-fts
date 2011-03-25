package org.freebus.fts.products.services.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.ProductDescription;
import org.freebus.fts.products.services.ProductDescriptionService;

/**
 * JPA {@link ProductDescription} service.
 */
public final class JpaProductDescriptionService implements ProductDescriptionService
{
   private final EntityManager entityManager;

   JpaProductDescriptionService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public ProductDescription getProductDescription(CatalogEntry entry)
   {
      final Query query = entityManager.createQuery("select pd from ProductDescription pd where pd.catalogEntry=?1");
      query.setParameter(1, entry);
      return (ProductDescription) query.getSingleResult();
   }

   @Override
   public void persist(ProductDescription desc) throws PersistenceException
   {
      entityManager.persist(desc);
   }

   @Override
   public ProductDescription merge(ProductDescription desc) throws PersistenceException
   {
      return entityManager.merge(desc);
   }
}
