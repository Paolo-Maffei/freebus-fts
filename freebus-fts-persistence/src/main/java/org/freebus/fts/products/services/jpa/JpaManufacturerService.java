package org.freebus.fts.products.services.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.services.ManufacturerService;

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
   public void save(Manufacturer manufacturer) throws PersistenceException
   {
      entityManager.merge(manufacturer);
   }

   @Override
   public void save(List<Manufacturer> manufacturers) throws PersistenceException
   {
      for (Manufacturer manufacturer: manufacturers)
         entityManager.persist(manufacturer);
   }
}
