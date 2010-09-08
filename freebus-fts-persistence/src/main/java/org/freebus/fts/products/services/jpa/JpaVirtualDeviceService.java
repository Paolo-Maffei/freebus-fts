package org.freebus.fts.products.services.jpa;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.VirtualDeviceService;

public final class JpaVirtualDeviceService implements VirtualDeviceService
{
   private final EntityManager entityManager;

   JpaVirtualDeviceService(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   @Override
   public VirtualDevice getVirtualDevice(int id) throws PersistenceException
   {
      return entityManager.find(VirtualDevice.class, id);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<VirtualDevice> getVirtualDevices() throws PersistenceException
   {
      final Query query = entityManager.createQuery("select v from VirtualDevice v");
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities)
         throws PersistenceException
   {
      if (functionalEntities == null || functionalEntities.length < 1)
         return new LinkedList<VirtualDevice>();

      final StringBuilder funcsStr = new StringBuilder();
      for (int i = 0; i < functionalEntities.length; ++i)
      {
         if (i > 0) funcsStr.append(',');
         funcsStr.append(functionalEntities[i].getId());
      }

      final Query query = entityManager.createQuery("select vd from VirtualDevice vd, FunctionalEntity fe "
            + "where vd.functionalEntity = fe and fe.id in (" + funcsStr.toString() + ") order by vd.name");
      return query.getResultList();
   }

   @Override
   public void persist(VirtualDevice device) throws PersistenceException
   {
      entityManager.persist(device);
   }

   @Override
   public VirtualDevice merge(VirtualDevice device) throws PersistenceException
   {
      return entityManager.merge(device);
   }
}
