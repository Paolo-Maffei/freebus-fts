package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.VirtualDevice;

/**
 * Interface for {@link VirtualDevice} virtual-device data access methods.
 */
public interface VirtualDeviceService
{
   /**
    * Get a virtual device by id.
    * 
    * @throws DAONotFoundException if the object does not exist
    */
   public VirtualDevice getVirtualDevice(int id) throws PersistenceException;

   /**
    * Get all virtual devices.
    */
   public List<VirtualDevice> getVirtualDevices() throws PersistenceException;

   /**
    * Get all matching virtual devices.
    */
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities)
         throws PersistenceException;

   /**
    * Save a virtual device.
    */
   public void save(VirtualDevice device) throws PersistenceException;
}
