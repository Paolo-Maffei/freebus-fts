package org.freebus.fts.client.editors.scanner;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.elements.services.ImageCache;

/**
 * A panel for setting the scan parameters for the {@link Scanner device scanner editor}.
 */
public class ScanParamsPanel extends JPanel
{
   private static final long serialVersionUID = 4524870447464740973L;

   private final JComboBox areaAddrCombo, lineAddrCombo, minDeviceAddrCombo, maxDeviceAddrCombo;
   private final JButton startStopButton = new JButton(I18n.getMessage("Scanner.Start"));

   /**
    * Create the scan parameter panel.
    * 
    * @param configPrefix - the prefix for persisting the configuration settings
    */
   public ScanParamsPanel(final String configPrefix)
   {

      final Config cfg = Config.getInstance();
      final Insets noInsets = new Insets(0, 0, 0, 0);
      final Insets stdInsets = new Insets(2, 2, 2, 2);
      final int anchorW = GridBagConstraints.WEST;
      JLabel lbl;
      int col = -1;

      lbl = new JLabel(ImageCache.getIcon("icons-large/find"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));

      lbl = new JLabel(I18n.getMessage("Scanner.ScanArea"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));

      areaAddrCombo = createNumbersComboBox(0, 15);
      areaAddrCombo.setMaximumRowCount(16);
      areaAddrCombo.setSelectedItem(cfg.getIntValue(configPrefix + ".areaAddr", 0));
      add(areaAddrCombo, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));
      areaAddrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Config.getInstance().put(configPrefix + ".areaAddr", (Integer) areaAddrCombo.getSelectedItem());
         }
      });

      lbl = new JLabel(I18n.getMessage("Scanner.ScanLine"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, noInsets, 0, 0));

      lineAddrCombo = createNumbersComboBox(0, 15);
      lineAddrCombo.setMaximumRowCount(16);
      lineAddrCombo.setSelectedItem(cfg.getIntValue(configPrefix + ".lineAddr", 0));
      add(lineAddrCombo, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));
      lineAddrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            Config.getInstance().put(configPrefix + ".lineAddr", (Integer) lineAddrCombo.getSelectedItem());
         }
      });

      lbl = new JLabel(I18n.getMessage("Scanner.FromAddr"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, new Insets(2, 20, 2, 2), 0, 0));

      minDeviceAddrCombo = createNumbersComboBox(0, 255);
      minDeviceAddrCombo.setMaximumRowCount(32);
      minDeviceAddrCombo.setSelectedItem(cfg.getIntValue(configPrefix + ".deviceMinAddr", 0));
      add(minDeviceAddrCombo, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));
      minDeviceAddrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            int minValue = (Integer) minDeviceAddrCombo.getSelectedItem();
            int maxValue = (Integer) maxDeviceAddrCombo.getSelectedItem();
            Config.getInstance().put(configPrefix + ".deviceMinAddr", minValue);
            if (minValue > maxValue)
               maxDeviceAddrCombo.setSelectedItem(minValue);
         }
      });

      lbl = new JLabel(I18n.getMessage("Scanner.ToAddr"));
      add(lbl, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));

      maxDeviceAddrCombo = createNumbersComboBox(0, 255);
      maxDeviceAddrCombo.setMaximumRowCount(32);
      maxDeviceAddrCombo.setSelectedItem(cfg.getIntValue(configPrefix + ".deviceMaxAddr", 255));
      add(maxDeviceAddrCombo, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, stdInsets, 0, 0));
      maxDeviceAddrCombo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            int minValue = (Integer) minDeviceAddrCombo.getSelectedItem();
            int maxValue = (Integer) maxDeviceAddrCombo.getSelectedItem();
            Config.getInstance().put(configPrefix + ".deviceMaxAddr", maxValue);
            if (maxValue < minValue)
               minDeviceAddrCombo.setSelectedItem(maxValue);
         }
      });

      add(startStopButton, new GridBagConstraints(++col, 0, 1, 1, 1, 1, anchorW, GridBagConstraints.NONE, new Insets(2, 40, 2, 2), 0, 0));
      startStopButton.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            cfg.put(configPrefix + ".areaAddr", getAreaAddr());
            cfg.put(configPrefix + ".lineAddr", getLineAddr());
            cfg.put(configPrefix + ".deviceMinAddr", getMinDeviceAddr());
            cfg.put(configPrefix + ".deviceMaxAddr", getMaxDeviceAddr());
            cfg.save();
         }
      });

      add(Box.createHorizontalGlue(), new GridBagConstraints(++col, 0, 1, 1, 100, 1, anchorW,
            GridBagConstraints.HORIZONTAL, noInsets, 0, 0));
   }

   /**
    * Add an action listener to the start/stop button.
    *
    * @param listener - the action listener to add.
    */
   public void addStartStopActionListener(ActionListener listener)
   {
      startStopButton.addActionListener(listener);
   }

   /**
    * @return The selected area address.
    */
   public int getAreaAddr()
   {
      return (Integer) areaAddrCombo.getSelectedItem();
   }

   /**
    * @return The selected line address.
    */
   public int getLineAddr()
   {
      return (Integer) lineAddrCombo.getSelectedItem();
   }

   /**
    * @return The selected minimum device address.
    */
   public int getMinDeviceAddr()
   {
      return (Integer) minDeviceAddrCombo.getSelectedItem();
   }

   /**
    * @return The selected maximum device address.
    */
   public int getMaxDeviceAddr()
   {
      return (Integer) maxDeviceAddrCombo.getSelectedItem();
   }

   /**
    * Toggle whether the scanning is in progress or not. This changes
    * the text of the start/stop button.
    */
   public void setScanning(boolean scanning)
   {
      if (scanning)
      {
         startStopButton.setCursor(Cursor.getDefaultCursor());
         startStopButton.setText(I18n.getMessage("Scanner.Stop"));
      }
      else
      {
         startStopButton.setText(I18n.getMessage("Scanner.Start"));
      }
   }
   
   /**
    * Create a {@link JComboBox combo-box} with numbers from minValue to maxValue
    * (including minValue and maxValue).
    *
    * @param minValue - the minimum value.
    * @param maxValue - the maximum value.
    *
    * @return The created combo-box.
    */
   private JComboBox createNumbersComboBox(int minValue, int maxValue)
   {
      final JComboBox combo = new JComboBox();

      for (int value = minValue; value <= maxValue; ++value)
         combo.addItem(value);

      return combo;
   }
}
