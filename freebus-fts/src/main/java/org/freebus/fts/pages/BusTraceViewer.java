package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.freebus.fts.common.HexString;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.core.I18n;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.pages.busmonitor.BusMonitorCellRenderer;
import org.freebus.fts.pages.busmonitor.BusMonitorItem;
import org.freebus.fts.utils.TreeUtils;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameFactory;

/**
 * A page that displays the contents of a KNX/EIB bus trace file.
 */
public class BusTraceViewer extends AbstractPage
{
   private static final long serialVersionUID = -7001873554872571938L;

   private final JTree tree;
   private final JScrollPane treeView;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
   private final transient BusMonitorCellRenderer cellRenderer;
   private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

   /**
    * Create a bus trace viewer.
    */
   public BusTraceViewer()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("BusTraceViewer.Title"));

      tree = new JTree(treeModel);
      tree.setRootVisible(false);

      cellRenderer = new BusMonitorCellRenderer();
      tree.setCellRenderer(cellRenderer);

      treeView = new JScrollPane(tree);
      add(treeView, BorderLayout.CENTER);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setObject(Object o)
   {
      try
      {
         final File file = (File) o;
         open(file);
         setName(file.getName());
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.formatMessage("BusTraceViewer.ErrOpen", new Object[] { o }));
      }
   }

   /**
    * Clear the page
    */
   public void clear()
   {
      rootNode.removeAllChildren();
      treeModel.reload();
   }

   /**
    * Read the file and display it's contents.
    *
    * @param file - the file to open
    * @throws IOException
    */
   public void open(File file) throws IOException
   {
      final BufferedReader in = new BufferedReader(new FileReader(file));
      final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
      EmiFrame frame;
      Date when;

      rootNode.removeAllChildren();

      int id = 0;
      while (true)
      {
         final String line = in.readLine();
         if (line == null)
            break;

         int pos = line.indexOf('\t');
         if (pos < 0)
            continue;

         try
         {
            when = dateFormatter.parse(line.substring(0, pos));
         }
         catch (ParseException e)
         {
            Logger.getLogger(getClass()).warn(e);
            when = null;
         }

         frame = EmiFrameFactory.createFrame(HexString.valueOf(line.substring(pos + 1)));
         DefaultMutableTreeNode node = new DefaultMutableTreeNode(new BusMonitorItem(id + 1, when, frame));
         treeModel.insertNodeInto(node, rootNode, id);

         if (id <= 1)
            TreeUtils.expandAll(tree);

         ++id;
      }
   }
}
