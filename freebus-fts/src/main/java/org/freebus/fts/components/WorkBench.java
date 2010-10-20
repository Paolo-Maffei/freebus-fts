package org.freebus.fts.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.freebus.fts.I18n;
import org.freebus.fts.core.WorkBenchPageId;
import org.freebus.fts.elements.ApplicationWindow;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.components.ExtTabbedPane;

/**
 * A class for main application windows. This class contains no FTS specific
 * code. This class is meant to be sub-classed.
 */
public class WorkBench extends ApplicationWindow
{
   private static final long serialVersionUID = 6080914627731378282L;

   private final JMenuBar menuBar = new JMenuBar();
   private final ExtTabbedPane leftTabbedPane, centerTabbedPane;
   private final JSplitPane leftCenterSplit;
   private final JPanel bottomLeftPanel;
   private final StatusBar statusBar = new StatusBar();

   private final Map<WorkBenchPageId, AbstractPage> pages = new HashMap<WorkBenchPageId, AbstractPage>();

   /**
    * Create a workbench window.
    */
   public WorkBench()
   {
      setMinimumSize(new Dimension(600, 400));
      setLayout(new BorderLayout(2, 2));
      setJMenuBar(menuBar);

      final JPanel leftPanel = new JPanel();
      leftPanel.setLayout(new BorderLayout(0, 0));

      leftTabbedPane = new ExtTabbedPane();
      leftTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      leftTabbedPane.setBorder(BorderFactory.createEmptyBorder());
      leftTabbedPane.addContainerListener(panesContainerAdapter);
      leftPanel.add(leftTabbedPane, BorderLayout.CENTER);

      bottomLeftPanel = new JPanel();
      bottomLeftPanel.setLayout(new BorderLayout(0, 0));
      leftPanel.add(bottomLeftPanel, BorderLayout.SOUTH);

      centerTabbedPane = new ExtTabbedPane();
      centerTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      centerTabbedPane.setBorder(BorderFactory.createEmptyBorder());
      centerTabbedPane.addContainerListener(panesContainerAdapter);

      leftCenterSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerTabbedPane);
      add(leftCenterSplit, BorderLayout.CENTER);
      leftCenterSplit.setOneTouchExpandable(false);
      leftCenterSplit.setDividerLocation(320);
      leftCenterSplit.setResizeWeight(0);
      leftCenterSplit.setDividerSize(6);
      leftCenterSplit.setContinuousLayout(true);
      leftCenterSplit.setFocusable(false);

