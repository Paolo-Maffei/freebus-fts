package org.freebus.fts.gui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.comm.BusInterfaceFactory;
import org.freebus.fts.comm.jobs.JobQueue;
import org.freebus.fts.comm.jobs.JobQueueEvent;
import org.freebus.fts.comm.jobs.JobQueueListener;
import org.freebus.fts.eib.Application;
import org.freebus.fts.eib.PhysicalAddress;
import org.freebus.fts.eib.Priority;
import org.freebus.fts.eib.Telegram;
import org.freebus.fts.eib.Transport;
import org.freebus.fts.gui.actions.Action;
import org.freebus.fts.project.Project;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ImageCache;
import org.freebus.fts.utils.SimpleSelectionListener;
import org.freebus.fts.widgets.JobQueueWidget;

/**
 * The main window.
 */
public final class MainWindow extends WorkBench implements JobQueueListener
{
   private static MainWindow instance = null;

   final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
   final Menu productsMenu = new Menu(shell, SWT.DROP_DOWN);
   final Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN);
   final Menu settingsMenu = new Menu(shell, SWT.DROP_DOWN);
   final TopologyTab topologyTab;
   final PhysicalTab physicalTab;
   final LogicalTab logicalTab;
   final JobQueueWidget jobMonitor;
   ToolItem toolItemTestMessage1, toolItemTestMessage2, toolItemTestMessage3, toolItemTestMessage4;

   private Project project = Project.createSampleProject();

   MenuItem fileMenuHeader, helpMenuHeader;
   MenuItem fileExitItem, fileSaveItem, helpGetHelpItem;

   /**
    * @return the main window instance.
    */
   public static MainWindow getInstance()
   {
      return instance;
   }

   /**
    * Construct the main window.
    */
   public MainWindow()
   {
      instance = this;

      shell.setText(I18n.getMessage("Main_Window_Title"));

      initMenuBar();
      initToolBar();

      physicalTab = (PhysicalTab) showTabPage(PhysicalTab.class, project);
      topologyTab = (TopologyTab) showTabPage(TopologyTab.class, project);
      logicalTab = (LogicalTab) showTabPage(LogicalTab.class, project);
      leftTabFolder.setSelection(0);

      jobMonitor = new JobQueueWidget(leftSash, SWT.BORDER);
      jobMonitor.layout();
      jobMonitor.setVisible(false);

      JobQueue.getDefaultJobQueue().addListener(this);
      leftSash.setWeights(new int[] { 10, 1 });
   }

   /**
    * Create the menu bar.
    */
   protected void initMenuBar()
   {
      MenuItem menuItem;

      //
      // Menu: File
      //
      final MenuItem fileMenuHeader = new MenuItem(getMenuBar(), SWT.CASCADE);
      fileMenuHeader.setText(I18n.getMessage("Menu_File"));
      fileMenuHeader.setMenu(fileMenu);

      Action.PROJECT_NEW.newMenuItem(fileMenu, SWT.PUSH);

      menuItem = Action.PROJECT_OPEN.newMenuItem(fileMenu, SWT.PUSH);
      menuItem.setAccelerator(SWT.CONTROL | 'O');

      new MenuItem(fileMenu, SWT.SEPARATOR);

      menuItem = Action.PROJECT_SAVE.newMenuItem(fileMenu, SWT.PUSH);
      menuItem.setAccelerator(SWT.CONTROL | 'S');

      new MenuItem(fileMenu, SWT.SEPARATOR);

      menuItem = Action.EXIT.newMenuItem(fileMenu, SWT.PUSH);
      menuItem.setAccelerator(SWT.CONTROL | 'Q');

      //
      // Menu: Products
      //
      final MenuItem productsMenuHeader = new MenuItem(getMenuBar(), SWT.CASCADE);
      productsMenuHeader.setText(I18n.getMessage("Menu_Products"));
      productsMenuHeader.setMenu(productsMenu);

      Action.PRODUCTS_BROWSER.newMenuItem(productsMenu, SWT.PUSH);
      Action.VDX_IMPORT.newMenuItem(productsMenu, SWT.PUSH);

      //
      // Menu: Tools
      //
      final MenuItem toolsMenuHeader = new MenuItem(getMenuBar(), SWT.CASCADE);
      toolsMenuHeader.setText(I18n.getMessage("Menu_Tools"));
      toolsMenuHeader.setMenu(toolsMenu);

      Action.VDX_BROWSER.newMenuItem(toolsMenu, SWT.PUSH);

      menuItem = Action.BUS_MONITOR.newMenuItem(toolsMenu, SWT.PUSH);
      menuItem.setAccelerator(SWT.CONTROL | 'B');

      Action.PROGRAM_ADDRESS.newMenuItem(toolsMenu, SWT.PUSH);

      //
      // Menu: Settings
      //
      final MenuItem settingsMenuHeader = new MenuItem(getMenuBar(), SWT.CASCADE);
      settingsMenuHeader.setText(I18n.getMessage("Menu_Settings"));
      settingsMenuHeader.setMenu(settingsMenu);

      Action.SETTINGS.newMenuItem(settingsMenu, SWT.PUSH);


      shell.setMenuBar(getMenuBar());
   }

   /**
    * Create the tool bar.
    */
   protected void initToolBar()
   {
      ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
      ToolItem toolItem;

      Action.EXIT.obj.attach(new ToolItem(toolBar, SWT.PUSH));
      Action.PRODUCTS_BROWSER.obj.attach(new ToolItem(toolBar, SWT.PUSH));
      Action.VDX_IMPORT.obj.attach(new ToolItem(toolBar, SWT.PUSH));
      Action.BUS_MONITOR.obj.attach(new ToolItem(toolBar, SWT.PUSH));

      addToolBar(toolBar);


      toolBar = new ToolBar(coolBar, SWT.FLAT);

      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/music_32ndnote"));
      toolItem.addSelectionListener(new OnSendBusMessage());
      toolItem.setToolTipText(I18n.getMessage("Bus_Send_Test_Message"));
      toolItemTestMessage1 = toolItem;

      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/music_eightnote"));
      toolItem.addSelectionListener(new OnSendBusMessage());
      toolItem.setToolTipText(I18n.getMessage("Bus_Send_Test_Message"));
      toolItemTestMessage2 = toolItem;

      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/music_flat"));
      toolItem.addSelectionListener(new OnSendBusMessage());
      toolItem.setToolTipText(I18n.getMessage("Bus_Send_Test_Message"));
      toolItemTestMessage3 = toolItem;

      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/music_fullnote"));
      toolItem.addSelectionListener(new OnSendBusMessage());
      toolItem.setToolTipText(I18n.getMessage("Bus_Send_Test_Message"));
      toolItemTestMessage4 = toolItem;

      addToolBar(toolBar);
   }

   /**
    * Open the window.
    */
   @Override
   public void open()
   {
      super.open();

      topologyTab.updateContents();
      physicalTab.updateContents();
      logicalTab.updateContents();
   }

   /**
    * Event callback: Send a test message to the EIB bus.
    */
   int sequence = 0;
   class OnSendBusMessage extends SimpleSelectionListener
   {

      public void widgetSelected(SelectionEvent event)
      {
         Telegram telegram = null;
         BusInterface bus;

         try
         {
            bus = BusInterfaceFactory.getDefault();
         }
         catch (IOException e)
         {
            e.printStackTrace();
            MessageBox mbox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            mbox.setMessage(e.getMessage());
            mbox.open();
            return;
         }

         if (event.widget == toolItemTestMessage1)
         {
            telegram = new Telegram();
            telegram.setFrom(new PhysicalAddress(1, 1, 255));
            telegram.setDest(new PhysicalAddress(1, 1, 6));
            telegram.setPriority(Priority.SYSTEM);
            telegram.setTransport(Transport.Connect);
            sequence = 0;
         }
         else if (event.widget == toolItemTestMessage2)
         {
            telegram = new Telegram();
            telegram.setFrom(new PhysicalAddress(1, 1, 255));
            telegram.setDest(new PhysicalAddress(1, 1, 6));
            telegram.setPriority(Priority.SYSTEM);
            telegram.setTransport(Transport.Connected);
            telegram.setSequence(sequence++);
            telegram.setApplication(Application.Memory_Read);
            telegram.setData(new int[] { 0, 0, 0 });
         }
         else if (event.widget == toolItemTestMessage3)
         {
            telegram = new Telegram();
            telegram.setFrom(new PhysicalAddress(1, 1, 255));
            telegram.setDest(new PhysicalAddress(1, 1, 6));
            telegram.setPriority(Priority.SYSTEM);
            telegram.setTransport(Transport.Disconnect);
         }
         else if (event.widget == toolItemTestMessage4)
         {
            telegram = new Telegram();
            telegram.setFrom(new PhysicalAddress(1, 1, 255));
            telegram.setDest(new PhysicalAddress(1, 1, 6));
            telegram.setPriority(Priority.SYSTEM);
            telegram.setTransport(Transport.Connected);
            telegram.setSequence(sequence++);
            telegram.setApplication(Application.Restart);
            telegram.setData(new int[] { 0 });
         }

         if (telegram != null) try
         {
            bus.send(telegram);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * {@inheritDoc}.
    * 
    * The job-queue widget is shown if there is a new job, and is hidden
    * if the job-queue got empty.
    */
   @Override
   public void jobEvent(final JobQueueEvent event)
   {
      if (event.job == null)
      {
         getDisplay().asyncExec(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {
                  Thread.sleep(1000);
               }
               catch (InterruptedException e)
               {
               }

               if (JobQueue.getDefaultJobQueue().isEmpty())
               {
                  jobMonitor.setVisible(false);
                  leftSash.setWeights(new int[] { 8, 0 });
               }
            }
         });
      }
      else
      {
         getDisplay().syncExec(new Runnable()
         {
            @Override
            public void run()
            {
               if (!jobMonitor.isVisible())
               {
                  jobMonitor.setVisible(true);
                  leftSash.setWeights(new int[] { 8, 1 });
               }
               jobMonitor.jobEvent(event);
            }
         });
      }
   }
}
