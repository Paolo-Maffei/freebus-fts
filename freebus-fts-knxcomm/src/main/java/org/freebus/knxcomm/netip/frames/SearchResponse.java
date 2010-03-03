package org.freebus.knxcomm.netip.frames;

import org.freebus.knxcomm.netip.blocks.DeviceInfoBlock;
import org.freebus.knxcomm.netip.blocks.HostProtAddrInfo;
import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * The response to a search request.
 * 
 * @see {@link SearchRequest}.
 */
public class SearchResponse implements FrameBody
{
   private final HostProtAddrInfo hostProtAddrInfo = new HostProtAddrInfo();
   private final DeviceInfoBlock hardwareInfo = new DeviceInfoBlock();
   private final DeviceInfoBlock servicesInfo = new DeviceInfoBlock();

   /**
    * Create an empty response object.
    */
   public SearchResponse()
   {
   }

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
    * @return the information block describing the supported services.
    */
   public DeviceInfoBlock getServicesInfo()
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
   public int fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      int pos = start;
      pos += hostProtAddrInfo.fromRawData(rawData, pos);
      pos += hardwareInfo.fromRawData(rawData, pos);
      pos += servicesInfo.fromRawData(rawData, pos);

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = start;
      pos += hostProtAddrInfo.toRawData(rawData, pos);
      pos += hardwareInfo.toRawData(rawData, pos);
      pos += servicesInfo.toRawData(rawData, pos);

      return pos - start;
   }
}
