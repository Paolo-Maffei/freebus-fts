package org.freebus.fts.products.services.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.FunctionalEntityService;

public final class JpaFunctionalEntityService implements FunctionalEntityService
{
   private final EntityManager entityManager;

   JpaFunctionalEntityService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<FunctionalEntity> getFunctionalEntities()
   {
      final Query query = entityManager.createQuery("select fe from FunctionalEntity fe order by fe.name");
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<FunctionalEntity> getFunctionalEntities(Manufacturer m)
   {
      final Query query = entityManager.createQuery("select fe from FunctionalEntity fe where fe.manufacturerId=?1 order by fe.name");
      query.setParameter(1, m.getId());
      return query.getResultList();
   }

   @Override
   public FunctionalEntity getFunctionalEntity(int id)
   {
      return entityManager.find(FunctionalEntity.class, id);
   }
}
