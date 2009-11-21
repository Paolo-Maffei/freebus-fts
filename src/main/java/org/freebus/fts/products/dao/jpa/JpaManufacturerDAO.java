package org.freebus.fts.products.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.ManufacturerDAO;

public final class JpaManufacturerDAO implements ManufacturerDAO
{
   private final EntityManager entityManager;

   JpaManufacturerDAO(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public Manufacturer getManufacturer(int id) throws DAOException
   {
      return entityManager.find(Manufacturer.class, id);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Manufacturer> getManufacturers() throws DAOException
   {
      final Query query = entityManager.createQuery("select m from Manufacturer m order by m.name");
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Manufacturer> getActiveManufacturers() throws DAOException
   {
      final Query query = entityManager.createQuery("select m from Manufacturer m where m.id in (select distinct fe.manufacturerId from FunctionalEntity fe) order by m.name");
      return query.getResultList();
   }
}
