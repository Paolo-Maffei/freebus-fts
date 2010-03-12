package org.freebus.fts.actions;

import java.awt.event.ActionEvent;

import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.utils.BusInterfaceService;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.telegram.Application;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

/**
 * Send a test telegram to the KNX/EIB bus. This is a development / debugging
 * helper action. The type of the sent telegram changes, depending on what is
 * currently tested.
 */
public final class SendTestTelegramAction extends BasicAction
{
   private static final long serialVersionUID = 5690672834708924069L;

   /** A connect telegram. */
   public static final String CONNECT = "connect";

   /** A connected memory-read telegram. */
   public static final String MEMORY_READ = "memory_read";

   private int sequence = 0;
   private final String telegramType;

   /**
    * Create an action object.
    */
   SendTestTelegramAction(String telegramType)
   {
      super(I18n.getMessage("SendTestTelegramAction.Name"), I18n.getMessage("SendTestTelegramAction.ToolTip"),
            ImageCache.getIcon("icons/music_32ndnote"));

      this.telegramType = telegramType;
   }

   /**
    * Create an action object with the default telegram type.
    */
   SendTestTelegramAction()
   {
      this(CONNECT);
   }

   /**
    * Perform the action.
    */
   @Override
   public void actionPerformed(ActionEvent event)
   {
      final BusInterface bus = BusInterfaceService.getBusInterface();
      if (bus == null) return;

      Telegram telegram = null;

      if (telegramType == CONNECT)
      {
         telegram = new Telegram();
         telegram.setFrom(new PhysicalAddress(1, 1, 255));
         telegram.setDest(new PhysicalAddress(1, 1, 6));
         telegram.setPriority(Priority.SYSTEM);
         telegram.setTransport(Transport.Connect);
         sequence = 0;
      }
      else if (telegramType == MEMORY_READ)
      {
         telegram = new Telegram();
         telegram.setFrom(new PhysicalAddress(1, 1, 255));
         telegram.setDest(new PhysicalAddress(1, 1, 6));
         telegram.setPriority(Priority.SYSTEM);
         telegram.setTransport(Transport.Connected);
         telegram.setSequence(sequence++);
         telegram.setApplication(Application.Memory_Read);
         telegram.setData(new int[] { 0, 0, 0 });
      }
      else
      {
         Dialogs.showErrorDialog("Internal error", "Unknown test-telegram type: " + telegramType);
      }

//      else if (event.widget == toolItemTestMessage3)
//      {
//         telegram = new Telegram();
//         telegram.setFrom(new PhysicalAddress(1, 1, 255));
//         telegram.setDest(new PhysicalAddress(1, 1, 6));
//         telegram.setPriority(Priority.SYSTEM);
//         telegram.setTransport(Transport.Disconnect);
//      }
//      else if (event.widget == toolItemTestMessage4)
//      {
//         telegram = new Telegram();
//         telegram.setFrom(new PhysicalAddress(1, 1, 255));
//         telegram.setDest(new PhysicalAddress(1, 1, 6));
//         telegram.setPriority(Priority.SYSTEM);
//         telegram.setTransport(Transport.Connected);
//         telegram.setSequence(sequence++);
//         telegram.setApplication(Application.Restart);
//         telegram.setData(new int[] { 0 });
//      }

      if (telegram != null) try
      {
         bus.send(telegram);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("SendTestTelegramAction.ErrSendTelegram", new Object[]{ telegramType }));
      }
   }
}
