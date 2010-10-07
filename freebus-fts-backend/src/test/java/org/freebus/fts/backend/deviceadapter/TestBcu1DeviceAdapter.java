package org.freebus.fts.backend.deviceadapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.freebus.fts.backend.memory.AssociationTableEntry;
import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.test_utils.ProjectTestCase;
import org.junit.Test;

public class TestBcu1DeviceAdapter extends ProjectTestCase
{
   @Test
   public void testBcu1DeviceAdapter()
   {
      final Project proj = getProject();

      final Device dev = proj.getAreas().get(0).getLines().get(0).getDevices().get(0);
      assertNotNull(dev);

      final Bcu1DeviceAdapter devAdapter = new Bcu1DeviceAdapter(dev);
      assertNotNull(devAdapter);

      assertEquals(dev, devAdapter.getDevice());
   }

   @Test
   public void testGetGroupAddresses()
   {
      final Project proj = getProject();
      final Device dev = proj.getAreas().get(0).getLines().get(0).getDevices().get(0);
      final Bcu1DeviceAdapter devAdapter = new Bcu1DeviceAdapter(dev);

      final GroupAddress[] groupAddrs = devAdapter.getGroupAddresses();
      assertNotNull(groupAddrs);
      assertEquals(2, groupAddrs.length);
      assertTrue(groupAddrs[0].compareTo(groupAddrs[1]) < 0);
   }

   @Test
   public void testGetObjectDescriptors()
   {
      final Project proj = getProject();
      final Device dev = proj.getAreas().get(0).getLines().get(0).getDevices().get(0);
      final Bcu1DeviceAdapter devAdapter = new Bcu1DeviceAdapter(dev);

      final ObjectDescriptor[] objDescs = devAdapter.getObjectDescriptors();
      assertNotNull(objDescs);
      assertEquals(11, objDescs.length);
   }

   @Test
   public void testGetAssociationTable()
   {
      final Project proj = getProject();
      final Device dev = proj.getAreas().get(0).getLines().get(0).getDevices().get(0);
      final Bcu1DeviceAdapter devAdapter = new Bcu1DeviceAdapter(dev);

      final AssociationTableEntry[] assocTab = devAdapter.getAssociationTable();
      assertNotNull(assocTab);
      assertEquals(2, assocTab.length);
   }
}
