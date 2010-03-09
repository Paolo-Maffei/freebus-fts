package org.freebus.knxcomm.netip.frames;

import org.freebus.knxcomm.netip.blocks.DeviceInfoBlock;
import org.freebus.knxcomm.netip.blocks.SupportedServiceFamilies;
import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * The response to a description request.
 * 
 * @see {@link DescriptionRequest}.
 */
public class DescriptionResponse extends AbstractFrame
{
   private final DeviceInfoBlock hardwareInfo = new DeviceInfoBlock();
   private final SupportedServiceFamilies servicesInfo = new SupportedServiceFamilies();

   /**
    * @return the service type: {@link ServiceType#DESCRIPTION_RESPONSE}.
    */
   @Override
   public ServiceType getServiceType()
   {
      return ServiceType.DESCRIPTION_RESPONSE;
   }

   /**
    * @return the information block describing the KNXnet/IP server hardware.
    */
   public DeviceInfoBlock getHardwareInfo()
   {
      return hardwareInfo;
   }

   /**
    * @return the block describing the supported services.
    */
   public SupportedServiceFamilies getServicesInfo()
   {
      return servicesInfo;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int bodyFromData(int[] data, int start) throws InvalidDataException
   {
      int pos = start;
      pos += hardwareInfo.fromData(data, pos);
      pos += servicesInfo.fromData(data, pos);

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int bodyToData(int[] data, int start)
   {
      int pos = start;
      pos += hardwareInfo.toData(data, pos);
      pos += servicesInfo.toData(data, pos);

      return pos - start;
   }
}
