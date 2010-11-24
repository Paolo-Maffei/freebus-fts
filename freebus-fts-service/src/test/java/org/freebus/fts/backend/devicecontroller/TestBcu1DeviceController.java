package org.freebus.fts.backend.devicecontroller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.freebus.fts.common.ObjectDescriptor;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.service.devicecontroller.AssociationTableEntry;
import org.freebus.fts.service.devicecontroller.internal.Bcu1DeviceController;
import org.freebus.fts.test_utils.ProjectTestCase;
import org.junit.Test;

public class TestBcu1DeviceController extends ProjectTestCase
{
   @Test
   public void testBcu1DeviceController()
   {
      final Project proj = getProject();

      final Device dev = proj.getAreas().iterator().next().getLines().iterator().next().getDevices().iterator().next();
      assertNotNull(dev);

      final Bcu1DeviceController devController = new Bcu1DeviceController(dev);
      assertNotNull(devController);

      assertEquals(dev, devController.getDevice());
   }

   @Test
   public void testGetGroupAddresses()
   {
      final Project proj = getProject();
      final Device dev = proj.getAreas().iterator().next().getLines().iterator().next().getDevices().iterator().next();
      final Bcu1DeviceController devController = new Bcu1DeviceController(dev);

      final GroupAddress[] groupAddrs = devController.getGroupAddresses();
      assertNotNull(groupAddrs);
      assertEquals(2, groupAddrs.length);
      assertTrue(groupAddrs[0].compareTo(groupAddrs[1]) < 0);
   }

   @Test
   public void testGetObjectDescriptors()
   {
      final Project proj = getProject();
      final Device dev = proj.getAreas().iterator().next().getLines().iterator().next().getDevices().iterator().next();
      final Bcu1DeviceController devController = new Bcu1DeviceController(dev);

      final ObjectDescriptor[] objDescs = devController.getObjectDescriptors();
      assertNotNull(objDescs);
      assertEquals(11, objDescs.length);
   }

   @Test
   public void testGetAssociationTable()
   {
      final Project proj = getProject();
      final Device dev = proj.getAreas().iterator().next().getLines().iterator().next().getDevices().iterator().next();
      final Bcu1DeviceController devController = new Bcu1DeviceController(dev);

      final AssociationTableEntry[] assocTab = devController.getAssociationTable();
      assertNotNull(assocTab);
      assertEquals(2, assocTab.length);
   }
}
