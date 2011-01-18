package org.freebus.fts.client.components;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * A status bar.
 */
public class StatusBar extends Box
{
   private static final long serialVersionUID = -7398294079859028138L;

   /**
    * Create a status bar widget.
    */
   public StatusBar()
   {
      super(BoxLayout.X_AXIS);

      setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
   }
}
