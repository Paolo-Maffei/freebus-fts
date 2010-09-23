package org.freebus.fts.products.services.vdx;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.freebus.fts.persistence.vdx.VdxEntityManager;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Data access object for virtual devices stored in a VD_ file.
 */
public final class VdxVirtualDeviceService implements VirtualDeviceService
{
   private final VdxEntityManager manager;

   VdxVirtualDeviceService(VdxEntityManager manager)
   {
      this.manager = manager;
   }

   @Override
   public VirtualDevice getVirtualDevice(int id) throws PersistenceException
   {
      return manager.fetch(VirtualDevice.class, id);
   }

   @Override
   public VirtualDevice getVirtualDevice(Manufacturer manufacturer, String name) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<VirtualDevice> getVirtualDevices() throws PersistenceException
   {
      return (List<VirtualDevice>) manager.fetchAll(VirtualDevice.class);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities) throws PersistenceException
   {
      final List<VirtualDevice> devices = (List<VirtualDevice>) manager.fetchAll(VirtualDevice.class);

      final Set<FunctionalEntity> ents = new HashSet<FunctionalEntity>();
      for (FunctionalEntity entity: functionalEntities)
      {
         ents.add(entity);
      }

      final List<VirtualDevice> results = new LinkedList<VirtualDevice>();
      for (VirtualDevice device: devices)
      {
         if (ents.contains(device.getFunctionalEntity())) results.add(device);
      }

      return results;
   }

   @Override
   public void persist(VirtualDevice device) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }

   @Override
   public VirtualDevice merge(VirtualDevice device) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
