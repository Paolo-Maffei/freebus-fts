package org.freebus.fts.products.services.vdx;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.freebus.fts.common.vdx.VdxEntityManager;
import org.freebus.fts.products.FunctionalEntity;
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
   public List<VirtualDevice> getVirtualDevices() throws PersistenceException
   {
      @SuppressWarnings("unchecked")
      final List<VirtualDevice> result = (List<VirtualDevice>) manager.fetchAll(VirtualDevice.class);
      return result; 
   }

   @Override
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities) throws PersistenceException
   {
      @SuppressWarnings("unchecked")
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
   public void save(VirtualDevice device) throws PersistenceException
   {
      throw new PersistenceException("Sorry, not implemented");
   }
}
