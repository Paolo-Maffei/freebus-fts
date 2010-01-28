package org.freebus.fts.products.services.jpa;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.CatalogEntry;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.CatalogEntryService;

public final class JpaCatalogEntryService implements CatalogEntryService
{
   private final EntityManager entityManager;

   JpaCatalogEntryService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<CatalogEntry> getCatalogEntries() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select ce from CatalogEntry ce");
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<CatalogEntry> getCatalogEntries(Manufacturer m, FunctionalEntity[] functionalEntities)
         throws PersistenceException
   {
      if (functionalEntities == null || functionalEntities.length < 1 || m == null)
         return new LinkedList<CatalogEntry>();

      final StringBuilder funcsStr = new StringBuilder();
      for (int i = 0; i < functionalEntities.length; ++i)
      {
         if (i > 0) funcsStr.append(',');
         funcsStr.append(functionalEntities[i].getId());
      }

      final Query query = entityManager.createQuery("select ce from CatalogEntry ce, VirtualDevice vd "
            + "where ce.id=vd.catalogEntryId and ce.manufacturerId=" + Integer.toString(m.getId())
            + " and vd.functionalEntityId in (" + funcsStr.toString() + ") order by ce.name");
      return query.getResultList();
   }

   @Override
   public CatalogEntry getCatalogEntry(int id) throws PersistenceException
   {
      return entityManager.find(CatalogEntry.class, id);
   }

   @Override
   public void save(CatalogEntry catalogEntry) throws PersistenceException
   {
      entityManager.persist(catalogEntry);
   }
}
