package org.freebus.fts.eib;

/**
 * Telegram types.
 */
public enum Type
{
   /**
    * Multicast address, one to many, connection-less.
    */
   Multicast(1<<1),

   /**
    * Broadcast address, one to all, connection-less.
    */
   Broadcast(1<<7),

   /**
    * Direct addressing, one to one.
    */
   Direct(1<<1),

   /**
    * Group addressing.
    */
   Group(1<<6),

   /**
    * Connect request.
    */
   Connect(1<<5),

   /**
    * Disconnect request.
    */
   Disconnect(1<<4);
   
   /**
    * type id
    */
   public final int id;

   /*
    * Internal constructor
    */
   private Type(int id)
   {
      this.id = id;
   }
}
