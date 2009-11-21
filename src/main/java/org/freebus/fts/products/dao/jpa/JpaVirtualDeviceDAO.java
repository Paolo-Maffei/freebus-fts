package org.freebus.fts.products.dao.jpa;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.VirtualDeviceDAO;

public final class JpaVirtualDeviceDAO implements VirtualDeviceDAO
{
   private final EntityManager entityManager;

   JpaVirtualDeviceDAO(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public VirtualDevice getVirtualDevice(int id) throws DAOException
   {
      return entityManager.find(VirtualDevice.class, id);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<VirtualDevice> getVirtualDevices() throws DAOException
   {
      final Query query = entityManager.createQuery("select v from VirtualDevice v");
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities)
         throws DAOException
   {
      if (functionalEntities == null || functionalEntities.length < 1)
         return new LinkedList<VirtualDevice>();

      final StringBuilder funcsStr = new StringBuilder();
      for (int i = 0; i < functionalEntities.length; ++i)
      {
         if (i > 0) funcsStr.append(',');
         funcsStr.append(functionalEntities[i].getId());
      }

      final Query query = entityManager.createQuery("select vd from VirtualDevice vd "
            + "where vd.functionalEntityId in (" + funcsStr.toString() + ") order by vd.name");
      return query.getResultList();
   }
}
