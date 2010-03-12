package org.freebus.fts.dialogs.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freebus.fts.common.SimpleConfig;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.knxcomm.serial.SerialPortUtil;

/**
 * A widget for configuring a serial bus interface connection.
 */
final class SerialBusInterface extends JPanel
{
   private static final long serialVersionUID = 1457255663125616282L;
   private final static String configKey = "knxConnectionSerial";
   private final JComboBox cboPort;

   /**
    * Create a new serial bus-interface configuration widget.
    */
   public SerialBusInterface()
   {
      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(new JLabel(I18n.getMessage("Settings.SerialBusInterface.Port")), c);

      cboPort = new JComboBox();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      add(cboPort, c);

      final JPanel spacer = new JPanel();
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 10;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(spacer, c);


      final SimpleConfig cfg = Config.getInstance();
      final String cfgPortName = cfg.get(configKey + ".port");

      for (final String portName: SerialPortUtil.getPortNames())
      {
         cboPort.addItem(portName);
         if (portName.equals(cfgPortName)) cboPort.setSelectedIndex(cboPort.getItemCount() - 1);
      }
   }

   /**
    * Apply the widget's contents to the configuration.
    */
   protected void apply()
   {
      final SimpleConfig cfg = Config.getInstance();
      cfg.put(configKey + ".port", (String) cboPort.getSelectedItem());
   }
}
