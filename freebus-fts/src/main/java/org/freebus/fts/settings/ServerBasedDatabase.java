package org.freebus.fts.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.persistence.EntityManagerFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.SimpleConfig;
import org.freebus.fts.utils.Rot13;

/**
 * A widget for configuring a database connection to a database server.
 */
public final class ServerBasedDatabase extends DatabaseDriverPage
{
   private static final long serialVersionUID = -221274503273310361L;
   private final JTextField inpHost, inpDatabase, inpUser, inpPasswd;
   private final String configKey;

   /**
    * Create a new serial bus-interface configuration widget.
    */
   public ServerBasedDatabase(DriverType driverType)
   {
      super(driverType);
      this.configKey = "database" + driverType.toString();

      final JPanel base = getConfigPanel();
      GridBagLayout layout = new GridBagLayout();
      base.setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      base.add(new JLabel(I18n.getMessage("Settings.ServerBasedDatabase.Database")), c);

      inpDatabase = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      base.add(inpDatabase, c);

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      base.add(new JLabel(I18n.getMessage("Settings.ServerBasedDatabase.Host")), c);

      inpHost = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      base.add(inpHost, c);

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      base.add(new JLabel(I18n.getMessage("Settings.ServerBasedDatabase.User")), c);

      inpUser = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      base.add(inpUser, c);

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      base.add(new JLabel(I18n.getMessage("Settings.ServerBasedDatabase.Passwd")), c);

      inpPasswd = new JPasswordField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      base.add(inpPasswd, c);

      final SimpleConfig cfg = Config.getInstance();

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
    * Try to open a database connection.
    */
   protected void connectNow() throws Exception
   {
      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(getDriverType(), inpHost.getText() + '/'
            + inpDatabase.getText(), inpUser.getText(), inpPasswd.getText());

      emf.createEntityManager().close();
   }

   /**
    * Apply the widget's contents to the configuration.
    */
   @Override
   public void apply()
   {
      final SimpleConfig cfg = Config.getInstance();
      cfg.put(configKey + ".host", inpHost.getText());
      cfg.put(configKey + ".user", inpUser.getText());
      cfg.put(configKey + ".passwd", Rot13.rotate(inpPasswd.getText()));
      cfg.put(configKey + ".database", inpDatabase.getText());
   }
}
