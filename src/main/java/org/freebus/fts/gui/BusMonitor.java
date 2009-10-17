package org.freebus.fts.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.freebus.fts.comm.BusConnectException;
import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.comm.BusListener;
import org.freebus.fts.comm.Telegram;
import org.freebus.fts.utils.I18n;

/**
 * A widget that outputs the telegrams on the EIB bus.
 */
public class BusMonitor extends Composite implements BusListener
{
   private final List list;

   /**
    * Create a new bus monitor widget.
    * 
    * @param parent is the parent widget. 
    * @param style is the SWT gui style.
    */
   public BusMonitor(Composite parent, int style)
   {
      super(parent, style);

      final FillLayout fillLayout = new FillLayout();
      fillLayout.marginWidth = 4;
      fillLayout.marginHeight = 4;
      setLayout(fillLayout);

      Group grp = new Group(this, SWT.FLAT|SWT.UNDERLINE_SQUIGGLE);
      grp.setText(I18n.getMessage("Bus_Monitor_Caption"));
      grp.setLayout(new FillLayout());

      list = new List(grp, SWT.BORDER);

      try
      {
         BusInterface.getInstance().addListener(this);
      }
      catch (BusConnectException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Destroy the widget.
    */
   @Override
   public void dispose()
   {
      try
      {
         BusInterface.getInstance().removeListener(this);
      }
      catch (BusConnectException e)
      {
         e.printStackTrace();
      }
      super.dispose();
   }
   
   /**
    * An EIB bus telegram was received.
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
//      String msg = telegram.getFromStr()+" to "+telegram.getRecvStr();
      list.add(telegram.toString());
   }

}
