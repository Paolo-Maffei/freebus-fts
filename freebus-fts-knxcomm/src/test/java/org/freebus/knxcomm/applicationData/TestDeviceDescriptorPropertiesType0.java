package org.freebus.knxcomm.applicationData;


import org.freebus.knxcomm.application.attic.DeviceDescriptorPropertiesFactory;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor0;
import org.freebus.knxcomm.telegram.InvalidDataException;
import org.junit.Test;

public class TestDeviceDescriptorPropertiesType0
{
   @Test
   public void testloadProperties0012() throws InvalidDataException, ApplicationDataException
   {
      DeviceDescriptor deviceDescriptor = new DeviceDescriptor0(0x0012);
      DeviceDescriptorPropertiesFactory ddpf = new DeviceDescriptorPropertiesFactory();
      ddpf.getDeviceDescriptor(deviceDescriptor);
   }

   @Test(expected = ApplicationDataException.class)
   public void testloadProperties9999() throws InvalidDataException, ApplicationDataException
   {
      DeviceDescriptor deviceDescriptor = new DeviceDescriptor0(9999);
      DeviceDescriptorPropertiesFactory ddpf = new DeviceDescriptorPropertiesFactory();
      ddpf.getDeviceDescriptor(deviceDescriptor);
   }
}
