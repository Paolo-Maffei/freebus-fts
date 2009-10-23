package org.freebus.fts.gui;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.freebus.fts.Config;
import org.freebus.fts.comm.BusConnectException;
import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.eib.MessageCode;
import org.freebus.fts.eib.Telegram;
import org.freebus.fts.project.Project;
import org.freebus.fts.settings.Settings;
import org.freebus.fts.utils.I18n;
import org.freebus.fts.utils.ImageCache;
import org.freebus.fts.utils.SimpleSelectionListener;
import org.freebus.fts.vdx.VdxLoader;
import org.freebus.fts.vdx.VdxSectionType;

/**
 * The main window.
 */
public final class MainWindow
{
   final private Display display = Display.getCurrent();
   final private Shell shell = new Shell(display);
   final private Menu menuBar = new Menu(shell, SWT.BAR);
   final private Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
   final private Menu toolsMenu = new Menu(shell, SWT.DROP_DOWN);
   final private Menu settingsMenu = new Menu(shell, SWT.DROP_DOWN);
   final private CoolBar coolBar = new CoolBar(shell, SWT.FLAT);
   final private CTabFolder leftTabFolder, centerTabFolder;
   final private TopologyTab topologyTab;
   final private PhysicalTab physicalTab;
   final private LogicalTab logicalTab;
   final private SashForm body;
   private ToolItem toolItemTestMessage1 = null;

   private Project project = Project.createSampleProject();

   MenuItem fileMenuHeader, helpMenuHeader;
   MenuItem fileExitItem, fileSaveItem, helpGetHelpItem;

   /**
    * Construct the main window.
    */
   public MainWindow()
   {
      shell.setText(I18n.getMessage("Main_Window_Title"));
      shell.setLayout(new FormLayout());

      final double f = 0.9;
      final int w = (int) (display.getBounds().width * f);
      final int h = (int) (display.getBounds().height * f);
      shell.setSize(w, h);

      FormData formData;

      initMenuBar();
      initToolBar();

      body = new SashForm(shell, SWT.FLAT | SWT.HORIZONTAL);
      body.setLayout(new FillLayout());

      formData = new FormData();
      formData.top = new FormAttachment(0);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      coolBar.setLayoutData(formData);

      formData = new FormData();
      formData.top = new FormAttachment(coolBar, 1);
      formData.left = new FormAttachment(0);
      formData.right = new FormAttachment(100);
      formData.bottom = new FormAttachment(100);
      body.setLayoutData(formData);

      leftTabFolder = new CTabFolder(body, SWT.BORDER | SWT.TOP);
      leftTabFolder.setSimple(false);
      leftTabFolder.setMinimizeVisible(false);
      leftTabFolder.setMaximizeVisible(false);
      leftTabFolder.setSelectionBackground(body.getBackground());
      leftTabFolder.setSelectionBackground(ImageCache.getImage("images/tab-sel"));

      centerTabFolder = new CTabFolder(body, SWT.DEFAULT);
      centerTabFolder.setSimple(false);
      centerTabFolder.setMinimizeVisible(false);
      centerTabFolder.setMaximizeVisible(false);
      centerTabFolder.setSelectionBackground(body.getBackground());
      centerTabFolder.setSelectionBackground(ImageCache.getImage("images/tab-sel"));

      body.setWeights(new int[] { 1, 3 });

      final CTabItem topologyItem = new CTabItem(leftTabFolder, SWT.CLOSE);
      topologyItem.setText(I18n.getMessage("Topology_Tab"));
      topologyTab = new TopologyTab(leftTabFolder, project);
      topologyItem.setControl(topologyTab);
      leftTabFolder.setSelection(topologyItem);

      final CTabItem physicalItem = new CTabItem(leftTabFolder, SWT.CLOSE);
      physicalItem.setText(I18n.getMessage("Physical_Tab"));
      physicalTab = new PhysicalTab(leftTabFolder, project);
      physicalItem.setControl(physicalTab);

      final CTabItem logicalItem = new CTabItem(leftTabFolder, SWT.CLOSE);
      logicalItem.setText(I18n.getMessage("Logical_Tab"));
      logicalTab = new LogicalTab(leftTabFolder, project);
      logicalItem.setControl(logicalTab);

      final CTabItem testItem = new CTabItem(centerTabFolder, SWT.CLOSE);
      testItem.setText("Test");
      LogicalTab testTab = new LogicalTab(centerTabFolder, project);
      testItem.setControl(testTab);
      centerTabFolder.setSelection(testItem);
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
      final MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
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
      final MenuItem toolsMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
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
      final MenuItem settingsMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
      settingsMenuHeader.setText(I18n.getMessage("Settings_Menu"));
      settingsMenuHeader.setMenu(settingsMenu);

      menuItem = new MenuItem(settingsMenu, SWT.PUSH);
      menuItem.setText(I18n.getMessage("Settings"));
      menuItem.addSelectionListener(new OnSettings());

      shell.setMenuBar(menuBar);
   }

