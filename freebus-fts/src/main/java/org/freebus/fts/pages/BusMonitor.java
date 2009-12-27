package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.pages.internal.BusMonitorCellRenderer;
import org.freebus.fts.pages.internal.BusMonitorItem;
import org.freebus.fts.utils.TreeUtils;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.serial.SerialPortUtil;
import org.freebus.knxcomm.telegram.Telegram;

/**
 * A KNX/ETS bus monitor.
 */
public class BusMonitor extends AbstractPage implements TelegramListener
{
   private static final long serialVersionUID = -3243196694923284469L;

   private final JTree tree;
   private final JScrollPane treeView;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
   private final BusMonitorCellRenderer cellRenderer;
   private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

   private BusInterface iface = null;

   /**
    * Create a bus monitor widget.
    */
   public BusMonitor()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("BusMonitor.Title"));

      tree = new JTree(treeModel);
      tree.setRootVisible(false);

      cellRenderer = new BusMonitorCellRenderer();
      tree.setCellRenderer(cellRenderer);

      treeView = new JScrollPane(tree);
      add(treeView, BorderLayout.CENTER);

      treeView.addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentResized(ComponentEvent e)
         {
            cellRenderer.setViewSize(treeView.getSize());
         }
      });
   }

   /**
    * Set the object that the bus monitor works with. The KNX/EIB bus connection
    * is opened here, the given object is ignored.
    */
   @Override
   public void setObject(Object o)
   {
      if (iface == null)
      {
         try
         {
            // TODO use the bus connection type that was configured in the settings
            // dialog.

            SerialPortUtil.loadSerialPortLib();
            BusInterface newIface = BusInterfaceFactory.newSerialInterface(Config.getInstance().getStringValue(
                  "knxConnectionSerial.port"));

            newIface.addListener(this);
            newIface.open();
            iface = newIface;
         }
         catch (Exception e)
         {
            Dialogs.showExceptionDialog(e, I18n.getMessage("BusMonitor.ErrOpenBus"));
         }
      }
   }

   /**
    * Add an entry to the list of telegrams.
    * 
    * @param telegram - The telegram that the entry is about.
    * @param isReceived - True if the telegram was received, false else.
    */
   private void addBusMonitorItem(final Telegram telegram, final boolean isReceived)
   {
      try
      {
         SwingUtilities.invokeAndWait(new Runnable()
         {
            @Override
            public void run()
            {
               final int numChilds = treeModel.getChildCount(rootNode);

               DefaultMutableTreeNode node = new DefaultMutableTreeNode(new BusMonitorItem(telegram, isReceived));
               treeModel.insertNodeInto(node, rootNode, numChilds);

               if (numChilds <= 1)
                  TreeUtils.expandAll(tree);

               tree.scrollRowToVisible(numChilds + 1);
            }
         });
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, "Internal error: failed to add telegram to the bus monitor list");
      }
   }

   /**
    * Callback: a telegram was received.
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      addBusMonitorItem(telegram, true);
   }

   /**
    * Callback: a telegram was sent.
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
      addBusMonitorItem(telegram, false);
   }
}
