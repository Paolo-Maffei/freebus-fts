package org.freebus.fts.products.services.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.freebus.fts.products.Mask;
import org.freebus.fts.products.services.MaskService;

/**
 * JPA {@link Mask} service.
 */
public final class JpaMaskService implements MaskService
{
   private final EntityManager entityManager;

   JpaMaskService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public Mask getMask(int id) throws PersistenceException
   {
      return entityManager.find(Mask.class, id);
   }

   @Override
   public void persist(Mask mask)
   {
      if (!entityManager.contains(mask))
         mask.setId(0);

      entityManager.persist(mask);
   }

   @Override
   public Mask merge(Mask mask)
   {
      return entityManager.merge(mask);
   }
}
