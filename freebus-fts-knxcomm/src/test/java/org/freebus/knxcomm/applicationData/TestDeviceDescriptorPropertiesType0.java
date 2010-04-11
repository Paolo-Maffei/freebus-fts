package org.freebus.knxcomm.applicationData;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestDeviceDescriptorPropertiesType0
{
   @Test
   public void testloadProperties0012()
   {
      DeviceDescriptorResponse ddr = new DeviceDescriptorResponse();
      int[] data = { 0x40, 0x00, 0x12 };

      try
      {
         ddr.fromRawData(data, 0, 3);
         DeviceDescriptorPropertiesFactory ddpf = new DeviceDescriptorPropertiesFactory();
         DeviceDescriptorProperties ddp = ddpf.getDeviceDescriptor(ddr);


      }
      catch (AppilcationDataException e)
      {
         assertNull(e);
         //e.printStackTrace();
      }
      catch (InvalidDataException e)
      {
         assertNull(e);
         //e1.printStackTrace();
      }
      
   }
   
   @Test
   public void testloadProperties9999()
   {
      DeviceDescriptorResponse ddr = new DeviceDescriptorResponse();
      int[] data = { 0x40, 0x99, 0x99 };

      try
      {
         ddr.fromRawData(data, 0, 3);
         DeviceDescriptorPropertiesFactory ddpf = new DeviceDescriptorPropertiesFactory();
         DeviceDescriptorProperties ddp = ddpf.getDeviceDescriptor(ddr);

      }
      catch (AppilcationDataException e)
      {
         return;
      }
      catch (InvalidDataException e)
      {
         assertNull(e);
      }
      fail("no Exception is thorwn");
     
      
   }
}