   /**
    * Add the given tool-bar to the main-window's cool-bar.
    */
   protected void addToolBar(ToolBar toolBar)
   {
      toolBar.pack();     
      final Point size = toolBar.getSize();
      final CoolItem item = new CoolItem(coolBar, SWT.NONE);
      item.setControl(toolBar);
      final Point preferred = item.computeSize(size.x+4, size.y+2);
      item.setPreferredSize(preferred);
      toolBar.pack();     
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
    * Dispose all occupied resources.
    */
   public void dispose()
   {
   }

   /**
    * Open the shell
    */
   public void open()
   {
      shell.open();

      topologyTab.updateContents();
      physicalTab.updateContents();
      logicalTab.updateContents();
   }

   /**
    * The main event loop
    */
   public void run()
   {
      while (!shell.isDisposed())
      {
         if (!display.readAndDispatch()) display.sleep();
      }
   }

   /**
    * Open a tab page in a tab-folder. The tab page must be a child of a
    * {@link CTabFolder}.
    */
   public void openTabPage(Composite page, String title)
   {
      final CTabFolder folder = (CTabFolder) page.getParent();

      final CTabItem tabItem = new CTabItem(folder, SWT.CLOSE);
      tabItem.setText(title);
      tabItem.setControl(page);
      folder.setSelection(tabItem);
   }

   /**
    * Event listener for: new-project
    */
   private class OnNewProject extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         // TODO
      }
   }

   /**
    * Event listener for: load-project
    */
   private class OnOpenProject extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         // TODO
      }
   }

   /**
    * Event listener for: save-project
    */
   private class OnSaveProject extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         // TODO
      }
   }

   /**
    * Event listener for: exit
    */
   private class OnExit extends SimpleSelectionListener
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
   private class OnSettings extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         Settings.openDialog();
      }
   }

   /**
    * Event listener for: open bus monitor
    */
   private class OnBusMonitor extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         final CTabItem tabItem = new CTabItem(centerTabFolder, SWT.CLOSE);
         tabItem.setText(I18n.getMessage("Bus_Monitor_Tab"));
         final BusMonitor dlg = new BusMonitor(centerTabFolder, SWT.FLAT);
         tabItem.setControl(dlg);
         centerTabFolder.setSelection(tabItem);
      }
   }

   /**
    * Event Callback: Products Browser
    */
   private class OnProductsBrowser extends SimpleSelectionListener
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

         final VdxLoader loader = new VdxLoader();
         try
         {
            loader.setEndLoadAfter(VdxSectionType.VIRTUAL_DEVICE);
            loader.load(fileName);
         }
         catch (Exception e)
         {
            e.printStackTrace();
            MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mbox.setMessage(e.getLocalizedMessage());
            mbox.open();
            return;
         }

         openTabPage(new ProductsTab(centerTabFolder, loader.getProductDb()), I18n.getMessage("Products_Tab"));
      }
   }

   /**
    * Event Callback: VDX Browser
    */
   private class OnVdxBrowser extends SimpleSelectionListener
   {
      VdxLoader loader;
      VdxBrowser dlg;

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

         final CTabItem tabItem = new CTabItem(centerTabFolder, SWT.CLOSE);
         tabItem.setText(I18n.getMessage("Vdx_Browser_Tab"));
         try
         {
            tabItem.setControl(new VdxBrowser(centerTabFolder, fileName));
            centerTabFolder.setSelection(tabItem);
         }
         catch (IOException e1)
         {
            e1.printStackTrace();
            MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mbox.setMessage(e1.getMessage());
            mbox.open();
         }
      }
   }

   /**
    * Event Callback: Send a test message to the EIB bus.
    */
   private class OnSendBusMessage extends SimpleSelectionListener
   {
      public void widgetSelected(SelectionEvent event)
      {
         BusInterface bus;
         try
         {
            bus = BusInterface.getInstance();
         }
         catch (BusConnectException e)
         {
            e.printStackTrace();
            return;
         }

         Telegram telegram = new Telegram();
         if (event.widget==toolItemTestMessage1)
         {
            telegram.setMessageCode(MessageCode.L_DATA_REQ);
            telegram.setFrom(1, 1, 254);
            telegram.setBroadcastDest();
         }

         if (telegram.getMessageCode() != MessageCode.UNKNOWN) try
         {
            bus.sendData(telegram.toRawData());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }
}
