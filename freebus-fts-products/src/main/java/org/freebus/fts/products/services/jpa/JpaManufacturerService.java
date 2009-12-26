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

   @SuppressWarnings("unchecked")
   @Override
   public List<Manufacturer> getManufacturers() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select m from Manufacturer m order by m.name");
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Manufacturer> getActiveManufacturers() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select m from Manufacturer m where m.id in (select distinct fe.manufacturerId from FunctionalEntity fe) order by m.name");
      return query.getResultList();
   }

   @Override
   public void remove(Manufacturer manufacturer) throws PersistenceException
   {
      entityManager.remove(manufacturer);
   }

   @Override
   public void save(Manufacturer manufacturer) throws PersistenceException
   {
      entityManager.getTransaction().begin();
      entityManager.merge(manufacturer);
      entityManager.getTransaction().commit();
   }

   @Override
   public void save(List<Manufacturer> manufacturers) throws PersistenceException
   {
      entityManager.getTransaction().begin();

      for (Manufacturer manufacturer: manufacturers)
         entityManager.merge(manufacturer);

      entityManager.getTransaction().commit();
   }
}
