package org.freebus.fts.products.services.vdx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.freebus.fts.common.vdx.VdxFileReader;
import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.services.VirtualDeviceService;

/**
 * Data access object for virtual devices stored in a VD_ file.
 */
public final class VdxVirtualDeviceService implements VirtualDeviceService
{
   private final VdxFileReader reader;
   private List<VirtualDevice> devices;
   private Map<Integer, VirtualDevice> devicesById;

   VdxVirtualDeviceService(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws PersistenceException
   {
      if (devices != null) return;

      try
      {
         final Object[] arr = reader.getSectionEntries("virtual_device", VirtualDevice.class);
         Arrays.sort(arr, new Comparator<Object>()
         {
            @Override
            public int compare(Object a, Object b)
            {
               return ((VirtualDevice) a).getName().compareTo(((VirtualDevice) b).getName());
            }
         });

         devices = new ArrayList<VirtualDevice>(arr.length);
         devicesById = new HashMap<Integer, VirtualDevice>((arr.length << 1) + 31);

         for (Object obj : arr)
         {
            final VirtualDevice device = (VirtualDevice) obj;

            devices.add(device);
            devicesById.put(device.getId(), device);
         }
      }
      catch (IOException e)
      {
         throw new PersistenceException(e);
      }
   }

   @Override
   public VirtualDevice getVirtualDevice(int id) throws PersistenceException
   {
      if (devices == null) fetchData();

      final VirtualDevice device = devicesById.get(id);
      if (device == null) throw new PersistenceException("Object not found, id=" + Integer.toString(id));

      return device;
   }

   @Override
   public List<VirtualDevice> getVirtualDevices() throws PersistenceException
   {
      if (devices == null) fetchData();
      return devices;
   }

   @Override
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities) throws PersistenceException
   {
      if (devices == null) fetchData();

      final Set<Integer> ids = new HashSet<Integer>();
      for (FunctionalEntity entity: functionalEntities)
         ids.add(entity.getId());

      final List<VirtualDevice> results = new LinkedList<VirtualDevice>();
      for (VirtualDevice device: devices)
      {
         if (ids.contains(device.getFunctionalEntityId())) results.add(device);
      }

      return results;
   }
}
