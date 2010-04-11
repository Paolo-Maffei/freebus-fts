package org.freebus.knxcomm.applicationData;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.freebus.knxcomm.application.DeviceDescriptorResponse;
import org.freebus.knxcomm.serial.Ft12Connection;

public class DeviceDescriptorPropertiesType0 implements DeviceDescriptorProperties
{

   private Logger logger = Logger.getLogger(Ft12Connection.class);
   Properties deviceProperties;

   /**
    * Internal converter for int[] to String
    * 
    * @param data Application data Array
    * 
    * @return Mask String
    */
   private String Type2MaskString(int[] data)
   {
      return String.format("%02X%02X", data[0], data[1]);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MemoryAddressMapper getMemoryAddressMapper()
   {
      return new MemoryAddressMapper(deviceProperties);
   }

   /**
    * Load the PropertiesFile for the DeviceDescriptor properties from the
    * resource folder
    * 
    * @param deviceDescriptorResponse
    * @throws AppilcationDataException
    */
   protected void loadProperties(DeviceDescriptorResponse deviceDescriptorResponse) throws AppilcationDataException
   {
      //TODO Nice to have: Search in the resources folder the properties files
      String[] kownPropertiesFiles = { "0010", "0011", "0012" };
      boolean PropertieFileExist = false;
      deviceProperties = new Properties();
      String mask = Type2MaskString(deviceDescriptorResponse.getDescriptor());
      InputStream in = null;
      ClassLoader cl = this.getClass().getClassLoader();

      in = cl.getResourceAsStream("DeviceDescriptorType0_" + mask + ".properties");
      logger.debug("Load device descriptor type 0 properties für mask " + mask);
      for (String s : kownPropertiesFiles)
      {
         if (s.equals(mask))
            PropertieFileExist = true;
      }
      if (!PropertieFileExist){
         logger.error("The device descriptor mask " + mask+" is not supported");
         throw new AppilcationDataException("The device descriptor mask " + mask+" is not supported");
         
      }
         
      if (in == null)
      {
         try
         {
            in = new FileInputStream("../freebus-fts-knxcomm/src/main/resources/DeviceDescriptorType0_" + mask
                  + ".properties");
            
         }
         catch (FileNotFoundException e)
         {
            logger.error("DeviceDescriptorType0_" + mask + ".properties cannot loaded", e);
            throw new AppilcationDataException("DeviceDescriptorType0_" + mask + ".properties cannot loaded");
         }
      }
      try
      {
         deviceProperties.load(in);
         in.close();
      }
      catch (IOException e)
      {
         logger.error("Fie DeviceDescriptorType0_" + mask + ".properties cannot loaded", e);
         throw new AppilcationDataException("Fie DeviceDescriptorType0_" + mask + ".properties cannot loaded");
      }
     
   }
}
