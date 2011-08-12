package org.freebus.fts.products.services;

import java.util.List;

import javax.persistence.PersistenceException;

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.Manufacturer;
import org.freebus.fts.products.Program;
import org.freebus.fts.products.VirtualDevice;

/**
 * Interface for {@link VirtualDevice} virtual-device data access methods.
 */
public interface VirtualDeviceService
{
   /**
    * Get a virtual device by id.
    * 
    * @param id - the {@link VirtualDevice#getId() id} of the virtual device.
    * 
    * @return The requested virtual device.
    * 
    * @throws PersistenceException if the object does not exist
    */
   public VirtualDevice getVirtualDevice(int id) throws PersistenceException;

   /**
    * Get a virtual device by manufacturer and name.
    * 
    * @param manufacturer - the manufacturer of the virtual device.
    * @param name - the name of the virtual device.
    * 
    * @return The requested virtual device.
    * 
    * @throws PersistenceException if the object does not exist
    */
   public VirtualDevice getVirtualDevice(Manufacturer manufacturer, String name) throws PersistenceException;

   /**
    * Get all virtual devices.
    * 
    * @return A list with all known virtual devices.
    */
   public List<VirtualDevice> getVirtualDevices() throws PersistenceException;

   /**
    * Get all virtual devices that match the given functional entities.
    * 
    * @param functionalEntities - the functional entities to match virtual
    *           devices against.
    * 
    * @return A list with all known virtual devices.
    */
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities) throws PersistenceException;

   /**
    * Find all virtual devices that reference the given application program.
    *
    * @param program - the application program that the devices shall use.
    *
    * @return A list with all found virtual devices.
    */
   public List<VirtualDevice> findVirtualDevices(Program program);

   /**
    * Persist a virtual device.
    * 
    * @param device - the virtual device to persist.
    */
   public void persist(VirtualDevice device) throws PersistenceException;

   /**
    * Merge the state of the given virtual device into the current persistence
    * context.
    * 
    * @param device - the virtual device to merge.
    * 
    * @return The virtual device. The returned object may be different from the
    *         given object.
    */
   public VirtualDevice merge(VirtualDevice device) throws PersistenceException;
}
