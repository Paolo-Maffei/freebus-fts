package org.freebus.knxcomm.application.devicedescriptor;

import org.freebus.knxcomm.applicationData.MemoryAddressMapper;


public interface DeviceDescriptorProperties
{

   /**
    * Create a {@link MemoryAddressMapper} with the loaded PorpertieFile
    *
    * @return the memory address mapper
    */
   public MemoryAddressMapper getMemoryAddressMapper();

 }
