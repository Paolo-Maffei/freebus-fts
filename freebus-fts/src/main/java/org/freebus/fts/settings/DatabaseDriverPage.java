package org.freebus.fts.settings;

import java.awt.BorderLayout;
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

import org.freebus.fts.common.db.DriverType;
import org.freebus.fts.core.I18n;

/**
 * Abstract base class for database-driver specific settings pages.
 */
public abstract class DatabaseDriverPage extends JPanel
{
   private static final long serialVersionUID = -4422819869584669896L;

   private final DriverType driverType;
   private final JPanel configPanel, testPanel;
   private final JTextArea txtTestResults;

   /**
    * Create a database-driver settings page.
    */
   DatabaseDriverPage(DriverType driverType)
   {
      this.driverType = driverType;

      setLayout(new BorderLayout(0, 8));

      configPanel = new JPanel();
      add(configPanel, BorderLayout.CENTER);

      testPanel = new JPanel();
      testPanel.setLayout(new GridBagLayout());
      add(testPanel, BorderLayout.SOUTH);
      
      JButton btnTest = new JButton(I18n.getMessage("Settings.DatabaseDriverPage.Test"));
      testPanel.add(btnTest, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8, 2, 8, 2), 0, 0));
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
      testPanel.add(txtTestResults, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

      testPanel.add(new JPanel(), new GridBagConstraints(0, 2, 1, 1, 10, 10, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
   }

   /**
    * @return The {@link JPanel} panel, which is the parent of the configuration options.
    */
   public JPanel getConfigPanel()
   {
      return configPanel;
   }

   /**
    * @return The {@link JPanel} panel, which is the parent of the test GUI elements.
    */
   public JPanel getTestPanel()
   {
      return testPanel;
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
    * Test the database connection. Calls {@link connectNow}.
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
                                                  new EmptyBorder(5, 5, 5, 5)));
      txtTestResults.setVisible(true);

   }

   /**
    * Does the actual connecting. Called by {@link connectTest}.
    */
   protected abstract void connectNow() throws Exception;
}
