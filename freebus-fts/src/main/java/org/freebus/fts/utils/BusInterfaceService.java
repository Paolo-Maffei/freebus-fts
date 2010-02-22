package org.freebus.fts.utils;

import javax.swing.SwingUtilities;

import org.freebus.fts.core.Config;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.project.internal.I18n;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;

/**
 * Singleton class that holds the default {@link BusInterface} bus interface.
 */
public final class BusInterfaceService
{
   private static BusInterface busInterface;

   /**
    * Returns the default bus-interface. If no default bus-interface exists, one
    * is created. If the creation of the bus-interface fails, an error dialog is
    * shown and null is returned.
    * 
    * @return The default {@link BusInterface} bus-interface, or null if no
    *         bus-interface could be created.
    */
   public synchronized static BusInterface getBusInterface()
   {
      if (busInterface == null)
      {
         try
         {
            createBusInterface();
         }
         catch (final Exception e)
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               @Override
               public void run()
               {
                  Dialogs.showExceptionDialog(e, I18n.getMessage("BusInterfaceService.ErrCreateBusInterface"));
               }
            });

            return null;
         }
      }

      return busInterface;
   }

   /**
    * @return true if the bus interface exists, false if it would be created
    *         when calling {@link #getBusInterface}.
    */
   public synchronized static boolean busInterfaceOpened()
   {
      return busInterface != null;
   }

   /**
    * Create the default bus interface. Automatically called on demand by
    * {@link #getBusInterface}.
    * 
    * @throws Exception
    */
   private static void createBusInterface() throws Exception
   {
      final Config cfg = Config.getInstance();

      // TODO use the configured bus interface type here
      busInterface = BusInterfaceFactory.newSerialInterface(cfg.getStringValue("knxConnectionSerial.port"));

      busInterface.open();
   }

   /**
    * Close the default bus-interface. The bus-interface will be re-created upon
    * the next {@link getBusInterface} call.
    */
   public synchronized void closeBusInterface()
   {
      if (busInterface != null)
      {
         busInterface.close();
         busInterface = null;
      }
   }

   // An instance of this class not be created
   private BusInterfaceService()
   {
      throw new RuntimeException();
   }
}
