package org.freebus.knxcomm.netip.frames;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.freebus.knxcomm.emi.CEmiFrame;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.netip.types.ServiceType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A request that tunnels a KNX frame, either from the KNXnet/IP client to the
 * server, or from the server to the client.
 *
 * The request shall be answered with a {@link TunnelingAck tunneling
 * acknowledge}.
 */
public class TunnelingRequest extends AbstractFrame
{
   private int channelId;
   private int sequence = 1;
   private final CEmiFrame frame = new CEmiFrame();

   /**
    * Create a tunneling request object.
    *
    * @param channelId - the id of the communication channel.
    * @param sequence - the sequence counter.
    */
   public TunnelingRequest(int channelId, int sequence)
   {
      this.channelId = channelId;
      this.sequence = sequence;
   }

   /**
    * Create a tunneling request object.
    */
   public TunnelingRequest()
   {
      super();
   }

   /**
    * @return the communication channel id
    */
   public int getChannelId()
   {
      return channelId;
   }

   /**
    * Set the communication channel id.
    *
    * @param channelId - the channel id to set
    */
   public void setChannelId(int channelId)
   {
      this.channelId = channelId;
   }

   /**
    * @return the sequence number
    */
   public int getSequence()
   {
      return sequence;
   }

   /**
    * Set the sequence number.
    *
    * @param sequence - the sequence number to set
    */
   public void setSequence(int sequence)
   {
      this.sequence = sequence;
   }

   /**
    * @return the EMI frame
    */
   public EmiFrame getFrame()
   {
      return frame.getFrame();
   }

   /**
    * Set the EMI frame.
    *
    * @param frame - the frame to set
    */
   public void setFrame(EmiFrame frame)
   {
      this.frame.setFrame(frame);
   }

   /**
    * @return the additional information, or null if none
    */
   public byte[] getInfo()
   {
      return frame.getInfo();
   }

   /**
    * Set the additional information.
    *
    * @param info - the info to set
    */
   public void setInfo(byte[] info)
   {
      frame.setInfo(info);
   }

   /**
    * @return {@link ServiceType#TUNNELING_REQUEST}.
    */
   @Override
   public ServiceType getServiceType()
   {
      return ServiceType.TUNNELING_REQUEST;
   }

   /**
    * Initialize the object from the given {@link DataInput data input stream}.
    *
    * @param in - the input stream to read
    *
    * @throws InvalidDataException
    */
   @Override
   public void readData(DataInput in) throws IOException
   {
      in.skipBytes(1); // header length
      channelId = in.readUnsignedByte();
      sequence = in.readUnsignedByte();
      in.skipBytes(1); // reserved

      frame.readData(in);
   }

   /**
    * Write the object to a {@link DataOutput data output stream}.
    *
    * @param out - the output stream to write to
    *
    * @throws IOException
    */
   @Override
   public void writeData(DataOutput out) throws IOException
   {
      out.write(4); // header length
      out.write(channelId);
      out.write(sequence);
      out.write(0); // reserved

      frame.writeData(out);
   }
}
