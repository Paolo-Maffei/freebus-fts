package org.freebus.knxcomm.netip.frames;

import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * An abstract base class for KNXnet/IP frames.
 */
public abstract class AbstractFrame implements Frame
{
   /**
    * The KNXnet/IP protocol version
    */
   public static final int protocolVersion = 0x10;

   /**
    * {@inheritDoc}
    * 
    * This implementation reads the header and then calls
    * {@link #bodyFromData(int[], int)} to read the frame-body.
    */
   @Override
   public final int fromData(int[] data) throws InvalidDataException
   {
      int pos = 0;

      final int headerSize = data[pos++];

      final int version = data[pos++];
      if (version != protocolVersion)
         throw new InvalidDataException("Unknown protocol version", version);

      final ServiceType type = ServiceType.valueOf((data[pos++] << 8) | data[pos++]);
      if (type != getServiceType())
         throw new InvalidDataException("Invalid frame service-type", type.code);

      final int frameSize = (data[pos++] << 8) | data[pos++];

      if (pos != headerSize)
         throw new InvalidDataException("Invalid header size (expected " + headerSize + ")", pos);

      bodyFromData(data, pos);

      return frameSize;
   }

   /**
    * Read the frame body.
    * 
    * @param data - the data to read.
    * @param start - the first byte within <code>data</code> that is read.
    * @return the number of bytes that were read.
    */
   public abstract int bodyFromData(int[] data, int start) throws InvalidDataException;

   /**
    * {@inheritDoc}
    * 
    * This implementation writes the header and then calls
    * {@link #bodyToData(int[], int)} to write the frame-body.
    */
   @Override
   public final int toData(int[] data)
   {
      int pos = 0;

      ++pos; // header size is written later
      data[pos++] = protocolVersion;

      final int typeCode = getServiceType().code;
      data[pos++] = (typeCode >> 8) & 0xff;
      data[pos++] = typeCode & 0xff;

      final int frameSizePos = pos;
      pos += 2; // frame size is written later

      final int headerSize = pos;
      data[0] = headerSize;

      final int frameSize = bodyToData(data, pos) + headerSize;
      data[frameSizePos] = frameSize >> 8;
      data[frameSizePos + 1] = frameSize & 0xff;

      return frameSize;
   }

   /**
    * Write the frame body.
    * 
    * @param data - the data to write.
    * @param start - the first byte within <code>data</code> that shall be used.
    * @return the number of bytes that were written.
    */
   public abstract int bodyToData(int[] data, int start);
}
