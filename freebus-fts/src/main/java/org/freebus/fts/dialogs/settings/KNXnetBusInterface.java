package org.freebus.fts.dialogs.settings;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.common.SimpleConfig;
import org.freebus.knxcomm.link.netip.KNXnetLink;
import org.freebus.knxcomm.types.LinkMode;

/**
 * A widget for configuring a KNXnet/IP bus interface connection.
 */
final class KNXnetBusInterface extends JPanel
{
   private static final long serialVersionUID = 1457255663125616282L;
   private final static String configKey = "knxConnectionKNXnet";
   private final JTextField inpHost, inpPort;
   private final JLabel lblTestOutput;

   /**
    * Create a new KNXnet/IP bus-interface configuration widget.
    */
   public KNXnetBusInterface()
   {
      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(2, 2, 2, 2);

      int gridY = -1;

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(new JLabel(I18n.getMessage("Settings.KNXnetBusInterface.Host")), c);

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
      add(new JLabel(I18n.formatMessage("Settings.KNXnetBusInterface.Port", new String[] { Integer
            .toString(KNXnetLink.defaultPortUDP) })), c);

      inpPort = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      add(inpPort, c);

      c.gridx = 0;
      c.weightx = 1;
      c.gridy = ++gridY;
      add(Box.createVerticalStrut(8), c);

      final JButton btnTest = new JButton(I18n.getMessage("Settings.KNXnetBusInterface.Test"));
      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      c.gridwidth = 2;
      add(btnTest, c);
      c.gridwidth = 1;
      btnTest.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            testConnection();
         }
      });

      lblTestOutput = new JLabel();
      lblTestOutput.setAlignmentY(0.1f);
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1;
      c.weighty = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      c.gridwidth = 2;
      c.insets = new Insets(8, 8, 40, 40);
      add(lblTestOutput, c);
      c.gridwidth = 1;

      c.gridy = ++gridY;
      c.weighty = 2;
      c.fill = GridBagConstraints.VERTICAL;
      add(Box.createVerticalGlue(), c);

      final SimpleConfig cfg = Config.getInstance();

      String host = cfg.getStringValue(configKey + ".host");
      if (host == null || host.isEmpty())
         host = "localhost";
      inpHost.setText(host);

      int port = cfg.getIntValue(configKey + ".port");
      if (port < 1)
         port = KNXnetLink.defaultPortUDP;
      inpPort.setText(Integer.toString(port));
   }

   /**
    * Test the connection to the KNXnet/IP server using the values in the input
    * fields.
    */
   protected void testConnection()
   {
      try
      {
         lblTestOutput.setText(I18n.getMessage("Settings.KNXnetBusInterface.TestConnecting"));
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

         KNXnetLink link = new KNXnetLink(inpHost.getText(), Integer.valueOf(inpPort.getText()));
         link.open(LinkMode.LinkLayer);

         final StringBuffer sb = new StringBuffer();

         sb.append("<html><body><h3>");
         sb.append(I18n.getMessage("Settings.KNXnetBusInterface.TestOk"));
         sb.append("</h3>");
         sb.append("</body></html>");

         lblTestOutput.setText(sb.toString());

         link.close();
      }
      catch (Exception e)
      {
         final StringBuffer sb = new StringBuffer();

         sb.append("<html><body><h3>");
         sb.append(I18n.getMessage("Settings.KNXnetBusInterface.TestFailed"));
         sb.append("</h3><br />");
         sb.append(e.toString());
         sb.append("</body></html>");

         lblTestOutput.setText(sb.toString());
      }
      finally
      {
         setCursor(Cursor.getDefaultCursor());
      }
   }

   /**
    * Apply the widget's contents to the configuration.
    */
   protected void apply()
   {
      final SimpleConfig cfg = Config.getInstance();
      cfg.put(configKey + ".host", inpHost.getText());
      cfg.put(configKey + ".port", inpPort.getText());
   }
}
