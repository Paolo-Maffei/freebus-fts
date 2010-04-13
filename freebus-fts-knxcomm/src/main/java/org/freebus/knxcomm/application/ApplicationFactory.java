package org.freebus.knxcomm.application;

import org.freebus.knxcomm.telegram.Telegram;

/**
 * Factory class for {@link Application} application instances.
 *
 * @see Application
 * @see Telegram
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
    * @param type - the {@link ApplicationType application type}.
    *
    * @return the created application object.
    */
   public static Application createApplication(final ApplicationType type)
   {
      final Class<? extends Application> appClass = type.getApplicationClass();

      if (appClass == null)
      {
         if (type.getMaxDataBytes() == 0)
            return new GenericApplication(type);
         return new GenericDataApplication(type);
      }

      try
      {
         return appClass.newInstance();
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
