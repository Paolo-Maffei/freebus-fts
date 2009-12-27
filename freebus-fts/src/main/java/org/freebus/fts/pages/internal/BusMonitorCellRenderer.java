package org.freebus.fts.pages.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.knxcomm.telegram.Telegram;

public final class BusMonitorCellRenderer implements TreeCellRenderer
{
   private static final Icon recvIcon = ImageCache.getIcon("icons/msg_receive");
   private static final Icon sendIcon = ImageCache.getIcon("icons/msg_send");

   private final JPanel renderer;
   private final JLabel lblDirection, lblAppName, lblFrom, lblDest, lblAppData, lblRaw;

   private final DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

   private final Color backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
   private final Color backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();

   /**
    * Create a bus-monitor cell renderer.
    */
   public BusMonitorCellRenderer()
   {
      renderer = new JPanel(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.WEST;
      c.ipadx = 8;
      c.ipady = 2;

      lblDirection = new JLabel();
      c.gridx = 0;
      c.gridy = 0;
      renderer.add(lblDirection, c);

      lblAppName = new JLabel();
      c.gridx = 1;
      c.gridy = 0;
      renderer.add(lblAppName, c);

      lblFrom = new JLabel();
      c.gridx = 2;
      c.gridy = 0;
      renderer.add(lblFrom, c);

      lblDest = new JLabel();
      c.gridx = 3;
      c.gridy = 0;
      renderer.add(lblDest, c);

      lblAppData = new JLabel();
      c.gridx = 4;
      c.gridy = 0;
      renderer.add(lblAppData, c);
      
      lblRaw = new JLabel();
      lblRaw.setForeground(Color.gray);
      c.gridx = 1;
      c.gridwidth = 4;
      c.gridy = 1;
      renderer.add(lblRaw, c);
      c.gridwidth = 1;
   }

   /**
    * Render a tree cell.
    */
   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
         boolean leaf, int row, boolean hasFocus)
   {
      Component returnValue = null;
      if ((value != null) && (value instanceof DefaultMutableTreeNode))
      {
         Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
         if (userObject instanceof BusMonitorItem)
         {
            final BusMonitorItem busMonitorItem = (BusMonitorItem) userObject;
            final Telegram telegram = busMonitorItem.getTelegram();

            lblDirection.setIcon(busMonitorItem.isReceived() ? recvIcon : sendIcon);

            lblAppName.setText(telegram.getApplication().name());
            lblFrom.setText(I18n.formatMessage("BusMonitorCellRenderer.From", new Object[] { telegram.getFrom().toString() }));
            lblDest.setText(I18n.formatMessage("BusMonitorCellRenderer.Dest", new Object[] { telegram.getDest().toString() }));

            final int[] rawData = new int[256];
            final StringBuilder rawDataSB = new StringBuilder(1024);
            final int len = telegram.toRawData(rawData, 0);
            
            for (int i = 0; i < len; ++i)
               rawDataSB.append(String.format("%02x ", rawData[i]));
            
            lblRaw.setText(rawDataSB.toString());

            final int[] appData = telegram.getData();
            final StringBuilder appDataSB = new StringBuilder(256);
            for (int i = 0; i < appData.length; ++i)
               appDataSB.append(String.format("%02x ", appData[i]));

            lblAppData.setText(I18n.formatMessage("BusMonitorCellRenderer.Data", new Object[] { appDataSB.toString() }));
            
            renderer.setBackground(selected ? backgroundSelectionColor : backgroundNonSelectionColor);
            renderer.setEnabled(tree.isEnabled());

            returnValue = renderer;
         }
      }

      if (returnValue == null)
      {
         returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
               hasFocus);
      }

      return returnValue;
   }
}
