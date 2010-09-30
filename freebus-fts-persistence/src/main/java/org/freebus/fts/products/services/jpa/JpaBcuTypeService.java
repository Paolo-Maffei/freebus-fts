package org.freebus.fts.products.services.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.BcuType;
import org.freebus.fts.products.services.BcuTypeService;

public final class JpaBcuTypeService implements BcuTypeService
{
   private final EntityManager entityManager;

   JpaBcuTypeService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public BcuType getBcuType(int id) throws PersistenceException
   {
      return entityManager.find(BcuType.class, id);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<BcuType> getBcuTypes() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select p from BcuType p");

      return query.getResultList();
   }

   @Override
   public void persist(BcuType program)
   {
      entityManager.persist(program);
   }

   @Override
   public BcuType merge(BcuType program)
   {
      return entityManager.merge(program);
   }
}
