package org.freebus.fts.products.services.jpa;

import java.util.List;

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

   @Override
   public List<Manufacturer> getManufacturers() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select m from Manufacturer m");

      @SuppressWarnings("unchecked")
      final List<Manufacturer> result = (List<Manufacturer>) query.getResultList();
      return result;
   }

   @Override
   public List<Manufacturer> getActiveManufacturers() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select distinct fe.manufacturer from FunctionalEntity fe");

      @SuppressWarnings("unchecked")
      final List<Manufacturer> result = (List<Manufacturer>) query.getResultList();
      return result;
   }

   @Override
   public void remove(Manufacturer manufacturer) throws PersistenceException
   {
      entityManager.remove(manufacturer);
   }

   @Override
   public void save(Manufacturer manufacturer) throws PersistenceException
   {
      entityManager.persist(manufacturer);
   }

   @Override
   public void save(List<Manufacturer> manufacturers) throws PersistenceException
   {
      for (Manufacturer manufacturer: manufacturers)
         entityManager.persist(manufacturer);
   }
}
