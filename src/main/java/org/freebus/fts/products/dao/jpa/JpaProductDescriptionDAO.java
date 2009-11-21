package org.freebus.fts.products.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.dao.ProductDescriptionDAO;

public final class JpaProductDescriptionDAO implements ProductDescriptionDAO
{
   private final EntityManager entityManager;

   JpaProductDescriptionDAO(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<String> getProductDescription(CatalogEntry entry)
   {
      final Query query = entityManager.createQuery("select pd.description from ProductDescription pd where pd.catalogEntryId=?1 order by pd.displayOrder");
      query.setParameter(1, entry.getId());
      return query.getResultList();
   }
}
