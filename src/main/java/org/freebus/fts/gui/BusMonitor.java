package org.freebus.fts.gui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.comm.BusInterfaceFactory;
import org.freebus.fts.comm.BusListener;
import org.freebus.fts.eib.Telegram;
import org.freebus.fts.emi.EmiMessage;
import org.freebus.fts.emi.EmiMessageBase;
import org.freebus.fts.emi.L_Data;
import org.freebus.fts.utils.I18n;

/**
 * A widget that outputs the telegrams on the EIB bus.
 */
public class BusMonitor extends TabPage implements BusListener
{
   protected final Group grp = new Group(this, SWT.FLAT|SWT.UNDERLINE_SQUIGGLE);
   protected final Tree tree = new Tree(grp, SWT.BORDER);
   protected final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
   protected final Font fntCaption;
   protected int count = 0;

   /**
    * Create a new bus monitor widget.
    * 
    * @param parent is the parent widget. 
    * @param style is the SWT gui style.
    */
   public BusMonitor(Composite parent)
   {
      super(parent);
      setTitle(I18n.getMessage("BusMonitor_Tab"));
      setPlace(SWT.CENTER);
      
      final FontData curFontData = getFont().getFontData()[0];
      fntCaption = new Font(Display.getCurrent(), new FontData(curFontData.getName(), curFontData.getHeight(), SWT.BOLD));

      final FillLayout fillLayout = new FillLayout();
      fillLayout.marginWidth = 4;
      fillLayout.marginHeight = 4;
      setLayout(fillLayout);

      grp.setText(I18n.getMessage("BusMonitor_Caption"));
      grp.setLayout(new FillLayout());

      TreeColumn col = new TreeColumn(tree, SWT.DEFAULT);
      col.setWidth(50);

      col = new TreeColumn(tree, SWT.DEFAULT);
      col.setWidth(300);

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

   @Override
   public void setObject(Object o)
   {
   }

   protected void addMessage(EmiMessage message)
   {
      if (!(message instanceof L_Data.base)) return;

      ++count;

      final Telegram telegram = ((L_Data.base) message).getTelegram();
      final EmiMessageBase msg = (EmiMessageBase) message;
      final Calendar cal = Calendar.getInstance();

      final int[] raw = new int[256];
      final int len = telegram.toRawData(raw, 0);
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < len; ++i)
         sb.append(String.format("%02x ", raw[i]));

      final TreeItem treeItem = new TreeItem(tree, SWT.DEFAULT);
      treeItem.setFont(fntCaption);
      treeItem.setText(0, dateFormat.format(cal.getTime()) + ' ' + msg.getTypeString());
      treeItem.setText(1, I18n.getMessage("BusMonitor_Raw_Data").replace("%1", sb.toString()));
      
      TreeItem childItem = new TreeItem(treeItem, SWT.DEFAULT);
      childItem.setText(0, I18n.getMessage("BusMonitor_Parties"));
      childItem.setText(1, I18n.getMessage("BusMonitor_From_Dest")
            .replace("%1", telegram.getFrom().toString())
            .replace("%2", telegram.getDest().toString()));

      String seqStr;
      if (telegram.getTransport().hasSequence)
         seqStr = " " + I18n.getMessage("BusMonitor_Sequence").replace("%1", Integer.toString(telegram.getSequence()));
      else seqStr = "";

      final int[] appData = telegram.getData();
      sb = new StringBuilder();
      String appDataStr;
      if (appData != null)
      {
         for (int i = 0; i < appData.length; ++i)
            sb.append(String.format(" %02x", appData[i]));
         appDataStr = I18n.getMessage("BusMonitor_AppData").replace("%1", sb.toString().substring(1));
      }
      else appDataStr = "";
      
      childItem = new TreeItem(treeItem, SWT.DEFAULT);
      childItem.setText(0, I18n.getMessage("BusMonitor_Contents"));
      childItem.setText(1, I18n.getMessage("BusMonitor_Types")
            .replace("%1", telegram.getTransport().toString() + seqStr)
            .replace("%2", telegram.getApplication().toString())
            .replace("%3", appDataStr));

      for (final TreeColumn col: tree.getColumns())
         col.pack();
      treeItem.setExpanded(true);
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
            addMessage(message);            
         }
      });
   }

   @Override
   public void telegramReceived(final Telegram telegram)
   {
      getDisplay().syncExec(new Runnable()
      {
         @Override
         public void run()
         {
         }
      });
   }
}
