package org.freebus.fts.client.dialogs.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.persistence.EntityManagerFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;

/**
 * A widget for configuring a database connection to a database server.
 */
final class ServerBasedDatabase extends DatabaseDriverPage
{
   private static final long serialVersionUID = -221274503273310361L;
   private final JTextField inpHost, inpDatabase, inpUser, inpPasswd;

   /**
    * Create a new serial bus-interface configuration widget.
    */
   protected ServerBasedDatabase(DriverType driverType)
   {
      super(driverType);

      final JPanel base = getConfigPanel();
      GridBagLayout layout = new GridBagLayout();
      base.setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      c.insets = new Insets(4, 4, 2, 2);
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

      final ConnectionDetails conDetails = new ConnectionDetails();
      conDetails.fromConfig(Config.getInstance(), driverType);

      inpDatabase.setText(conDetails.getDbName());
      inpUser.setText(conDetails.getUser());
      inpPasswd.setText(conDetails.getPassword());

      String val = conDetails.getHost();
      if (val.isEmpty()) val = "localhost";
      inpHost.setText(val);
   }

   /**
    * Try to open a database connection.
    */
   @Override
   protected void connectNow() throws Exception
   {
      final ConnectionDetails conDetails = new ConnectionDetails(getDriverType(), inpDatabase.getText(), inpHost
            .getText(), inpUser.getText(), inpPasswd.getText());

      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("connect-test", conDetails);
      emf.createEntityManager().close();
      emf.close();
   }

   /**
    * Apply the widget's contents to the configuration.
    */
   @Override
   public void apply()
   {
      final ConnectionDetails conDetails = new ConnectionDetails();

      conDetails.setType(getDriverType());
      conDetails.setDbName(inpDatabase.getText());
      conDetails.setHost(inpHost.getText());
      conDetails.setUser(inpUser.getText());
      conDetails.setPassword(inpPasswd.getText());

      conDetails.toConfig(Config.getInstance());
   }
}
