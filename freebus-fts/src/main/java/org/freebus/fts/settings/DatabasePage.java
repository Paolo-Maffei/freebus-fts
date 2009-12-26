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

import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;

/**
 * Settings page for the database settings.
 */
public final class DatabasePage extends SettingsPage
{
   private static final long serialVersionUID = 882434523577623L;
   private final JComboBox cboConnectionType;
   private final FileBasedDatabase cfgHSQL;
   private final ServerBasedDatabase cfgMYSQL;
   final JPanel cfgNone;

   /**
    * Create a database settings page.
    */
   DatabasePage()
   {
      super(I18n.getMessage("Settings.DatabasePage.Title"));

      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      final JLabel lblCaption = new JLabel(I18n.getMessage("Settings.DatabasePage.Caption"));
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
      add(new JLabel(I18n.getMessage("Settings.DatabasePage.Type")), c);

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

      cfgNone = new JPanel();
      cfgHSQL = new FileBasedDatabase(DriverType.HSQL);
      cfgMYSQL = new ServerBasedDatabase(DriverType.MYSQL);

      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 10;
      c.gridwidth = 2;
      c.gridx = 0;
      c.gridy = ++gridY;
      c.insets = new Insets(0, 4, 0, 4);
      add(cfgNone, c);
      add(cfgHSQL, c);
      add(cfgMYSQL, c);

      cboConnectionType.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            showConnectionDetails();
         }
      });

      final Config cfg = Config.getInstance();

      final String connTypeStr = cfg.get("databaseDriverType");
      for (DriverType type : DriverType.values())
      {
         final String typeStr = type.toString();
         final String key = "Settings.DatabasePage." + typeStr;
         String label = I18n.getMessage(key);
         if (label.equals(key) || label.isEmpty()) label = typeStr;

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
      final DriverType type = getSelectedDriverType();

      cfgNone.setVisible(type == DriverType.None);
      cfgHSQL.setVisible(type == DriverType.HSQL);
      cfgMYSQL.setVisible(type == DriverType.MYSQL);
   }

   /**
    * @return the connection type of the selected item in the connection-type combo-box.
    */
   protected DriverType getSelectedDriverType()
   {
      return DriverType.values()[cboConnectionType.getSelectedIndex()];
   }

   /**
    * Apply the widget's contents to the running application and the configuration.
    */
   @Override
   public void apply()
   {
      final Config cfg = Config.getInstance();
      cfg.put("databaseDriverType", getSelectedDriverType().toString());

      cfgHSQL.apply();
      cfgMYSQL.apply();
   }

   /**
    * Revert the widget's contents changes, if any.
    */
   @Override
   public void revert()
   {
   }
}
