package org.freebus.knxcomm.emi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.freebus.knxcomm.emi.types.EmiFrameType;

/**
 * T_Connect.req - transport layer connect request
 */
public final class T_Connect_req extends AbstractEmiFrame
{
   protected int dest = 0;

   /**
    * Create an empty connect-request object.
    */
   public T_Connect_req()
   {
      super(EmiFrameType.T_CONNECT_REQ);
   }

   /**
    * Create a connect-request object.
    */
   public T_Connect_req(int dest)
   {
      super(EmiFrameType.T_CONNECT_REQ);
      this.dest = dest;
   }

   /**
    * Set the destination address.
    */
   public void setDest(int dest)
   {
      this.dest = dest;
   }

   /**
    * @return he destination address.
    */
   public int getDest()
   {
      return dest;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void readData(DataInput in) throws IOException
   {
      in.skipBytes(3);
      dest = in.readUnsignedShort();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeData(DataOutput out) throws IOException
   {
      out.write(0);
      out.writeShort(0);
      out.writeShort(dest);
   }
}