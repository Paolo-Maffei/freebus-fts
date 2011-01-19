package org.freebus.fts.client.workbench;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * The status bar of the {@link Workbench workbench}.
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
