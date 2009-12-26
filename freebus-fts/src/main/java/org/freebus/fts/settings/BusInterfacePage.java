package org.freebus.fts.settings;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.knxcomm.KNXConnectionType;

/**
 * Settings page for the bus-interface settings.
 */
public final class BusInterfacePage extends SettingsPage
{
   private static final long serialVersionUID = 88245350928577623L;
   private final JComboBox cboConnectionType;
   private final SerialBusInterface cfgSerial;
   private final EibdBusInterface cfgEibd;
   final JPanel cfgNone;

   /**
    * Create a bus-interface settings page.
    */
   BusInterfacePage()
   {
      super(I18n.getMessage("Settings.BusInterfacePage.Title"));

      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      final JLabel lblCaption = new JLabel(I18n.getMessage("Settings.BusInterfacePage.Caption"));
      lblCaption.setFont(getFont().deriveFont(Font.BOLD).deriveFont(getFont().getSize() * (float) 1.2));
      c.fill = GridBagConstraints.NONE;
      c.gridx = 0;
      c.gridy = ++gridY;
      c.gridwidth = 2;
      c.ipady = 20;
      add(lblCaption, c);
      c.gridwidth = 1;
      c.ipady = 2;

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(new JLabel(I18n.getMessage("Settings.BusInterfacePage.Type")), c);

      cboConnectionType = new JComboBox();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 5;
      c.gridx = 1;
      c.gridy = gridY;
      add(cboConnectionType, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridwidth = 2;
      c.gridx = 0;
      c.gridy = ++gridY;
      c.insets = new Insets(8, 4, 8, 4);
      add(new JSeparator(), c);
      c.gridwidth = 1;

      cfgSerial = new SerialBusInterface();
      cfgEibd = new EibdBusInterface();
      cfgNone = new JPanel();

      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 10;
      c.gridwidth = 2;
      c.gridx = 0;
      c.gridy = ++gridY;
      c.insets = new Insets(0, 4, 0, 4);
      add(cfgNone, c);
      add(cfgSerial, c);
      add(cfgEibd, c);

      cboConnectionType.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            showConnectionDetails();
         }
      });

      final Config cfg = Config.getInstance();

      final String connTypeStr = cfg.get("knxConnectionType");
      for (KNXConnectionType type : KNXConnectionType.values())
      {
         final String typeStr = type.toString();
         final String key = "Settings.BusInterfacePage." + typeStr;
         String label = I18n.getMessage(key);
         if (label.equals(key) || label.isEmpty()) label = type.label;

         cboConnectionType.addItem(label);
         if (typeStr.equals(connTypeStr))
            cboConnectionType.setSelectedIndex(cboConnectionType.getItemCount() - 1);
      }
      showConnectionDetails();
   }

   /**
    * Called when a connection type is selected. Show the connection-details widget.
    */
   protected void showConnectionDetails()
   {
      final KNXConnectionType type = getSelectedConnectionType();
      
      cfgNone.setVisible(type == KNXConnectionType.NONE);
      cfgSerial.setVisible(type == KNXConnectionType.SERIAL_FT12);
      cfgEibd.setVisible(type == KNXConnectionType.EIBD);
   }

   /**
    * @return the connection type of the selected item in the connection-type combobox.
    */
   protected KNXConnectionType getSelectedConnectionType()
   {
      return KNXConnectionType.values()[cboConnectionType.getSelectedIndex()];
   }

   /**
    * Apply the widget's contents to the running application and the configuration.
    */
   @Override
   public void apply()
   {
      final Config cfg = Config.getInstance();
      cfg.put("knxConnectionType", getSelectedConnectionType().toString());

      cfgSerial.apply();
      cfgEibd.apply();
   }

   /**
    * Revert the widget's contents changes, if any.
    */
   @Override
   public void revert()
   {
   }
}
