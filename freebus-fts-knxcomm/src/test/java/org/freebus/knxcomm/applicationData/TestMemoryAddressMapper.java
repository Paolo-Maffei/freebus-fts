package org.freebus.knxcomm.applicationData;


import static org.junit.Assert.assertEquals;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptor0;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptorProperties;
import org.freebus.knxcomm.application.devicedescriptor.DeviceDescriptorPropertiesFactory;
import org.freebus.knxcomm.applicationData.MemoryAddressTypes;
import org.junit.Test;

public class TestMemoryAddressMapper
{
   @Test
   public void testGetMemoryAddressIntArr() throws Exception
   {
      DeviceDescriptorProperties ddp = null;
      DeviceDescriptorPropertiesFactory ddpf;
      DeviceDescriptorResponse ddr;
      MemoryAddressMapper mam;
      int[] ddrdata = { 0x40, 0x00, 0x12 };
      int[] memaddr = { 0x01, 0x06 };

      ddr = new DeviceDescriptorResponse();
      ddr.fromRawData(ddrdata, 0, 3);
      
      DeviceDescriptor deviceDescriptor = new DeviceDescriptor0(0x0012);
      ddpf = new DeviceDescriptorPropertiesFactory();
      ddp = ddpf.getDeviceDescriptor(deviceDescriptor);

      mam = ddp.getMemoryAddressMapper();
      MemoryAddress memoryAddress = mam.getMemoryAddress(memaddr);
      
      assertEquals(memoryAddress.getMemoryAddressType(), MemoryAddressTypes.ApplicationID);

   }

}
