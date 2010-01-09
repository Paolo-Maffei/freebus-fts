package org.freebus.fts.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import org.freebus.fts.common.db.DatabaseResources;
import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.Environment;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.SimpleConfig;
import org.freebus.fts.utils.Rot13;

/**
 * A widget for configuring a serial bus interface connection.
 */
public final class FileBasedDatabase extends DatabaseDriverPage
{
   private static final long serialVersionUID = 789090230895L;
   private final JTextField inpDatabase, inpUser, inpPasswd;
   private final String configKey;

   /**
    * Create a new serial bus-interface configuration widget.
    */
   public FileBasedDatabase(DriverType driverType)
   {
      super(driverType);
      configKey = "database" + driverType.toString();

      final JPanel base = getConfigPanel();
      base.setLayout(new GridBagLayout());

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

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

      final JButton btnSelDatabase = new JButton("...");
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

      final SimpleConfig cfg = Config.getInstance();

      String val = cfg.getStringValue(configKey + ".database");
      if (val.isEmpty()) val = Environment.getAppDir() + "/db";
      inpDatabase.setText(val);

      val = cfg.getStringValue(configKey + ".user");
      if (val.isEmpty()) val = "sa";
      inpUser.setText(val);

      inpPasswd.setText(Rot13.rotate(cfg.getStringValue(configKey + ".passwd")));
   }

   /**
    * Open a file chooser and let the user select the database file.
    */
   protected void selectDatabase()
   {
      String cur = inpDatabase.getText();
      if (cur.isEmpty()) cur = Environment.getAppDir() + "/db";

      final JFileChooser dlg = new JFileChooser();
      dlg.setSelectedFile(new File(cur));

      if (dlg.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
         inpDatabase.setText(dlg.getSelectedFile().getAbsolutePath());
   }

   /**
    * Try to open a database connection.
    */
   protected void connectNow() throws Exception
   {
      EntityManagerFactory emf = DatabaseResources.createEntityManagerFactory(getDriverType(), inpDatabase.getText(),
            inpUser.getText(), inpPasswd.getText());

      emf.createEntityManager().close();
   }

   /**
    * Apply the widget's contents to the configuration.
    */
   @Override
   public void apply()
   {
      final SimpleConfig cfg = Config.getInstance();
      cfg.put(configKey + ".database", inpDatabase.getText());
      cfg.put(configKey + ".user", inpUser.getText());
      cfg.put(configKey + ".passwd", Rot13.rotate(inpPasswd.getText()));
   }
}
