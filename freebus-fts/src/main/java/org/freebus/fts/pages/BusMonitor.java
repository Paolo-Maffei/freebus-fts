package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.core.I18n;
import org.freebus.fts.pages.busmonitor.BusMonitorCellRenderer;
import org.freebus.fts.pages.busmonitor.BusMonitorItem;
import org.freebus.fts.utils.TreeUtils;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
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
   private final transient BusMonitorCellRenderer cellRenderer;
   private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

   private BusInterface bus = null;

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
   }

   /**
    * Set the object that the bus monitor works with. The KNX/EIB bus connection
    * is opened here, the given object is ignored.
    */
   @Override
   public void setObject(Object o)
   {
      if (bus == null)
         bus = BusInterfaceFactory.getBusInterface();

      if (bus != null)
         bus.addListener(this);
   }

   /**
    * Add an entry to the list of telegrams. The telegram is cloned to avoid
    * problems.
    *
    * @param telegram - The telegram that the entry is about.
    * @param isReceived - True if the telegram was received, false else.
    */
   private void addBusMonitorItem(Telegram telegram, final boolean isReceived)
   {
      final Telegram tel = telegram.clone();
      final Date when = new Date();

      try
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               final int numChilds = treeModel.getChildCount(rootNode);

               DefaultMutableTreeNode node = new DefaultMutableTreeNode(new BusMonitorItem(when, tel, isReceived));
               treeModel.insertNodeInto(node, rootNode, numChilds);

               if (numChilds <= 1)
                  TreeUtils.expandAll(tree);

               tree.scrollRowToVisible(numChilds + 1);
            }
         });
      }
      catch (Exception e)
      {
         e.printStackTrace();
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

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSendConfirmed(Telegram telegram)
   {
   }
}
