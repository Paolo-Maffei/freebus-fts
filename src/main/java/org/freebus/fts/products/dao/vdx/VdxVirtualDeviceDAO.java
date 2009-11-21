package org.freebus.fts.products.dao.vdx;

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

import org.freebus.fts.products.FunctionalEntity;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.products.dao.DAOException;
import org.freebus.fts.products.dao.DAONotFoundException;
import org.freebus.fts.products.dao.VirtualDeviceDAO;
import org.freebus.fts.vdx.VdxFileReader;

/**
 * Data access object for virtual devices stored in a VDX file.
 */
public final class VdxVirtualDeviceDAO implements VirtualDeviceDAO
{
   private final VdxFileReader reader;
   private List<VirtualDevice> devices;
   private Map<Integer, VirtualDevice> devicesById;

   VdxVirtualDeviceDAO(VdxFileReader reader)
   {
      this.reader = reader;
   }

   private synchronized void fetchData() throws DAOException
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
         throw new DAOException(e);
      }
   }

   @Override
   public VirtualDevice getVirtualDevice(int id) throws DAOException
   {
      if (devices == null) fetchData();

      final VirtualDevice device = devicesById.get(id);
      if (device == null) throw new DAONotFoundException("Object not found, id=" + Integer.toString(id));

      return device;
   }

   @Override
   public List<VirtualDevice> getVirtualDevices() throws DAOException
   {
      if (devices == null) fetchData();
      return devices;
   }

   @Override
   public List<VirtualDevice> getVirtualDevices(FunctionalEntity[] functionalEntities) throws DAOException
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
