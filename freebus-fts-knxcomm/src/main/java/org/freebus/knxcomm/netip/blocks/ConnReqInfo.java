package org.freebus.knxcomm.netip.blocks;

import org.freebus.knxcomm.netip.types.ConnectionType;
import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * Connection request information data block.
 */
public final class ConnReqInfo implements Block
{
   private ConnectionType type;
   private int layer, reserved;

   /**
    * Create an empty connection request information object.
    */
   public ConnReqInfo()
   {
   }

   /**
    * Create a connection request information object.
    *
    * @param type - the connection type.
    * @param layer - the protocol layer.
    */
   public ConnReqInfo(ConnectionType type, int layer)
   {
      this.type = type;
      this.layer = layer;
   }

   /**
    * Set the connection type.
    *
    * @param type - the connection type to set.
    */
   public void setType(ConnectionType type)
   {
      this.type = type;
   }

   /**
    * @return the connection type
    */
   public ConnectionType getType()
   {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int fromData(int[] data, int start) throws InvalidDataException
   {
      int pos = start;

      int len = data[pos++] - 2;
      type = ConnectionType.valueOf(data[pos++]);

      layer = data[pos++];
      reserved = data[pos++];

      return pos - start + len;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toData(int[] data, int start)
   {
      data[start++] = 3; // length
      data[start++] = type.code;
      data[start++] = layer;
      data[start++] = reserved;

      return 3;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return type == null ? 0 : type.code;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(final Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof ConnReqInfo))
         return false;

      final ConnReqInfo oo = (ConnReqInfo) o;
      return type == oo.type && layer == oo.layer;
   }
}
