package org.freebus.fts.client.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.dialogs.settings.BusInterfacePage;
import org.freebus.fts.client.dialogs.settings.DatabasePage;
import org.freebus.fts.client.dialogs.settings.SettingsPage;
import org.freebus.fts.client.dialogs.settings.UIPage;
import org.freebus.fts.elements.components.Dialogs;

/**
 * A dialog for editing the application's settings.
 */
public final class Settings extends JDialog
{
   private static final long serialVersionUID = 4575925737067173245L;
   private final Set<SettingsPage> pages = new HashSet<SettingsPage>();
   private final JTabbedPane tabbedPane;

   /**
    * Create a settings dialog.
    */
   public Settings(JFrame owner)
   {
      super(owner);
      setTitle(I18n.getMessage("Settings.Title"));

      setLayout(new BorderLayout(4, 4));
      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

      setModal(true);
      setMinimumSize(new Dimension(500, 400));
      setSize(new Dimension(700, 600));

      tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
      add(tabbedPane, BorderLayout.CENTER);

      addSettingsPage(new UIPage());
      addSettingsPage(new DatabasePage());
      addSettingsPage(new BusInterfacePage());

      final Box buttonBox = Box.createHorizontalBox();
      buttonBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
      add(buttonBox, BorderLayout.SOUTH);

      buttonBox.add(Box.createHorizontalGlue());

      final JButton btnOk = new JButton(I18n.getMessage("Button.Ok"));
      buttonBox.add(btnOk);

      buttonBox.add(Box.createRigidArea(new Dimension(10, 0)));

      final JButton btnCancel = new JButton(I18n.getMessage("Button.Cancel"));
      buttonBox.add(btnCancel);

      final Dimension buttonSize = new Dimension(100, btnOk.getPreferredSize().height);
      btnOk.setPreferredSize(buttonSize);
      btnCancel.setPreferredSize(buttonSize);

      btnOk.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            accepted();
         }
      });
      
      btnCancel.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            rejected();
         }
      });
   }

   /**
    * Add a settings page to the settings dialog.
    */
   private void addSettingsPage(SettingsPage page)
   {
      pages.add(page);
      tabbedPane.addTab(page.getTitle(), page);
   }

   /**
    * Apply the changes to the configuration and close the dialog.
    */
   private void accepted()
   {
      for (SettingsPage page: pages)
      {
         try
         {
            page.apply();
         }
         catch (Exception e)
         {
            Dialogs.showExceptionDialog(e, I18n.getMessage("Settings.ErrApply"));
         }
      }

      Config.getInstance().save();
      dispose();
   }

   /**
    * Revert any changes to the configuration and close the dialog.
    */
   private void rejected()
   {
      for (SettingsPage page: pages)
         page.revert();

      dispose();
   }
}
