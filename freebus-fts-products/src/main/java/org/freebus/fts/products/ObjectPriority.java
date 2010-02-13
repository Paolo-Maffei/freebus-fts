package org.freebus.fts.products;

/**
 * Priority of communication objects.
 */
public enum ObjectPriority
{
   /**
    * System priority. This is the highest priority.
    */
   SYSTEM,

   /**
    * High priority.
    */
   HIGH,

   /**
    * Alarm / urgent priority.
    */
   ALARM,

   /**
    * Low / normal priority. This is the lowest priority.
    */
   LOW;

   /**
    * @return the object for the given ordinal.
    */
   public static ObjectPriority valueOf(int ordinal)
   {
      for (ObjectPriority o: values())
         if (o.ordinal() == ordinal)
            return o;
      return null;
   }
}
