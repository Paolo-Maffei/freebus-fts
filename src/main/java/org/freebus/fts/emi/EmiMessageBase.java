package org.freebus.fts.emi;

import org.freebus.fts.eib.InvalidDataException;


/**
 * Base class for "External Message Interface" (EMI) messages.
 */
public abstract class EmiMessageBase implements EmiMessage
{
   protected final EmiMessageType type;

   /**
    * Internal constructor that sets the message type to {@link EmiMessageType#UNKNOWN}.
    */
   protected EmiMessageBase()
   {
      this.type = EmiMessageType.UNKNOWN;
   }
   
   /**
    * Create a new message and set the message type.
    * Internal constructor for subclasses.
    */
   protected EmiMessageBase(EmiMessageType type)
   {
      this.type = type;
   }

   /* (non-Javadoc)
    * @see org.freebus.fts.eib.EmiMessageInterface#getType()
    */
   public final EmiMessageType getType()
   {
      return type;
   }

   /**
    * @return the message type as a string.
    */
   public final String getTypeString()
   {
      final Class<?> clazz = getClass();
      final Class<?> enclosingClazz = clazz.getEnclosingClass();

      if (enclosingClazz == null) return clazz.getSimpleName();
      return enclosingClazz.getSimpleName() + '.' + clazz.getSimpleName();
   }

   /* (non-Javadoc)
    * @see org.freebus.fts.eib.EmiMessageInterface#fromRawData(int[], int)
    */
   public abstract void fromRawData(int[] rawData, int start) throws InvalidDataException;

   /* (non-Javadoc)
    * @see org.freebus.fts.eib.EmiMessageInterface#toRawData(int[], int)
    */
   public abstract int toRawData(int[] rawData, int start);

   /**
    * @return the address as a string: "xx.xx.xx" for a physical address,
    * "xx/xx/xx" for a group address.
    */
   protected String addrToString(int addr, boolean isGroup)
   {
      if (isGroup) return String.format("%d/%d/%d", (addr>>11)&15, (addr>>7)&15, addr&127);
      return String.format("%d.%d.%d", (addr>>12)&15, (addr>>8)&15, addr&255);
   }

   /**
    * @return the message in a human readable form.
    */
   @Override
   public String toString()
   {
      return getTypeString();
   }
}
