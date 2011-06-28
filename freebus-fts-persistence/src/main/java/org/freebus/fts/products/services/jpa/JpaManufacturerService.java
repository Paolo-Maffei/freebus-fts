package org.freebus.fts.products.services.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ManufacturerService;

/**
 * JPA {@link Manufacturer} service.
 */
public final class JpaManufacturerService implements ManufacturerService
{
   private final EntityManager entityManager;

   JpaManufacturerService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public Manufacturer getManufacturer(int id) throws PersistenceException
   {
      return entityManager.find(Manufacturer.class, id);
   }

   @Override
   public Manufacturer getManufacturer(String name) throws PersistenceException
   {
      final Query query = entityManager.createQuery("select m from Manufacturer m where m.name = ?1");
      query.setParameter(1, name);
      try
      {
         return (Manufacturer) query.getSingleResult();
      }
      catch (PersistenceException e)
      {
         throw new PersistenceException("Failed to load manufacturer \"" + name +"\"", e);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Manufacturer> getManufacturers() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select m from Manufacturer m");
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Manufacturer> getActiveManufacturers() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select distinct fe.manufacturer from FunctionalEntity fe");
      return query.getResultList();
   }

   @Override
   public void remove(Manufacturer manufacturer) throws PersistenceException
   {
      entityManager.remove(manufacturer);
   }

   @Override
   public void saveIfMissing(Manufacturer manufacturer) throws PersistenceException
   {
      final Manufacturer m = entityManager.find(Manufacturer.class, manufacturer.getId());
      if (m == null)
         entityManager.persist(manufacturer);
   }

   @Override
   public void persist(Manufacturer manufacturer) throws PersistenceException
   {
      entityManager.persist(manufacturer);
   }

   @Override
   public Manufacturer merge(Manufacturer manufacturer) throws PersistenceException
   {
      return entityManager.merge(manufacturer);
   }
}
