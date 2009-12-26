package org.freebus.fts.settings;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.persistence.EntityManagerFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.utils.Rot13;

/**
 * A widget for configuring a database connection to a database server.
 */
public final class ServerBasedDatabase extends JPanel
{
   private static final long serialVersionUID = -221274503273310361L;
   private final JTextField inpHost, inpDatabase, inpUser, inpPasswd;
   private final JTextArea txtTestResults;
   private final DriverType driverType;
   private final String configKey;

   /**
    * Create a new serial bus-interface configuration widget.
    */
   public ServerBasedDatabase(DriverType driverType)
   {
      this.driverType = driverType;
      this.configKey = "database" + driverType.toString();

      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(new JLabel(I18n.getMessage("Settings.ServerBasedDatabase.Database")), c);

      inpDatabase = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      add(inpDatabase, c);

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(new JLabel(I18n.getMessage("Settings.ServerBasedDatabase.Host")), c);

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
      add(new JLabel(I18n.getMessage("Settings.ServerBasedDatabase.User")), c);

      inpUser = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      add(inpUser, c);

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(new JLabel(I18n.getMessage("Settings.ServerBasedDatabase.Passwd")), c);

      inpPasswd = new JPasswordField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      add(inpPasswd, c);

      JButton btnTest = new JButton(I18n.getMessage("Settings.ServerBasedDatabase.Test"));
      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 1;
      c.gridy = ++gridY;
      add(btnTest, c);
      btnTest.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            testConnection();
         }
      });

      txtTestResults = new JTextArea();
      txtTestResults.setEditable(false);
      txtTestResults.setLineWrap(true);
      txtTestResults.setBorder(new LineBorder(getBackground(), 3));
      txtTestResults.setVisible(false);
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 10;
      c.weighty = 5;
      c.gridx = 0;
      c.gridy = ++gridY;
      c.gridwidth = 2;
      add(txtTestResults, c);
      c.gridwidth = 1;

      final JPanel spacer = new JPanel();
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 10;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(spacer, c);

      final Config cfg = Config.getInstance();

      String val = cfg.getStringValue(configKey + ".host");
      if (val.isEmpty()) val = "localhost";
      inpHost.setText(val);

      inpUser.setText(cfg.getStringValue(configKey + ".user"));
      inpPasswd.setText(Rot13.rotate(cfg.getStringValue(configKey + ".passwd")));

      val = cfg.getStringValue(configKey + ".database");
      if (val.isEmpty()) val = "fts";
      inpDatabase.setText(val);
   }

   /**
    * Test the database connection.
    */
   protected void testConnection()
   {
      Color borderColor = Color.RED;
      txtTestResults.setVisible(false);

      try
      {
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

         EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(driverType, inpHost.getText() + '/'
               + inpDatabase.getText(), inpUser.getText(), inpPasswd.getText());

         emf.createEntityManager();

         txtTestResults.setText(I18n.getMessage("Settings.ServerBasedDatabase.TestOk"));
         borderColor = new Color(0, 160, 0);
      }
      catch (Exception e)
      {
         e.printStackTrace();

         final String msg = e.getMessage().replace("\n", "\n\n");
         txtTestResults.setText(I18n.getMessage("Settings.ServerBasedDatabase.TestFailed") + "\n\n" + msg);
      }
      finally
      {
         setCursor(Cursor.getDefaultCursor());
      }

      txtTestResults.setBorder(new CompoundBorder(new LineBorder(borderColor, 5),
                                                  new LineBorder(Color.WHITE, 5)));
      txtTestResults.setVisible(true);
   }

   /**
    * Apply the widget's contents to the configuration.
    */
   public void apply()
   {
      final Config cfg = Config.getInstance();
      cfg.put(configKey + ".host", inpHost.getText());
      cfg.put(configKey + ".user", inpUser.getText());
      cfg.put(configKey + ".passwd", Rot13.rotate(inpPasswd.getText()));
      cfg.put(configKey + ".database", inpDatabase.getText());
   }
}
