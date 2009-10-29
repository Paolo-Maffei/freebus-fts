package org.freebus.fts.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.freebus.fts.utils.ImageCache;

/**
 * A generic main window that provides methods for handling the
 * tab-pages, tool-bars, etc.
 */
public class WorkBench
{
   protected final Display display = Display.getCurrent();
   protected final Shell shell = new Shell(display);
   private final Menu menuBar = new Menu(shell, SWT.BAR);
   protected final CoolBar coolBar = new CoolBar(shell, SWT.FLAT);
   protected final SashForm body = new SashForm(shell, SWT.FLAT | SWT.HORIZONTAL);;
   protected final CTabFolder leftTabFolder;
   protected final CTabFolder centerTabFolder;

   protected final Map<Integer,CTabFolder> tabFolders = new HashMap<Integer,CTabFolder>();
   private final Map<TabPageIdent,TabPage> tabPages = new ConcurrentHashMap<TabPageIdent,TabPage>();

   /**
    * Create a new workbench window.
    */
   public WorkBench()
   {
      super();

      shell.setLayout(new FormLayout());
      body.setLayout(new FillLayout());

      final double f = 0.9;
      final int w = (int) (display.getBounds().width * f);
      final int h = (int) (display.getBounds().height * f);
      shell.setSize(w, h);

      FormData formData = new FormData();
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
      tabFolders.put(SWT.LEFT, leftTabFolder);

      centerTabFolder = new CTabFolder(body, SWT.DEFAULT);
      centerTabFolder.setSimple(false);
      centerTabFolder.setMinimizeVisible(false);
      centerTabFolder.setMaximizeVisible(false);
      centerTabFolder.setSelectionBackground(body.getBackground());
      centerTabFolder.setSelectionBackground(ImageCache.getImage("images/tab-sel"));
      tabFolders.put(SWT.CENTER, centerTabFolder);

      body.setWeights(new int[] { 1, 3 });
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
    * Open the window.
    */
   public void open()
   {
      coolBar.pack();
      shell.layout();
      shell.open();
   }
   
   /**
    * Dispose all occupied resources.
    */
   public void dispose()
   {
   }

   /**
    * @return the display.
    */
   public Display getDisplay()
   {
      return display;
   }

   /**
    * @return the shell.
    */
   public Shell getShell()
   {
      return shell;
   }

   /**
    * Open a tab-page for the given item. If it is already opened, bring
    * it to the front.
    * 
    * @param tabPageClass - the class of the tab-page that shall be shown.
    * @param item - the item that the tab-page shall show.
    *
    * @return the created tab-page, or null if the creation failed.
    */
   public TabPage showTabPage(Class<? extends TabPage> tabPageClass, Object item)
   {
      final TabPageIdent ident = new TabPageIdent(tabPageClass, item);
      TabPage tabPage = tabPages.get(ident);
   
      if (tabPage == null)
      {
         try
         {
            tabPage = tabPageClass.getConstructor(Composite.class).newInstance(centerTabFolder);
            tabPage.setObject(item);
            tabPage.setData(item);
         }
         catch (Exception e)
         {
            e.printStackTrace();
            MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mbox.setMessage(e.getLocalizedMessage());
            mbox.open();
            return null;
         }
      }
      else
      {
         CTabFolder folder = tabFolders.get(tabPage.getPlace());
         for (CTabItem tabItem: folder.getItems())
         {
            if (tabItem.getControl() == tabPage)
            {
               folder.setSelection(tabItem);
               return tabPage;
            }
         }
      }
   
      CTabFolder folder = tabFolders.get(tabPage.getPlace());
      if (folder == null) folder = tabFolders.get(SWT.CENTER);
      tabPage.setParent(folder);

      final CTabItem tabItem = new CTabItem(folder, SWT.CLOSE);
      tabItem.setText(tabPage.getTitle());
      tabItem.setControl(tabPage);
      folder.setSelection(tabItem);

      tabPages.put(ident, tabPage);

      return tabPage;
   }

   /**
    * Inform the main window that a tab page was closed. Called by the
    * dispose method of the tab page.
    */
   protected void tabPageDisposed(TabPage tabPage)
   {
      tabPages.remove(tabPage.getData());
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
    * @return the menu-bar.
    */
   public Menu getMenuBar()
   {
      return menuBar;
   }

}
