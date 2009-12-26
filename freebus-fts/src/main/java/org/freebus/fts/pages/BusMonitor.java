package org.freebus.fts.pages;

import java.awt.BorderLayout;

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
      tree.setCellRenderer(new BusMonitorCellRenderer());

      treeView = new JScrollPane(tree);
      add(treeView, BorderLayout.CENTER);
   }

   @Override
   public void setObject(Object o)
   {
      if (iface == null)
      {
         try
         {
            // TODO use configured bus connection
            SerialPortUtil.loadSerialPortLib();
            BusInterface newIface = BusInterfaceFactory.newSerialInterface(Config.getInstance().getStringValue(
                  "knxConnectionSerial.port"));

            newIface.addListener(this);
            newIface.open();
            iface = newIface;
         }
         catch (Exception e)
         {
            Dialogs.showExceptionDialog(e, "Cannot open bus connection");
         }
      }
   }

   @Override
   public void telegramReceived(final Telegram telegram)
   {
      try
      {
         SwingUtilities.invokeAndWait(new Runnable()
         {
            @Override
            public void run()
            {
               final int numChilds = treeModel.getChildCount(rootNode);

               DefaultMutableTreeNode node = new DefaultMutableTreeNode(telegram, true);
               treeModel.insertNodeInto(node, rootNode, numChilds);

               if (numChilds <= 1) TreeUtils.expandAll(tree);
               tree.scrollRowToVisible(numChilds + 1);
            }
         });
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void telegramSent(final Telegram telegram)
   {
   }
}