      add(statusBar, BorderLayout.SOUTH);
   }

   //
   //  Container adapter that removes the page from the map of pages when the page is closed.
   //
   private final ContainerAdapter panesContainerAdapter = new ContainerAdapter()
   {
      @Override
      public void componentRemoved(ContainerEvent e)
      {
         final Component child = e.getChild();
         if (!(child instanceof AbstractPage))
            return;

         pageRemoved((AbstractPage) child);
      }
   };

   /**
    * Add a page to the work-bench and show it.
    * 
    * @param page - the page to add.
    * @return true if the page was successfully added.
    */
   private synchronized boolean addPage(AbstractPage page)
   {
      JTabbedPane pane = null;

      try
      {
         final PagePosition pagePos = page.getPagePosition();

         if (pagePos == PagePosition.CENTER)
            pane = centerTabbedPane;
         else if (pagePos == PagePosition.LEFT)
            pane = leftTabbedPane;
         else throw new Exception("Internal error: invalid page position");

         pane.addTab(page.getName(), page);
      }
      catch (Exception e)
      {
         Dialogs.showExceptionDialog(e,
               I18n.formatMessage("WorkBench.errAddPage", page.getClass().getName()));
         return false;
      }

      page.setVisible(true);
      pane.setSelectedComponent(page);

      return true;
   }

   /**
    * Add a page to the work-bench and show it. When the page is created and
    * visible, the page's {@link AbstractPage#setObject(Object)} is called.
    * 
    * @param page - the page to add.
    * @param obj - the object that is given to the page via
    *           {@link AbstractPage#setObject(Object)}.
    * @return true if the page was successfully added.
    */
   public boolean addPage(AbstractPage page, Object obj)
   {
      if (!addPage(page))
         return false;
      setPageObject(page, obj);
      return true;
   }

   /**
    * Remove a page from the work-bench's pages container.
    * Called by the event handler when a page is removed.
    * 
    * @param page - the page that is removed.
    */
   private synchronized void pageRemoved(AbstractPage page)
   {
      page.closeEvent();

      for (Entry<WorkBenchPageId,AbstractPage> entry: pages.entrySet())
      {
         if (entry.getValue() == page)
         {
            pages.remove(entry.getKey());
            break;
         }
      }
   }

   /**
    * Create a new menu with the given name and add it to the menu bar.
    * Mnemonics are properly detected if they are marked with an ampersand in
    * the name (e.g. "&File").
    */
   public JMenu createJMenu(String name)
   {
      final int idx = name.indexOf('&');
      char mnemonic = 0;
      if (idx >= 0)
      {
         mnemonic = name.charAt(idx + 1);
         name = name.substring(0, idx) + name.substring(idx + 1);
      }

      final JMenu menu = new JMenu(name);
      if (mnemonic != 0)
         menu.setMnemonic(mnemonic);

      menuBar.add(menu);
      return menu;
   }

   /**
    * @return the status bar widget.
    */
   public StatusBar getStatusBar()
   {
      return statusBar;
   }

   /**
    * Lookup a page.
    * 
    * @param pageClass - the class of the page that is searched.
    * @param obj - the object of the page that is searched.
    * 
    * @return The page object for the given class/object, or null if the page is
    *         currently not opened.
    */
   public AbstractPage getPage(final Class<? extends AbstractPage> pageClass, final Object obj)
   {
      return pages.get(new WorkBenchPageId(pageClass, obj));
   }

   /**
    * Set the panel that is shown in the south/west (bottom-left) corner of the
    * workbench.
    */
   public void setBottomLeftPanel(JPanel panel)
   {
      bottomLeftPanel.removeAll();
      bottomLeftPanel.add(panel, BorderLayout.CENTER);
   }

   /**
    * Set the page-object of the given page. The actual setting of the object
    * happens after all pending Swing events are processed.
    * 
    * @param page - the page to process.
    * @param obj - the object to set.
    */
   private synchronized void setPageObject(final AbstractPage page, final Object obj)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               page.setObject(obj);
            }
            catch (Exception e)
            {
               Dialogs.showExceptionDialog(e, "");
            }
            finally
            {
               setCursor(Cursor.getDefaultCursor());
            }
         }
      });
   }

   /**
    * Raise the page so that it is visible.
    */
   public void setSelectedPage(AbstractPage page)
   {
      if (leftTabbedPane.indexOfComponent(page) >= 0)
         leftTabbedPane.setSelectedComponent(page);
      else if (centerTabbedPane.indexOfComponent(page) >= 0)
         centerTabbedPane.setSelectedComponent(page);
   }

   /**
    * Create or show the page with the given class and object. Set the object
    * that the page shall display to the object displayedObject.
    *
    * @param pageClass - the class of the page to show.
    * @param obj - the object that is displayed in the page. May be null.
    * 
    * @return the shown page.
    */
   public synchronized AbstractPage showPage(Class<? extends AbstractPage> pageClass, final Object obj)
   {
      final WorkBenchPageId key = new WorkBenchPageId(pageClass, obj);
      AbstractPage page = pages.get(key);
      if (page == null)
      {
         try
         {
            page = pageClass.newInstance();
            if (!addPage(page, obj))
               return null;

            pages.put(key, page);
         }
         catch (Exception e)
         {
            Dialogs.showExceptionDialog(e,
                  I18n.formatMessage("WorkBench.errCreatePage", pageClass.getName()));
            return null;
         }
      }

      final PagePosition pagePos = page.getPagePosition();

      JTabbedPane pane = null;
      if (pagePos == PagePosition.CENTER)
         pane = centerTabbedPane;
      else if (pagePos == PagePosition.LEFT)
         pane = leftTabbedPane;
      else throw new RuntimeException("Internal error: invalid page position");

      page.setVisible(true);
      pane.setSelectedComponent(page);

      return page;
   }

   /**
    * Call {@link AbstractPage#updateContents()} for all pages.
    */
   public void updateContents()
   {
      for (Entry<WorkBenchPageId,AbstractPage> entry: pages.entrySet())
         entry.getValue().updateContents();
   }
}
