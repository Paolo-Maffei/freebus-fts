package org.freebus.knxcomm.telegram;

import org.freebus.fts.common.address.Address;
import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.knxcomm.application.Application;
import org.freebus.knxcomm.application.ApplicationFactory;
import org.freebus.knxcomm.application.ApplicationType;

/**
 * A communication data packet as it is sent on the EIB bus.
 *
 * It is mandatory for subclasses to override {@link #clone()} to avoid
 * problems.
 */
public class Telegram implements Cloneable
{
   private PhysicalAddress from = PhysicalAddress.NULL;
   private Address dest = GroupAddress.BROADCAST;
   private int routingCounter = 6;
   private Priority priority = Priority.LOW;
   private boolean repeated = false;
   private Transport transport = Transport.Individual;
   private int sequence = 0;
   private Application application = null;

   /**
    * Create an empty telegram object.
    */
   public Telegram()
   {
   }

   /**
    * Clone the telegram.
    */
   @Override
   public Telegram clone()
   {
      try
      {
         return (Telegram) super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Initialize the message from the given raw data, beginning at start.
    *
    * @throws InvalidDataException
    */
   public void fromRawData(int[] rawData, int start) throws InvalidDataException
   {
      int pos = start;

      /*
       * The control byte. bit 7: frame length-type: 0=extended frame,
       * 1=standard frame. bit 6: frame type 1: 0=data telegram, 1=poll-data
       * telegram. bit 5: repeated flag: 0=if the telegram is repeated, 1=not
       * repeated. bit 4: frame type 2: 0=acknowledge frame, 1=normal frame. bit
       * 3+2: Priority: 0=system, 1=urgent, 2=normal, 3=low priority. bit 1: 0
       * bit 0: 0
       */
      final int ctrl = rawData[pos++];

      priority = Priority.valueOf((ctrl >> 2) & 3);
      repeated = (ctrl & 0x20) == 0;

      // 16-bit sender address
      from = new PhysicalAddress(rawData[pos++], rawData[pos++]);

      // 16-bit destination address
      final int destAddr = (rawData[pos++] << 8) | rawData[pos++];

      /*
       * DRL byte (DRL means: destination address, routing, length). bit 7:
       * destination is 0=physical address, 1=group address bit 6..4: routing
       * hop count: 0=never route, 1..6=number of routings, 7=always route bit
       * 3..0: data length minus 2: 0 means 2 bytes, 15 means 17 bytes
       */
      final int drl = rawData[pos++];

      final boolean isGroup = (drl & 0x80) != 0;
      if (isGroup)
         dest = new GroupAddress(destAddr);
      else dest = new PhysicalAddress(destAddr);

      routingCounter = (drl >> 4) & 7;

      int dataLen = drl & 15;
      if (dataLen + pos >= rawData.length)
         throw new InvalidDataException("Invalid data length", dataLen);

      // TPCI - transport control field
      int tpci = rawData[pos++];
      transport = Transport.valueOf(isGroup, tpci);

      if (transport.hasSequence)
         sequence = (tpci >> 2) & 15;
      else sequence = 0;

      if (rawData.length > pos && (transport.mask & 3) == 0)
      {
         // APCI - application type & data bits
         final int apciByte = rawData[pos++];
         final int apci = ((tpci & 3) << 8) | apciByte;
         final ApplicationType type = ApplicationType.valueOf(apci);
         application = ApplicationFactory.createApplication(type);

         final int dataMask = type.getDataMask();
         if (dataMask != 0 && dataLen <= 1)
         {
            // ACPI byte contains data bits
            application.fromRawData(new int[] { apciByte }, 0, 1);
         }
         else if (dataLen > 1)
         {
            // telegram contains extra data
            --pos;
            application.fromRawData(rawData, pos, dataLen);
            pos += dataLen;
         }
         else
         {
            // telegram contains no extra data
         }
      }
      else
      {
         application = null;
      }
   }

   /**
    * @return the application.
    */
   public Application getApplication()
   {
      return application;
   }

   /**
    * @return the application type.
    */
   public ApplicationType getApplicationType()
   {
      return application == null ? ApplicationType.None : application.getType();
   }

   /**
    * Returns the destination address. This can either be a
    * {@link PhysicalAddress} physical address, or a {@link GroupAddress} group
    * address.
    *
    * @return the destination address.
    */
   public Address getDest()
   {
      return dest;
   }

   /**
    * @return the sender address.
    */
   public PhysicalAddress getFrom()
   {
      return from;
   }

   /**
    * @return the priority.
    */
   public Priority getPriority()
   {
      return priority;
   }

   /**
    * Returns the routing counter. 0 means never route, 1..6 is the number of
    * routing hops that would occur, 7 means route always.
    *
    * @return the routing counter.
    */
   public int getRoutingCounter()
   {
      return routingCounter;
   }

   /**
    * @return the sequence number. Only used for connected-data mode transport
    *         types.
    */
   public int getSequence()
   {
      return sequence;
   }

   /**
    * @return the transport type.
    */
   public Transport getTransport()
   {
      return transport;
   }

   /**
    * @return the repeated flag.
    */
   public boolean isRepeated()
   {
      return repeated;
   }

   /**
    * Set the application.
    *
    * @param application - the application to set.
    */
   public void setApplication(Application application)
   {
      this.application = application;
   }

   /**
    * Set the application by specifying the {@link ApplicationType application
    * type}.
    *
    * @param type - the application type.
    *
    * @throws InvalidDataException if the raw data is invalid.
    */
   public void setApplication(ApplicationType type)
   {
      application = ApplicationFactory.createApplication(type);
   }

   /**
    * Set the destination address. This can either be a {@link PhysicalAddress}
    * physical address, or a {@link GroupAddress} group address.
    *
    * Also sets the transport type, if it is yet unset: to
    * {@link Transport#Individual} if the destination is a
    * {@link PhysicalAddress}, or to {@link Transport#Group} if the destination
    * is a {@link GroupAddress}.
    */
   public void setDest(Address dest)
   {
      this.dest = dest;

      if (transport == null)
      {
         if (dest instanceof GroupAddress)
            transport = Transport.Group;
         else transport = Transport.Individual;
      }
   }

   /**
    * Set the sender address.
    */
   public void setFrom(PhysicalAddress from)
   {
      this.from = from;
   }

   /**
    * Set the priority. The default priority is {@link Priority#LOW}.
    */
   public void setPriority(Priority priority)
   {
      this.priority = priority;
   }

   /**
    * Set the repeated flag.
    */
   public void setRepeated(boolean repeated)
   {
      this.repeated = repeated;
   }

   /**
    * Set the routing counter. 0 means never route, 1..6 is the number of
    * routing hops that would occur, 7 means route always. Be careful with using
    * 7, it may result in telegrams that run on the bus infinitely.
    */
   public void setRoutingCounter(int routingCounter)
   {
      if (routingCounter < 0)
         routingCounter = 0;
      else if (routingCounter > 7)
         routingCounter = 7;

      this.routingCounter = routingCounter;
   }

   /**
    * Set the sequence number. Only used for connected-data mode transport
    * types.
    */
   public void setSequence(int sequence)
   {
      this.sequence = sequence;
   }

   /**
    * Set the transport type.
    */
   public void setTransport(Transport transport)
   {
      this.transport = transport;
   }

   /**
    * Fill the raw data of the message into the array rawData, starting at index
    * start.
    *
    * @return number of bytes that were used.
    */
   public int toRawData(int[] rawData, int start)
   {
      int pos = start;

      /*
       * The control byte. bit 7: frame length-type: 0=extended frame,
       * 1=standard frame. bit 6: frame type 1: 0=data telegram, 1=poll-data
       * telegram. bit 5: repeated flag: 0=if the telegram is repeated, 1=not
       * repeated. bit 4: frame type 2: 0=acknowledge frame, 1=normal frame. bit
       * 3+2: Priority: 0=system, 1=urgent, 2=normal, 3=low priority. bit 1: 0
       * bit 0: 0
       */
      int ctrl = (1 << 7) | (1 << 4);
      if (!repeated)
         ctrl |= 1 << 5;
      ctrl |= priority.id << 2;
      rawData[pos++] = ctrl;

      int addr = from.getAddr();
      rawData[pos++] = addr >> 8;
      rawData[pos++] = addr & 255;

      addr = dest.getAddr();
      rawData[pos++] = addr >> 8;
      rawData[pos++] = addr & 255;

      int applicationLen = 0;
      int apci = 0;

      if (transport.mask != 255 && application != null)
      {
         applicationLen = application.toRawData(rawData, pos + 2);
         apci = application.getType().getApci();
      }

      int drl = (routingCounter & 7) << 4;
      drl |= applicationLen & 15;
      if (dest instanceof GroupAddress)
         drl |= 0x80;
      rawData[pos++] = drl;

      int tpci = transport.value;
      tpci |= (apci >> 8) & ~transport.mask;
      if (transport.hasSequence)
         tpci |= (sequence & 15) << 2;
      rawData[pos++] = tpci;

      pos += applicationLen;

      return pos - start;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return from.getAddr() * 13 + dest.getAddr();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(final Object o)
   {
      if (this == o)
         return true;

      if (!(o instanceof Telegram))
         return false;

      final Telegram oo = (Telegram) o;

      return from.equals(oo.from) && dest.equals(oo.dest) && transport == oo.transport && sequence == oo.sequence
            && (application == null ? oo.application == null : application.equals(oo.application));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      final StringBuffer sb = new StringBuffer();

      sb.append(getTransport()).append(' ');
      sb.append("from ").append(getFrom()).append(" to ").append(getDest()).append(", ");
      sb.append(application == null ? "no application" : application.toString());

      return sb.toString();
   }
}
