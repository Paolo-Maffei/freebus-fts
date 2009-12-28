package org.freebus.fts.settings;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.freebus.fts.MainWindow;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.SimpleConfig;
import org.freebus.fts.dialogs.Dialogs;

/**
 * Settings page for user interface settings.
 */
public final class UIPage extends SettingsPage
{
   private static final long serialVersionUID = 1393125890233412334L;
   private final LookAndFeel initialLookAndFeel = UIManager.getLookAndFeel();
   private final JComboBox cboLookAndFeel;

   /**
    * Internal class for look-and-feel combo box entries
    */
   static class LookAndFeelItem
   {
      public final LookAndFeelInfo info;

      public LookAndFeelItem(LookAndFeelInfo info)
      {
         this.info = info;
      }

      @Override
      public String toString()
      {
         return info.getName();
      }
   }

   /**
    * Create a user interface settings page.
    */
   UIPage()
   {
      super(I18n.getMessage("Settings.UIPage.Title"));

      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      final GridBagConstraints c = new GridBagConstraints();
      int gridY = -1;

      final JLabel lblCaption = new JLabel(I18n.getMessage("Settings.UIPage.Caption"));
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
      add(new JLabel(I18n.getMessage("Settings.UIPage.LookAndFeel")), c);

      cboLookAndFeel = new JComboBox();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 10;
      c.gridx = 1;
      c.gridy = gridY;
      add(cboLookAndFeel, c);

      final JPanel spacer = new JPanel();
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1;
      c.weighty = 10;
      c.gridx = 0;
      c.gridy = ++gridY;
      add(spacer, c);

      final String currentLFClassName = UIManager.getLookAndFeel().getClass().getCanonicalName();
      for (LookAndFeelInfo lfInfo : UIManager.getInstalledLookAndFeels())
      {
         cboLookAndFeel.addItem(new LookAndFeelItem(lfInfo));
         System.out.println("- " + lfInfo.getClassName());

         if (currentLFClassName.equals(lfInfo.getClassName()))
            cboLookAndFeel.setSelectedIndex(cboLookAndFeel.getItemCount() - 1);
      }

      cboLookAndFeel.addItemListener(new ItemListener()
      {
         @Override
         public void itemStateChanged(ItemEvent event)
         {
            final LookAndFeelItem item = (LookAndFeelItem) event.getItem();
            if (item == null || item != cboLookAndFeel.getSelectedItem()) return;

            lookAndFeelSelected(item);
         }
      });
   }

   /**
    * Called when the user selects a look-and-feel.
    */
   protected void lookAndFeelSelected(final LookAndFeelItem item)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               UIManager.setLookAndFeel(item.info.getClassName());
               SwingUtilities.updateComponentTreeUI(getParent().getParent());
            }
            catch (Exception e)
            {
               Dialogs.showExceptionDialog(e, I18n.formatMessage("Settings.UIPage.ErrChangeLookAndFeel",
                     new Object[] { item.info.getName() }));
            }
         }
      });
   }

   /**
    * Apply the widget's contents to the running application and the configuration.
    */
   @Override
   public void apply()
   {
      final SimpleConfig cfg = Config.getInstance();
      cfg.put("lookAndFeel", ((LookAndFeelItem) cboLookAndFeel.getSelectedItem()).info.getClassName());

      SwingUtilities.updateComponentTreeUI(MainWindow.getInstance());
   }

   /**
    * Revert the widget's contents changes, if any.
    */
   @Override
   public void revert()
   {
      if (UIManager.getLookAndFeel() != initialLookAndFeel) try
      {
         UIManager.setLookAndFeel(initialLookAndFeel);
         SwingUtilities.updateComponentTreeUI(MainWindow.getInstance());
      }
      catch (UnsupportedLookAndFeelException e)
      {
      }
   }
}
