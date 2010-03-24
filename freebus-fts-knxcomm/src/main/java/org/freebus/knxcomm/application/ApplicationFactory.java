package org.freebus.knxcomm.application;

import org.freebus.knxcomm.telegram.Telegram;

/**
 * Factory class for {@link Application} application instances.
 *
 * @see {@link Application}
 * @see {@link Telegram}
 */
public final class ApplicationFactory
{
   /**
    * Create an {@link Application application} instance for a specific
    * application type.
    * <p>
    * If no application class exists for the application type, a
    * {@link GenericDataApplication} object is created if the application type
    * can contain data bytes, or a {@link GenericDataApplication} object is created
    * if the application type cannot contain data bytes.
    *
    * @param type - the application type.
    *
    * @return the created application object.
    */
   public static Application createApplication(final ApplicationType type)
   {
      if (type.clazz == null)
      {
         if (type.maxData == 0)
            return new GenericApplication(type);
         return new GenericDataApplication(type);
      }
      try
      {
         return type.clazz.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }
   }

   /*
    * Disabled
    */
   private ApplicationFactory()
   {
   }
}
