package org.freebus.fts.gui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.comm.BusInterfaceFactory;
import org.freebus.fts.comm.BusListener;
import org.freebus.fts.emi.EmiMessage;
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
         final BusInterface bus = BusInterfaceFactory.getDefaultInstance();
         if (!bus.isOpen()) bus.open();
         bus.addListener(this);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         MessageBox mbox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
         mbox.setMessage(e.getMessage());
         mbox.open();
      }
   }

   /**
    * Destroy the widget.
    */
   @Override
   public void dispose()
   {
      final BusInterface bus = BusInterfaceFactory.getDefaultInstance();
      bus.removeListener(this);

      super.dispose();
   }
   
   /**
    * An EMI message was received.
    */
   @Override
   public void messageReceived(final EmiMessage message)
   {
      getDisplay().syncExec(new Runnable()
      {
         @Override
         public void run()
         {
            list.add(message.toString());            
         }
      });
   }
}
