package org.freebus.fts.common.types;

/**
 * Priority of communication objects.
 * 
 * The {@link #ordinal() ordinal} gives the numeric value of the priority as it
 * is used in communication and the KNX devices.
 */
public enum ObjectPriority
{
   /**
    * System priority. This is the highest priority.
    */
   SYSTEM,

   /**
    * Alarm / urgent priority.
    */
   ALARM,

   /**
    * High priority.
    */
   HIGH,

   /**
    * Low / normal priority. This is the lowest priority.
    */
   LOW;

   /**
    * @return the object for the given ordinal.
    */
   public static ObjectPriority valueOf(int ordinal)
   {
      for (ObjectPriority o : values())
         if (o.ordinal() == ordinal)
            return o;
      return null;
   }
}
