package org.freebus.fts.client.dialogs.settings;

import java.awt.BorderLayout;

import org.freebus.fts.client.core.I18n;
import org.freebus.knxcomm.gui.settings.BusInterfacePanel;

/**
 * Settings page for the bus-interface settings.
 */
public final class BusInterfacePage extends SettingsPage
{
   private static final long serialVersionUID = 9215310928577623L;
   private BusInterfacePanel panel = new BusInterfacePanel();

   /**
    * Create a bus-interface settings page.
    */
   public BusInterfacePage()
   {
      super(I18n.getMessage("Settings.BusInterfacePage.Title"));

      setLayout(new BorderLayout());
      add(panel, BorderLayout.CENTER);
   }

   /**
    * Apply the widget's contents to the running application and the configuration.
    */
   @Override
   public void apply()
   {
      panel.apply();
   }

   /**
    * Revert the widget's contents changes, if any.
    */
   @Override
   public void revert()
   {
      panel.revert();
   }
}
