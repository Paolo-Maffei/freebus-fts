package org.freebus.knxcomm.application;

import org.freebus.knxcomm.telegram.InvalidDataException;

/**
 * A generic application without data bytes. For application types where no
 * application class exists.
 */
public class GenericApplication implements Application
{
   private final ApplicationType type;

   /**
    * Create an instance for a specific application type.
    *
    * @param type - the application type.
    */
   public GenericApplication(ApplicationType type)
   {
      this.type = type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ApplicationType getType()
   {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void fromRawData(int[] rawData, int start, int length) throws InvalidDataException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int toRawData(int[] rawData, int start)
   {
      rawData[start] = type == null ? 0 : (type.apci & 255);
      return 1;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return type == null ? 0 : type.apci;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;

      if (!(o instanceof GenericApplication))
         return false;

      final GenericApplication oo = (GenericApplication) o;
      return type == oo.type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return type.toString() + " no data";
   }
}
