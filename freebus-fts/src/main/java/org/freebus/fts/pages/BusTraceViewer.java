package org.freebus.fts.pages;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.freebus.fts.I18n;
import org.freebus.fts.MainWindow;
import org.freebus.fts.common.HexString;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.core.Config;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.components.ToolBar;
import org.freebus.fts.elements.components.ToolBarButton;
import org.freebus.fts.elements.models.FilteredListModel;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.pages.busmonitor.BusMonitorItem;
import org.freebus.fts.pages.busmonitor.BusMonitorItemFilter;
import org.freebus.fts.pages.busmonitor.BusMonitorListCellRenderer;
import org.freebus.fts.pages.busmonitor.FilterDialog;
import org.freebus.fts.pages.busmonitor.FrameFilter;
import org.freebus.knxcomm.emi.EmiFrame;
import org.freebus.knxcomm.emi.EmiFrameFactory;

/**
 * A page that displays the contents of a KNX/EIB bus trace file.
 */
public class BusTraceViewer extends AbstractPage
{
   private static final long serialVersionUID = -7001873554872571938L;

   private final JList list;
   private final JScrollPane treeView;

   private final FrameFilter filter = new FrameFilter();
   private final DefaultListModel model = new DefaultListModel();
   private final FilteredListModel filteredModel = new  FilteredListModel(model, new BusMonitorItemFilter(filter));

   /**
    * Create a bus trace viewer.
    */
   public BusTraceViewer()
   {
      setLayout(new BorderLayout());
      setName(I18n.getMessage("BusTraceViewer.Title"));

      try
      {
         filter.setEnabled(false);
         filter.fromConfig(Config.getInstance(), "busTraceViewer.filter");
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
   }

   /**
    * Create the tool-bar.
    */
   private void initToolBar()
   {
      final ToolBar toolBar = new ToolBar();
      add(toolBar, BorderLayout.NORTH);

      final JButton btnFilter = new ToolBarButton(ImageCache.getIcon("icons/filter"));
      toolBar.add(btnFilter);
      btnFilter.setToolTipText(I18n.getMessage("BusMonitor.Filter.ToolTip"));
      btnFilter.setSelected(filter.isEnabled());
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
                  filter.toConfig(Config.getInstance(), "busTraceViewer.filter");
                  Config.getInstance().save();
               }
               catch (Exception e)
               {
                  Dialogs.showExceptionDialog(e, I18n.getMessage("BusMonitor.ErrSaveFilter"));
               }
            }
          }
      });
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

      model.clear();

      for (int id = 1; ; ++id)
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
         model.addElement(new BusMonitorItem(id, when, frame));
      }

      filteredModel.update();
   }
}
