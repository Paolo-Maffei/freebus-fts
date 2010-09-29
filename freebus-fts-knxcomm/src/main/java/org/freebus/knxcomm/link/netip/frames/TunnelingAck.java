package org.freebus.knxcomm.link.netip.frames;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.freebus.knxcomm.link.netip.types.ServiceType;
import org.freebus.knxcomm.link.netip.types.StatusCode;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Acknowledge for a {@link TunnelingRequest tunneling request}.
 */
public class TunnelingAck extends AbstractFrame
{
   private int channelId;
   private int sequence;
   private StatusCode status;

   /**
    * Create a tunneling acknowledge object.
    *
    * @param channelId - the id of the communication channel.
    * @param sequence - the sequence counter.
    * @param status - the status code.
    */
   public TunnelingAck(int channelId, int sequence, StatusCode status)
   {
      this.channelId = channelId;
      this.sequence = sequence;
      this.status = status;
   }

   /**
    * Create an empty tunneling acknowledge object.
    */
   public TunnelingAck()
   {
      status = StatusCode.OK;
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
    * Returns the status of the connection request.
    *
    * Common status codes are: {@link StatusCode#OK}.
    * TODO what else can occur?
    *
    * @return The status code
    */
   public StatusCode getStatus()
   {
      return status;
   }

   /**
    * Set the status code.
    *
    * @param status - the status to set
    */
   public void setStatus(StatusCode status)
   {
      this.status = status;
   }

   /**
    * @return {@link ServiceType#TUNNELING_ACK}.
    */
   @Override
   public ServiceType getServiceType()
   {
      return ServiceType.TUNNELING_ACK;
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
      status = StatusCode.valueOf(in.readUnsignedByte());
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
      out.write(status.code);
   }
}
