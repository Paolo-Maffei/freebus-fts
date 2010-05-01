package org.freebus.knxcomm.applicationData;

import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestDeviceDescriptorPropertiesType0
{
   @Test(expected = ApplicationDataException.class)
   public void testloadProperties0012() throws InvalidDataException, ApplicationDataException
   {
      DeviceDescriptorResponse ddr = new DeviceDescriptorResponse();
      int[] data = { 0x40, 0x00, 0x12 };

      ddr.fromRawData(data, 0, 3);
      DeviceDescriptorPropertiesFactory ddpf = new DeviceDescriptorPropertiesFactory();
      ddpf.getDeviceDescriptor(ddr);
   }

   @Test(expected = ApplicationDataException.class)
   public void testloadProperties9999() throws InvalidDataException, ApplicationDataException
   {
      DeviceDescriptorResponse ddr = new DeviceDescriptorResponse();
      int[] data = { 0x40, 0x99, 0x99 };

      ddr.fromRawData(data, 0, 3);
      DeviceDescriptorPropertiesFactory ddpf = new DeviceDescriptorPropertiesFactory();
      ddpf.getDeviceDescriptor(ddr);
   }
}
