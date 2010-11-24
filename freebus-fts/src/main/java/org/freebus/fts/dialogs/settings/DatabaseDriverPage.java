package org.freebus.fts.dialogs.settings;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.persistence.db.DriverType;

/**
 * Abstract base class for database-driver specific settings pages.
 */
abstract class DatabaseDriverPage extends JPanel
{
   private static final long serialVersionUID = -4422819869584669896L;

   private final DriverType driverType;
   private final JPanel configPanel;
   private final JTextArea txtTestResults;

   /**
    * Create a database-driver settings page.
    */
   DatabaseDriverPage(DriverType driverType)
   {
      this.driverType = driverType;

      setLayout(new GridBagLayout());

      configPanel = new JPanel();
      add(configPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(8, 2, 8, 2), 0, 0));

      JButton btnTest = new JButton(I18n.getMessage("Settings.DatabaseDriverPage.Test"));
      add(btnTest, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(8, 2, 8, 2), 0, 0));
      btnTest.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            connectTest();
         }
      });

      txtTestResults = new JTextArea();
      txtTestResults.setEditable(false);
      txtTestResults.setLineWrap(true);
      txtTestResults.setOpaque(false);
      add(txtTestResults, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(8, 2, 8, 2), 0, 0));

      add(new JPanel(), new GridBagConstraints(0, 3, 1, 1, 1, 100, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
   }

   /**
    * @return The {@link JPanel} panel, which is the parent of the configuration options.
    */
   public JPanel getConfigPanel()
   {
      return configPanel;
   }

   /**
    * @return The database driver type.
    */
   public final DriverType getDriverType()
   {
      return driverType;
   }

   /**
    * Apply the configuration to the settings object.
    */
   public abstract void apply();

   /**
    * Test the database connection.
    */
   public final void connectTest()
   {
      Color borderColor = Color.RED;
      txtTestResults.setVisible(false);

      try
      {
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

         connectNow();

         txtTestResults.setText(I18n.getMessage("Settings.DatabaseDriverPage.TestOk"));
         borderColor = new Color(0, 160, 0);
      }
      catch (Exception e)
      {
         e.printStackTrace();

         final String msg = e.getMessage().replace("\n", "\n\n");
         txtTestResults.setText(I18n.getMessage("Settings.DatabaseDriverPage.TestFailed") + "\n\n" + msg);
      }
      finally
      {
         setCursor(Cursor.getDefaultCursor());
      }

      txtTestResults.setBorder(new CompoundBorder(new LineBorder(borderColor, 5),
                                                  new EmptyBorder(8, 8, 8, 8)));
      txtTestResults.setVisible(true);

   }

   /**
    * Does the actual connecting. Called by {@link #connectTest()}.
    */
   protected abstract void connectNow() throws Exception;
}
