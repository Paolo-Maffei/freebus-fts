package org.freebus.knxcomm.netip.frames;

import org.freebus.knxcomm.netip.blocks.DeviceInfoBlock;
import org.freebus.knxcomm.netip.blocks.HostProtAddrInfo;
import org.freebus.knxcomm.netip.blocks.SupportedServiceFamilies;
import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * The response to a search request.
 * 
 * @see {@link SearchRequest}.
 */
public class SearchResponse extends AbstractFrame
{
   private final HostProtAddrInfo hostProtAddrInfo = new HostProtAddrInfo();
   private final DeviceInfoBlock hardwareInfo = new DeviceInfoBlock();
   private final SupportedServiceFamilies servicesInfo = new SupportedServiceFamilies();

   /**
    * @return the host protocol address info object.
    */
   public HostProtAddrInfo getHostProtAddrInfo()
   {
      return hostProtAddrInfo;
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
   public ServiceType getServiceType()
   {
      return ServiceType.SEARCH_RESPONSE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int bodyFromData(int[] rawData, int start) throws InvalidDataException
   {
      int pos = start;
      pos += hostProtAddrInfo.fromData(rawData, pos);
      pos += hardwareInfo.fromData(rawData, pos);
      pos += servicesInfo.fromData(rawData, pos);

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int bodyToData(int[] rawData, int start)
   {
      int pos = start;
      pos += hostProtAddrInfo.toData(rawData, pos);
      pos += hardwareInfo.toData(rawData, pos);
      pos += servicesInfo.toData(rawData, pos);

      return pos - start;
   }
}
