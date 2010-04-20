package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.freebus.fts.MainWindow;
import org.freebus.fts.common.HexString;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.components.ToolBarButton;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.pages.busmonitor.BusMonitorCellRenderer;
import org.freebus.fts.pages.busmonitor.BusMonitorItem;
import org.freebus.fts.utils.TreeUtils;
import org.freebus.fts.utils.TrxFileFilter;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.TelegramListener;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.L_Data_con;
import org.freebus.knxcomm.emi.L_Data_ind;
import org.freebus.knxcomm.emi.L_Data_req;
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
   private JButton btnSave, btnErase;
   private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
   private int sequence;

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

      initToolBar();
      updateButtons();
   }

   /**
    * Create the tool-bar.
    */
   private void initToolBar()
   {
      final ToolBar toolBar = new ToolBar();
      add(toolBar, BorderLayout.NORTH);

      btnSave = new ToolBarButton(ImageCache.getIcon("icons/filesave"));
      btnSave.setToolTipText(I18n.getMessage("BusMonitor.Save.ToolTip"));
      toolBar.add(btnSave);
      btnSave.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent event)
         {
            saveBusTrace();
         }
      });

      btnErase = new ToolBarButton(ImageCache.getIcon("icons/eraser"));
      btnErase.setToolTipText(I18n.getMessage("BusMonitor.Clear.ToolTip"));
      toolBar.add(btnErase);
      btnErase.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            synchronized (rootNode)
            {
               sequence = 0;
               rootNode.removeAllChildren();
               treeModel.reload();
               updateButtons();
            }
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
      if (bus == null)
         bus = BusInterfaceFactory.getBusInterface();

      if (bus != null)
         bus.addListener(this);
   }

   /**
    * Save the entries to a trace file. Opens a file dialog to select a save
    * file.
    */
   public void saveBusTrace()
   {
      final Config cfg = Config.getInstance();
      String lastDir = cfg.getStringValue("BusTraces.lastDir");

      final JFileChooser dlg = new JFileChooser();
      dlg.setSelectedFile(new File(lastDir));
      final FileFilter fileFilter = new TrxFileFilter();
      dlg.addChoosableFileFilter(fileFilter);
      dlg.addChoosableFileFilter(dlg.getAcceptAllFileFilter());
      dlg.setFileFilter(fileFilter);
      dlg.setDialogTitle(I18n.getMessage("BusMonitor.SaveTraceFileTitle"));
      dlg.setDialogType(JFileChooser.SAVE_DIALOG);

      if (dlg.showOpenDialog(MainWindow.getInstance()) != JFileChooser.APPROVE_OPTION)
         return;

      final File file = dlg.getSelectedFile();
      if (file == null)
         return;

      cfg.put("BusTraces.lastDir", file.getAbsolutePath());
      cfg.save();

      try
      {
         saveBusTrace(file);
      }
      catch (IOException e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("BusMonitor.ErrSaveTraceFile"));
      }
   }

   /**
    * Save the entries to a trace file.
    *
    * @param file - the file to save into
    *
    * @throws IOException
    */
   public void saveBusTrace(File file) throws IOException
   {
      final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
      FileWriter out = null;

      try
      {
         synchronized (rootNode)
         {
            out = new FileWriter(file);

            DefaultMutableTreeNode node;
            for (node = (DefaultMutableTreeNode) rootNode.getFirstChild(); node != null; node = node.getNextLeaf())
            {
               final Object userObject = node.getUserObject();
               if (!(userObject instanceof BusMonitorItem))
                  continue;

               final BusMonitorItem item = (BusMonitorItem) userObject;

               out.write(dateFormatter.format(item.getWhen()));
               out.write("\t");
               out.write(HexString.toString(item.getFrame().toByteArray()));
               out.write("\n");
            }

            Logger.getLogger(getClass()).info("Bus trace log saved");

            out.flush();
         }
      }
      finally
      {
         if (out != null)
            out.close();
      }
   }

   /**
    * Add an entry to the list of telegrams. The telegram is cloned to avoid
    * problems.
    *
    * @param frame - The frame that the entry is about.
    * @param isReceived - True if the telegram was received, false else.
    */
   private void addBusMonitorItem(final EmiFrame frame)
   {
      final Date when = new Date();
      final int id = ++sequence;

      try
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               synchronized (rootNode)
               {
                  final int numChilds = treeModel.getChildCount(rootNode);

                  DefaultMutableTreeNode node = new DefaultMutableTreeNode(new BusMonitorItem(id, when, frame));
                  treeModel.insertNodeInto(node, rootNode, numChilds);

                  if (numChilds <= 1)
                     TreeUtils.expandAll(tree);

                  tree.scrollRowToVisible(numChilds + 1);
                  updateButtons();
               }
            }
         });
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Update the tool button state
    */
   private void updateButtons()
   {
      final boolean haveFrames = rootNode.getChildCount() > 0;
      btnSave.setEnabled(haveFrames);
      btnErase.setEnabled(haveFrames);
   }

   /**
    * Callback: a telegram was received.
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      addBusMonitorItem(new L_Data_ind(telegram));
   }

   /**
    * Callback: a telegram was sent.
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
      addBusMonitorItem(new L_Data_req(telegram));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void telegramSendConfirmed(Telegram telegram)
   {
      addBusMonitorItem(new L_Data_con(telegram));
   }
}
