package org.freebus.fts.dialogs.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.persistence.EntityManagerFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.common.Environment;
import org.freebus.fts.persistence.db.ConnectionDetails;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.persistence.db.DriverType;

/**
 * A widget for configuring a serial bus interface connection.
 */
final class FileBasedDatabase extends DatabaseDriverPage
{
   private static final long serialVersionUID = 789090230895L;
   private final JTextField inpDatabase, inpUser, inpPasswd;

   /**
    * Create a new serial bus-interface configuration widget.
    */
   protected FileBasedDatabase(DriverType driverType)
   {
      super(driverType);

      final JPanel base = getConfigPanel();
      base.setLayout(new GridBagLayout());

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      c.insets = new Insets(4, 4, 2, 2);
      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      base.add(new JLabel(I18n.getMessage("Settings.FileBasedDatabase.Database")), c);

      inpDatabase = new JTextField();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      base.add(inpDatabase, c);

      final JButton btnSelDatabase = new JButton(" ... ");
      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 2;
      c.gridy = gridY;
      base.add(btnSelDatabase, c);
      btnSelDatabase.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            selectDatabase();
         }
      });

      c.fill = GridBagConstraints.NONE;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = ++gridY;
      base.add(new JLabel(I18n.getMessage("Settings.FileBasedDatabase.User")), c);

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
      base.add(new JLabel(I18n.getMessage("Settings.FileBasedDatabase.Passwd")), c);

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
   }

   /**
    * Open a file chooser and let the user select the database file.
    */
   private void selectDatabase()
   {
      String cur = inpDatabase.getText();
      if (cur.isEmpty())
         cur = Environment.getAppDir() + "/db";

      final JFileChooser dlg = new JFileChooser();
      dlg.setSelectedFile(new File(cur));

      if (dlg.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
         inpDatabase.setText(dlg.getSelectedFile().getAbsolutePath());
   }

   /**
    * Try to open a database connection.
    */
   @Override
   protected void connectNow() throws Exception
   {
      final ConnectionDetails conDetails = new ConnectionDetails(getDriverType(), inpDatabase.getText(), "", inpUser
            .getText(), inpPasswd.getText());

      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory("connect-test", conDetails);
      emf.createEntityManager().close();
      emf.close();
   }

   /**
    * Apply the frame's contents to the configuration.
    */
   @Override
   public void apply()
   {
      final ConnectionDetails conDetails = new ConnectionDetails();

      conDetails.setType(getDriverType());
      conDetails.setDbName(inpDatabase.getText());
      conDetails.setUser(inpUser.getText());
      conDetails.setPassword(inpPasswd.getText());

      conDetails.toConfig(Config.getInstance());
   }
}
