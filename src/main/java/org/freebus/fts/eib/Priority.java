package org.freebus.fts.eib;


/**
 * The priority of an EIB telegram.
 */
public enum Priority
{
   /**
    * System-priority. This is the highest priority level.
    */
   SYSTEM(0),

   /**
    * High-priority.
    */
   HIGH(1),

   /**
    * Alarm-priority.
    */
   ALARM(2),

   /**
    * Normal priority. This is the lowest priority level.
    */
   NORMAL(3);

   /**
    * The numerical id of the priority.
    */
   public final int id;

   /**
    * @return the priority with the given id.
    */
   static public Priority valueOf(int id)
   {
      for (Priority e: values())
         if (e.id==id) return e;

      throw new IllegalArgumentException("Invalid telegram-priority #" + Integer.toString(id));
   }

   // Internal constructor
   private Priority(int id)
   {
      this.id = id;
   }
}
