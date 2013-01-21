package org.freebus.fts.client.editors.busmonitor;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.client.core.Config;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.filters.TrxFileFilter;
import org.freebus.fts.client.workbench.WorkBenchEditor;
import org.freebus.fts.common.HexString;
import org.freebus.fts.common.address.PhysicalAddress;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.components.ToolBar;
import org.freebus.fts.elements.components.ToolBarButton;
import org.freebus.fts.elements.models.FilteredListModel;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.application.DeviceDescriptorRead;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.L_Data_con;
import org.freebus.knxcomm.emi.L_Data_ind;
import org.freebus.knxcomm.emi.types.EmiFrameType;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.TelegramListener;
import org.freebus.knxcomm.telegram.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A KNX/ETS bus monitor.
 */
public class BusMonitor extends WorkBenchEditor implements TelegramListener
{
   private static final Logger LOGGER = LoggerFactory.getLogger(BusMonitor.class);
   private static final long serialVersionUID = -3243196694923284469L;

   private JButton btnErase, btnFilter, btnSave;
   private int sequence;

   private final JList list;
   private final JScrollPane treeView;

   private final FrameFilter filter = new FrameFilter();
   private final DefaultListModel model = new DefaultListModel();
   private final FilteredListModel filteredModel = new FilteredListModel(model, new BusMonitorItemFilter(filter));

   private BusInterface bus = null;

   /**
    * Create a bus monitor widget.
    */
   public BusMonitor()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("BusMonitor.Title"));

      try
      {
         filter.setEnabled(false);
         filter.fromConfig(Config.getInstance(), "busMonitor.filter");
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e, I18n.getMessage("BusMonitor.ErrLoadFilter"));
         filter.reset();
      }

      list = new JList(filteredModel);
      list.setCellRenderer(new BusMonitorListCellRenderer());

      treeView = new JScrollPane(list);
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
         public void actionPerformed(ActionEvent event)
         {
            synchronized (model)
            {
               sequence = 0;
               model.clear();
               updateButtons();
            }
         }
      });

      btnFilter = new ToolBarButton(ImageCache.getIcon("icons/filter"));
      btnFilter.setToolTipText(I18n.getMessage("BusMonitor.Filter.ToolTip"));
      toolBar.add(btnFilter);
      btnFilter.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent event)
         {
            final FilterDialog dlg = new FilterDialog(MainWindow.getInstance(), ModalityType.APPLICATION_MODAL);
            dlg.setFilter(filter);
            dlg.setVisible(true);

            if (dlg.isAccepted())
            {
               btnFilter.setSelected(filter.isEnabled());
               filteredModel.update();
               try
               {
                  filter.toConfig(Config.getInstance(), "busMonitor.filter");
                  Config.getInstance().save();
               }
               catch (Exception e)
               {
                  Dialogs.showExceptionDialog(e, I18n.getMessage("BusMonitor.ErrSaveFilter"));
               }
            }
         }
      });

      toolBar.addSeparator();

      final JButton btnTestTele = new ToolBarButton(ImageCache.getIcon("icons/music_32ndnote"));
      btnTestTele.setToolTipText(I18n.getMessage("BusMonitor.SendTestTelegram.ToolTip"));
      toolBar.add(btnTestTele);
      btnTestTele.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent event)
         {
            if (bus == null) return;

            final Telegram telegram = new Telegram();
            telegram.setDest(new PhysicalAddress(1, 1, 88));
            telegram.setPriority(Priority.SYSTEM);
            telegram.setTransport(Transport.Connect);

            try
            {
               bus.send(telegram);
            }
            catch (Exception e)
            {
               Dialogs.showExceptionDialog(e, "Failed to send test telegram");
            }
         }
      });

      final JButton btnTestTele2 = new ToolBarButton(ImageCache.getIcon("icons/music_cross"));
      btnTestTele2.setToolTipText(I18n.getMessage("BusMonitor.SendTestTelegram.ToolTip"));
      toolBar.add(btnTestTele2);
      btnTestTele2.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent event)
         {
            if (bus == null) return;

            final Telegram telegram = new Telegram(new DeviceDescriptorRead(0));
            telegram.setDest(new PhysicalAddress(1, 1, 88));
            telegram.setPriority(Priority.SYSTEM);
            telegram.setTransport(Transport.Connected);

            try
            {
               bus.send(telegram);
            }
            catch (Exception e)
            {
               Dialogs.showExceptionDialog(e, "Failed to send test telegram");
            }
         }
      });
   }

   /**
    * Set the object that the bus monitor works with. The KNX/EIB bus connection
    * is opened here, the given object is ignored.
    */
   @Override
   public void objectChanged(Object o)
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
         synchronized (model)
         {
            out = new FileWriter(file);

            for (Object obj: model.toArray())
            {
               final BusMonitorItem item = (BusMonitorItem) obj;

               out.write(dateFormatter.format(item.getWhen()));
               out.write("\t");
               out.write(HexString.toString(item.getFrame().toByteArray()));
               out.write("\n");
            }

            LOGGER.info("Bus trace log saved");

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
               synchronized (model)
               {
                  final boolean needUpdate = model.isEmpty();

                  model.addElement(new BusMonitorItem(id, when, frame));

//                  list.scrollToVisible(numChilds + 1);
                  if (needUpdate)
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
      final boolean haveFrames = !model.isEmpty();
      btnSave.setEnabled(haveFrames);
      btnErase.setEnabled(haveFrames);
   }

   /**
    * Callback: a telegram was received.
    */
   @Override
   public void telegramReceived(Telegram telegram)
   {
      addBusMonitorItem(new L_Data_ind(EmiFrameType.EMI2_L_DATA_IND, telegram));
   }

   /**
    * Callback: a telegram was sent.
    */
   @Override
   public void telegramSent(Telegram telegram)
   {
      addBusMonitorItem(new L_Data_con(EmiFrameType.EMI2_L_DATA_CON, telegram));
   }
}
