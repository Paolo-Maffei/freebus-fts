package org.freebus.fts.products.dao;

import java.util.List;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.VirtualDevice;

/**
 * Interface for {@link VirtualDevice} virtual-device data access methods.
 */
public interface VirtualDeviceDAO
{
   /**
    * Get a virtual device by id.
    * 
    * @throws DAONotFoundException if the object does not exist
    */
   public VirtualDevice getVirtualDevice(int id) throws DAOException;

   /**
    * Get all virtual devices.
    */
   public List<VirtualDevice> getVirtualDevices() throws DAOException;

   /**
    * Get all matching virtual devices.
    */
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities)
         throws DAOException;
}
