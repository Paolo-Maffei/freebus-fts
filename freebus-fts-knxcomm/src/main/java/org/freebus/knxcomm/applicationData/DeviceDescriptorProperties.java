package org.freebus.knxcomm.applicationData;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.freebus.knxcomm.application.DeviceDescriptorResponse;

public interface DeviceDescriptorProperties
{

   /**
    * Create a MemoryAddressMapper with the loaded PorpertieFile
    * 
    * @return
    */
   public MemoryAddressMapper getMemoryAddressMapper();

 }
