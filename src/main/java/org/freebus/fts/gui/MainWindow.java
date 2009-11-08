package org.freebus.fts.gui;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.freebus.fts.Config;
import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.comm.BusInterfaceFactory;
import org.freebus.fts.eib.Application;
import org.freebus.fts.eib.GroupAddress;
import org.freebus.fts.eib.PhysicalAddress;
import org.freebus.fts.eib.Priority;
import org.freebus.fts.eib.Telegram;
import org.freebus.fts.emi.EmiMessage;
import org.freebus.fts.emi.L_Data;
import org.freebus.fts.project.Project;
import org.freebus.fts.settings.Settings;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ImageCache;
import org.freebus.fts.utils.SimpleSelectionListener;
import org.freebus.fts.vdx.VdxFileReader;
import org.freebus.fts.vdx.VdxProductDb;
import org.freebus.fts.vdx.VdxToDb;

/**
 * The main window.
 */
public final class MainWindow extends WorkBench
{
   private static MainWindow instance = null;

   final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
   final Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN);
   final Menu settingsMenu = new Menu(shell, SWT.DROP_DOWN);
   final TopologyTab topologyTab;
   final PhysicalTab physicalTab;
   final LogicalTab logicalTab;
   ToolItem toolItemTestMessage1 = null;

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
      fileMenuHeader.setText(I18n.getMessage("File_Menu"));
      fileMenuHeader.setMenu(fileMenu);
   
      menuItem = new MenuItem(fileMenu, SWT.PUSH);
      menuItem.setText(I18n.getMessage("New_Project"));
      menuItem.addSelectionListener(new OnNewProject());
   
      menuItem = new MenuItem(fileMenu, SWT.PUSH);
      menuItem.setText(I18n.getMessage("Open_Project"));
      menuItem.addSelectionListener(new OnOpenProject());
      menuItem.setAccelerator(SWT.CONTROL|'O');
   
      menuItem = new MenuItem(fileMenu, SWT.PUSH);
      menuItem.setText(I18n.getMessage("Save_Project"));
      menuItem.addSelectionListener(new OnSaveProject());
      menuItem.setAccelerator(SWT.CONTROL|'S');
   
      new MenuItem(fileMenu, SWT.SEPARATOR);
   
      menuItem = new MenuItem(fileMenu, SWT.PUSH);
      menuItem.setText(I18n.getMessage("Exit"));
      menuItem.addSelectionListener(new OnExit());
      menuItem.setAccelerator(SWT.CONTROL|'Q');
   
      //
      // Menu: Tools
      //
      final MenuItem toolsMenuHeader = new MenuItem(getMenuBar(), SWT.CASCADE);
      toolsMenuHeader.setText(I18n.getMessage("Tools_Menu"));
      toolsMenuHeader.setMenu(toolsMenu);
   
      menuItem = new MenuItem(toolsMenu, SWT.PUSH);
      menuItem.setText(I18n.getMessage("Products_Browser"));
      menuItem.addSelectionListener(new OnProductsBrowser());
   
      menuItem = new MenuItem(toolsMenu, SWT.PUSH);
      menuItem.setText(I18n.getMessage("Vdx_Browser"));
      menuItem.addSelectionListener(new OnVdxBrowser());

      //
      // Menu: Settings
      //
      final MenuItem settingsMenuHeader = new MenuItem(getMenuBar(), SWT.CASCADE);
      settingsMenuHeader.setText(I18n.getMessage("Settings_Menu"));
      settingsMenuHeader.setMenu(settingsMenu);
   
      menuItem = new MenuItem(settingsMenu, SWT.PUSH);
      menuItem.setText(I18n.getMessage("Settings"));
      menuItem.addSelectionListener(new OnSettings());
   
      shell.setMenuBar(getMenuBar());
   }

   /**
    * Create the tool bar.
    */
   protected void initToolBar()
   {
      ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
      ToolItem toolItem;
   
      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/exit"));
      toolItem.addSelectionListener(new OnExit());
      toolItem.setToolTipText(I18n.getMessage("Exit_Tip"));
   
      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/find"));
      toolItem.addSelectionListener(new OnProductsBrowser());
      toolItem.setToolTipText(I18n.getMessage("Products_Browser_Tip"));
      
      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/wizard"));
      toolItem.addSelectionListener(new OnCopyVDX());
      toolItem.setToolTipText(I18n.getMessage("Products_Copy_VDX_Tip"));
 
      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/bus-monitor"));
      toolItem.addSelectionListener(new OnBusMonitor());
      toolItem.setToolTipText(I18n.getMessage("Bus_Monitor_Tip"));
   
      addToolBar(toolBar);
   
      
      toolBar = new ToolBar(coolBar, SWT.FLAT);
   
      toolItem = new ToolItem(toolBar, SWT.PUSH);
      toolItem.setImage(ImageCache.getImage("icons/music_32ndnote"));
      toolItem.addSelectionListener(new OnSendBusMessage());
      toolItem.setToolTipText(I18n.getMessage("Bus_Send_Test_Message"));
      toolItemTestMessage1 = toolItem;
   
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
    * Event listener for: new-project
    */
   class OnNewProject extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         // TODO
      }
   }

   /**
    * Event listener for: load-project
    */
   class OnOpenProject extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         // TODO
      }
   }

   /**
    * Event listener for: save-project
    */
   class OnSaveProject extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         // TODO
      }
   }

   /**
    * Event listener for: exit
    */
   class OnExit extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         shell.close();
         display.dispose();
      }
   }

   /**
    * Event listener for: settings
    */
   class OnSettings extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         Settings.openDialog();
      }
   }

   /**
    * Event listener for: open bus monitor
    */
   class OnBusMonitor extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         showTabPage(BusMonitor.class, null);
      }
   }

   /**
    * Event callback: Products Browser
    */
   class OnProductsBrowser extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         final FileDialog fileDialog = new FileDialog(shell, SWT.SINGLE);
         fileDialog.setText(I18n.getMessage("Products_Browser_Open_File"));
         fileDialog.setFilterExtensions(new String[] { "*.vd_", "*" });
         fileDialog.setFilterNames(new String[] { "VDX Files", "Any" });
         final String vdxDir = Config.getInstance().getVdxDir();
         if (vdxDir!=null) fileDialog.setFilterPath(vdxDir);

         final String fileName = fileDialog.open();
         if (fileName == null) return;

         final Config cfg = Config.getInstance();
         cfg.setVdxDir(new File(fileName).getParentFile().getPath());
         cfg.save();

         try
         {
            showTabPage(ProductsTab.class, new VdxProductDb(fileName));
         }
         catch (Exception e)
         {
            e.printStackTrace();
            MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mbox.setMessage(e.getLocalizedMessage());
            mbox.open();
            return;
         }

      }
   }

   /**
    * Event callback: VDX Browser
    */
   class OnVdxBrowser extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         final FileDialog fileDialog = new FileDialog(shell, SWT.SINGLE);
         fileDialog.setText(I18n.getMessage("Products_Browser_Open_File"));
         fileDialog.setFilterExtensions(new String[] { "*.vd*", "*" });
         fileDialog.setFilterNames(new String[] { "VDX Files", "Any" });
         final String vdxDir = Config.getInstance().getVdxDir();
         if (vdxDir!=null) fileDialog.setFilterPath(vdxDir);

         final String fileName = fileDialog.open();
         if (fileName == null) return;

         final Config cfg = Config.getInstance();
         cfg.setVdxDir(new File(fileName).getParentFile().getPath());
         cfg.save();

         VdxFileReader reader = null;
         try
         {
            reader = new VdxFileReader(fileName);
         }
         catch (IOException e)
         {
            e.printStackTrace();
            MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mbox.setMessage(e.getMessage());
            mbox.open();
            return;
         }               

         showTabPage(VdxBrowser.class, reader);
      }
   }

   /**
    * Event callback: Send a test message to the EIB bus.
    */
   class OnSendBusMessage extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         BusInterface bus;
         try
         {
            bus = BusInterfaceFactory.getDefaultInstance();
            if (!bus.isOpen()) bus.open();
         }
         catch (IOException e)
         {
            e.printStackTrace();
            return;
         }

         EmiMessage msg = null;
         if (event.widget==toolItemTestMessage1)
         {
            final L_Data.req newMsg = new L_Data.req();
            Telegram telegram = newMsg.getTelegram();
//            telegram.setDest(new PhysicalAddress(1, 1, 6));
            telegram.setFrom(new PhysicalAddress(1, 1, 255));
            telegram.setDest(new GroupAddress(0));
            telegram.setPriority(Priority.SYSTEM);
            telegram.setApplication(Application.IndividualAddress_Read);
            telegram.setData(new int[] { 10 });
            msg = newMsg;
         }

         if (msg != null) try
         {
            bus.write(msg);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * Event callback: Copy a vd_ file into the products database.
    */
   class OnCopyVDX extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         final FileDialog fileDialog = new FileDialog(shell, SWT.SINGLE);
         fileDialog.setText(I18n.getMessage("VdxToDb_Open_File"));
         fileDialog.setFilterExtensions(new String[] { "*.vd*", "*" });
         fileDialog.setFilterNames(new String[] { "VDX Files", "Any" });
         final String vdxDir = Config.getInstance().getVdxDir();
         if (vdxDir!=null) fileDialog.setFilterPath(vdxDir);

         final String fileName = fileDialog.open();
         if (fileName == null) return;

         final Config cfg = Config.getInstance();
         cfg.setVdxDir(new File(fileName).getParentFile().getPath());
         cfg.save();

         final VdxToDb conv = new VdxToDb(fileName);
         final ProgressDialog dlg = new ProgressDialog(I18n.getMessage("VdxToDb_Title"), I18n.getMessage("VdxToDb_Description").replace("%1", fileName));
         dlg.run(conv);
      }   
   }
}
