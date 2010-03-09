package org.freebus.knxcomm.netip.frames;

import java.io.IOException;

import org.freebus.knxcomm.netip.types.ServiceType;

/**
 * Factory class that creates frame objects from raw data.
 */
public final class FrameFactory
{
   /**
    * Create a frame object from the data. It is expected that the data contains
    * a full frame, including the frame header.
    * 
    * @param data - the data that is used to materialize the frame.
    * 
    * @return the created frame-body, or null if the frame type is valid, but
    *         there exists no frame-body class for this frame-body type.
    * 
    * @throws IOException
    */
   static public Frame createFrame(final int[] data) throws IOException
   {
      final int serviceTypeCode = (data[2] << 8) | data[3];
      final ServiceType serviceType = ServiceType.valueOf(serviceTypeCode);

      final Frame frame = serviceType.newFrameBodyInstance();
      if (frame == null)
         return null; // not implemented frame body type

      frame.fromData(data);
      return frame;
   }

   /*
    * Disabled
    */
   private FrameFactory()
   {
   }
}
