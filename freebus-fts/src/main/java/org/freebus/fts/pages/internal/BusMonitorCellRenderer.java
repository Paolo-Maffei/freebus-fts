package org.freebus.fts.pages.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.freebus.knxcomm.telegram.Telegram;

public final class BusMonitorCellRenderer implements TreeCellRenderer
{
   private final JPanel renderer;
   private final JLabel titleLabel;
   private final JLabel authorsLabel;
   private final JLabel priceLabel;

   private final DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

   private final Color backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
   private final Color backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();

   public BusMonitorCellRenderer()
   {
      renderer = new JPanel(new GridLayout(0, 1));

      titleLabel = new JLabel(" ");
      titleLabel.setForeground(Color.blue);
      renderer.add(titleLabel);

      authorsLabel = new JLabel(" ");
      authorsLabel.setForeground(Color.blue);
      renderer.add(authorsLabel);

      priceLabel = new JLabel(" ");
      priceLabel.setHorizontalAlignment(JLabel.RIGHT);
      priceLabel.setForeground(Color.red);
      renderer.add(priceLabel);
      renderer.setBorder(BorderFactory.createLineBorder(Color.black));
   }

   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
         boolean leaf, int row, boolean hasFocus)
   {
      Component returnValue = null;
      if ((value != null) && (value instanceof DefaultMutableTreeNode))
      {
         Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
         if (userObject instanceof Telegram)
         {
            final Telegram telegram = (Telegram) userObject;
            titleLabel.setText(telegram.getDest().toString());
            authorsLabel.setText(telegram.getApplication().toString());
            priceLabel.setText(telegram.getFrom().toString());

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
