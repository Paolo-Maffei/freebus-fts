package org.freebus.knxcomm.netip.frames;

import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A KNXnet/IP frame header.
 */
public final class FrameHeader implements FrameBody
{
   private int version = 0x10;
   private ServiceType serviceType;
   private int bodySize;

   /**
    * Set the service type of the frame.
    * 
    * @param serviceType the serviceType to set
    */
   public void setServiceType(ServiceType serviceType)
   {
      this.serviceType = serviceType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ServiceType getServiceType()
   {
      return serviceType;
   }

   /**
    * Set the KNXnet/IP protocol version. 0x10 is version 1.0, which is the
    * current default.
    */
   public void setVersion(int version)
   {
      this.version = version;
   }

   /**
    * @return the KNXnet/IP protocol version.
    */
   public int getVersion()
   {
      return version;
   }

   /**
    * Set the size of the frame body in bytes.
    *
    * @param bodySize the body size to set
    */
   public void setBodySize(int bodySize)
   {
      this.bodySize = bodySize;
   }

   /**
    * @return the size of the frame body in bytes.
    */
   public int getBodySize()
   {
      return bodySize;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      int pos = start;

      final int headerSize = rawData[++pos];
      version = rawData[pos++];
      
      final int serviceTypeCode = (rawData[pos++] << 8) | rawData[pos++];
      serviceType = ServiceType.valueOf(serviceTypeCode);

      bodySize = (rawData[pos++] << 8) | rawData[pos++];

      return headerSize;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      int pos = start + 1;

      // rawData[start] contains the size and is set last 
      rawData[pos++] = version;

      rawData[pos++] = (serviceType.code >> 8) & 0xff;
      rawData[pos++] = serviceType.code & 0xff;

      rawData[pos++] = (bodySize >> 8) & 0xff;
      rawData[pos++] = bodySize & 0xff;

      rawData[start] = pos - start;

      return pos - start;
   }
}
