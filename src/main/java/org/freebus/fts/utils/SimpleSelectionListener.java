package org.freebus.fts.utils;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;


/**
 * A simple wrapper class for @ref SelectionListener that implements
 * widgetDefaultSelected for you.
 */
public abstract class SimpleSelectionListener implements SelectionListener
{
   @Override
   public final void widgetDefaultSelected(SelectionEvent event)
   {
      widgetSelected(event);
   }
}
