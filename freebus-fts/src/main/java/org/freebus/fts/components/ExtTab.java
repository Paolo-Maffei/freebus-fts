package org.freebus.fts.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
   private static final Icon closeButtonIcon = ImageCache.getIcon("gui-images/tab-close");
   private static final Icon closeButtonHighliteIcon = ImageCache.getIcon("gui-images/tab-close-highlite");
   private static final Icon closeButtonDimmedIcon = ImageCache.getIcon("gui-images/tab-close-dimmed");
   private final JTabbedPane pane;
   private final CloseButton closeButton;
   private final JLabel lblTitle;
   private boolean mouseInTab, mouseInCloseButton;

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
      // setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      this.pane = pane;

      setOpaque(false);

      lblTitle = new JLabel(title);
      add(lblTitle, BorderLayout.CENTER);

      if (icon != null)
         add(new JLabel(icon), BorderLayout.WEST);

      if (closable)
      {
         closeButton = new CloseButton();
         add(closeButton, BorderLayout.EAST);

         addMouseListener(new MouseAdapter()
         {
            @Override
            public void mouseEntered(MouseEvent e)
            {
               mouseInTab = true;
               updateCloseButton();
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
               mouseInTab = false;
               updateCloseButton();
            }
         });
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

   /**
    * Set the name of the tab. This is also the displayed label.
    */
   @Override
   public void setName(String name)
   {
      super.setName(name);
      lblTitle.setText(name);
   }

   /**
    * Set the close button icon, depending on where the mouse is.
    */
   private void updateCloseButton()
   {
      if (mouseInCloseButton)
         closeButton.setIcon(closeButtonHighliteIcon);
      else if (mouseInTab)
         closeButton.setIcon(closeButtonIcon);
      else closeButton.setIcon(closeButtonDimmedIcon);
   }

   /**
    * Internal class of {@link ExtTab} for the close button.
    */
   private class CloseButton extends JButton implements ActionListener
   {
      private static final long serialVersionUID = -1335643238071282239L;

      public CloseButton()
      {
         int size = 17;
         setPreferredSize(new Dimension(size, size));
         // setToolTipText(I18n.getMessage("ExtTab.CloseTip"));
         setUI(new BasicButtonUI());
         setContentAreaFilled(false);
         setFocusable(false);
         setBorderPainted(false);
         setIcon(closeButtonDimmedIcon);
         setRolloverEnabled(true);
         addActionListener(this);

         addMouseListener(new MouseAdapter()
         {
            @Override
            public void mouseEntered(MouseEvent e)
            {
               mouseInCloseButton = true;
               updateCloseButton();
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
               mouseInCloseButton = false;
               updateCloseButton();
            }
         });
      }

      public void actionPerformed(ActionEvent e)
      {
         int i = pane.indexOfTabComponent(ExtTab.this);
         if (i != -1)
            pane.remove(i);
      }

      @Override
      public void updateUI()
      {
      }
   }
}
