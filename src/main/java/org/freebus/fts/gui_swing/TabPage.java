package org.freebus.fts.gui_swing;

import javax.swing.JPanel;

/**
 * Abstract base class for tab pages.
 */
public abstract class TabPage extends JPanel
{
   private static final long serialVersionUID = -2748362433916362971L;

   /**
    * @return the title of the tab-page.
    */
   public abstract String getTitle();
}
