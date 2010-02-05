package org.freebus.fts.dialogs.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.persistence.SimpleConfig;

/**
 * A widget for configuring an eibd bus interface connection.
 */
public final class EibdBusInterface extends JPanel
{
   private static final long serialVersionUID = 1457255663125616282L;
   private final static String configKey = "knxConnectionEibd";
   private final static int defaultEibdPort = 6720;
   private final JTextField inpHost, inpPort;

   /**
    * Create a new eibd bus-interface configuration widget.
    */
   public EibdBusInterface()
   {
      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(new JLabel(I18n.getMessage("Settings.EibdBusInterface.Host")), c);

      inpHost = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      add(inpHost, c);

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(new JLabel(I18n.formatMessage("Settings.EibdBusInterface.Port", new String[] { Integer.toString(defaultEibdPort) })), c);

      inpPort = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      add(inpPort, c);

      final JPanel spacer = new JPanel();
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 10;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(spacer, c);

      final SimpleConfig cfg = Config.getInstance();
      inpHost.setText(cfg.getStringValue(configKey + ".host"));
      inpHost.setText(cfg.getStringValue(configKey + ".port"));
   }

   /**
    * Apply the widget's contents to the configuration.
    */
   public void apply()
   {
      final SimpleConfig cfg = Config.getInstance();
      cfg.put(configKey + ".host", inpHost.getText());
      cfg.put(configKey + ".port", inpPort.getText());
   }
}
