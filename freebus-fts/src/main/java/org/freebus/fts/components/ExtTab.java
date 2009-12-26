package org.freebus.fts.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import org.freebus.fts.core.ImageCache;

/**
 * A tab of a tab-page that can contain an icon and a close button.
 */
class ExtTab extends JPanel
{
   private static final long serialVersionUID = 600126632738870853L;
   private static final Icon closeButtonIcon = ImageCache.getIcon("icons/tab-close");
   private static final Icon closeButtonHighliteIcon = ImageCache.getIcon("icons/tab-close-highlite");
   private static final Icon closeButtonDimmedIcon = ImageCache.getIcon("icons/tab-close-dimmed");
   private final JTabbedPane pane;
   private final TabButton closeButton;

   /**
    * Create a flexi-tab object.
    * 
    * @param title - the title of the tab.
    * @param icon - an optional icon.
    * @param closable - true if the tab shall have a close button.
    */
   public ExtTab(String title, JTabbedPane pane, Icon icon, boolean closable)
   {
      super(new BorderLayout(4, 0));
//      setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.pane = pane;

      setOpaque(false);

      add(new JLabel(title), BorderLayout.CENTER);
      if (icon != null) add(new JLabel(icon), BorderLayout.WEST);

      if (closable)
      {
         closeButton = new TabButton();
         add(closeButton, BorderLayout.EAST);
      }
      else
      {
         closeButton = null;
      }
   }

   /**
    * Create a flexi-tab object.
    * 
    * @param title - the title of the tab.
    * @param closable - true if the tab shall have a close button.
    */
   public ExtTab(String title, JTabbedPane pane, boolean closable)
   {
      this(title, pane, null, closable);
   }

   private class TabButton extends JButton implements ActionListener
   {
      private static final long serialVersionUID = -1335643238071282239L;

      public TabButton()
      {
         int size = 17;
         setPreferredSize(new Dimension(size, size));
         // setToolTipText(I18n.getMessage("ExtTab.CloseTip"));
         setUI(new BasicButtonUI());
         setContentAreaFilled(false);
         setFocusable(false);
         setBorderPainted(false);
         setIcon(closeButtonDimmedIcon);
         addMouseListener(buttonMouseListener);
         setRolloverEnabled(true);
         addActionListener(this);
      }

      public void actionPerformed(ActionEvent e)
      {
         int i = pane.indexOfTabComponent(ExtTab.this);
         if (i != -1) pane.remove(i);
      }

      @Override
      public void updateUI()
      {
      }
   }

   private final static MouseListener buttonMouseListener = new MouseAdapter()
   {
      @Override
      public void mouseEntered(MouseEvent e)
      {
         Component component = e.getComponent();
         if (component instanceof AbstractButton)
         {
            AbstractButton button = (AbstractButton) component;
            button.setIcon(closeButtonHighliteIcon);
         }
      }

      @Override
      public void mouseExited(MouseEvent e)
      {
         Component component = e.getComponent();
         if (component instanceof AbstractButton)
         {
            AbstractButton button = (AbstractButton) component;
            if (button.getIcon() == closeButtonHighliteIcon)
               button.setIcon(closeButtonIcon);
         }
      }
   };
}
