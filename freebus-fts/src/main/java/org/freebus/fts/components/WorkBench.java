package org.freebus.fts.components;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.freebus.fts.actions.Actions;
import org.freebus.fts.core.I18n;
import org.freebus.fts.dialogs.Dialogs;

/**
 * A class for main application windows. This class contains no FTS specific code.
 * 
 * This class is meant to be sub-classed.
 */
public class WorkBench extends JFrame
{
   private static final long serialVersionUID = 6080914627731378282L;

   private final JMenuBar menuBar = new JMenuBar();
   private final ExtTabbedPane leftTabbedPane, centerTabbedPane;
   private final JSplitPane leftCenterSplit;
   private final JPanel bottomLeftPanel;

   private final Map<Class<? extends AbstractPage>, AbstractPage> uniquePages = new HashMap<Class<? extends AbstractPage>, AbstractPage>();

   /**
    * Create a workbench window.
    */
   public WorkBench()
   {
      try
      {
         UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
      }
      catch (Exception e)
      {
         // Never mind if this does not work
      }

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setMinimumSize(new Dimension(600, 400));
      setLayout(new BorderLayout(2, 2));
      setJMenuBar(menuBar);

      final JPanel leftPanel = new JPanel();
      leftPanel.setLayout(new BorderLayout());
      
      leftTabbedPane = new ExtTabbedPane();
      leftTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      leftPanel.add(leftTabbedPane, BorderLayout.CENTER);

      bottomLeftPanel = new JPanel();
      bottomLeftPanel.setLayout(new BorderLayout());
      leftPanel.add(bottomLeftPanel, BorderLayout.SOUTH);
      
      centerTabbedPane = new ExtTabbedPane();
      centerTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

      leftCenterSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerTabbedPane);
      add(leftCenterSplit, BorderLayout.CENTER);
      leftCenterSplit.setOneTouchExpandable(true);
      leftCenterSplit.setDividerLocation(250);
      leftCenterSplit.setContinuousLayout(true);
      leftCenterSplit.setFocusable(false);

      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent event)
         {
            Actions.EXIT.action.actionPerformed(null);
         }
      });
   }

   /**
    * Create a new menu with the given name and add it to the menu bar. Mnemonics are
    * properly detected if they are marked with an ampersand in the name (e.g. "&File").
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
      if (mnemonic != 0) menu.setMnemonic(mnemonic);

      menuBar.add(menu);
      return menu;
   }

   /**
    * Add the given page to the work-bench and show it. When the page is created and
    * visible, the page's {@link AbstractPage#setObject(Object)} is called.
    * 
    * @param page - the page to show.
    * @param obj - the object that is given to the page via
    *           {@link AbstractPage#setObject(Object)}.
    * @return true if the page was successfully added.
    */
   public synchronized boolean addPage(AbstractPage page, Object obj)
   {
      if (!addPage(page)) return false;
      setPageObject(page, obj);
      return true;
   }

   /**
    * Add the given page to the work-bench and show it.
    * 
    * @param page - the page to show.
    * @return true if the page was successfully added.
    */
   public synchronized boolean addPage(AbstractPage page)
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
         Dialogs.showExceptionDialog(e, I18n.formatMessage("WorkBench.errAddPage", new Object[] { page.getClass()
               .getName() }));
         return false;
      }

      page.setVisible(true);
      pane.setSelectedComponent(page);

      return true;
   }

   /**
    * Create or show the page with the given class. Set the object that the page shall
    * display to the object displayedObject.
    */
   public synchronized void showUniquePage(Class<? extends AbstractPage> pageClass, final Object obj)
   {
      AbstractPage page = uniquePages.get(pageClass);
      if (page == null)
      {
         try
         {
            page = pageClass.newInstance();
            if (!addPage(page)) return;

            uniquePages.put(pageClass, page);
         }
         catch (Exception e)
         {
            Dialogs.showExceptionDialog(e, I18n.formatMessage("WorkBench.errCreatePage", new Object[] { pageClass
                  .getName() }));
            return;
         }
      }
      else
      {
         if (!addPage(page)) return;
      }

      page.setVisible(true);
      setPageObject(page, obj);
   }

   /**
    * Raise the page so that it is visible.
    */
   public void setSelectedPage(AbstractPage page)
   {
      if (leftTabbedPane.indexOfComponent(page) >= 0) leftTabbedPane.setSelectedComponent(page);
      else if (centerTabbedPane.indexOfComponent(page) >= 0) centerTabbedPane.setSelectedComponent(page);
   }

   /**
    * @return The page object for the given class, or null if the page is currently not opened.
    */
   public AbstractPage getUniquePage(Class<? extends AbstractPage> pageClass)
   {
      return uniquePages.get(pageClass);
   }

   /**
    * Set the page-object of the given page. The actual setting of the object happens after all
    * pending Swing events are processed.
    * 
    * @param page - the page to process.
    * @param obj - the object to set.
    */
   protected synchronized void setPageObject(final AbstractPage page, final Object obj)
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
    * Set the panel that is shown in the south/west (bottom-left) corner of the workbench.
    */
   public void setBottomLeftPanel(JPanel panel)
   {
      bottomLeftPanel.removeAll();
      bottomLeftPanel.add(panel, BorderLayout.CENTER);
   }
}
