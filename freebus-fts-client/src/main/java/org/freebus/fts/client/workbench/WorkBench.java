package org.freebus.fts.client.workbench;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.client.core.InteractiveProjectController;
import org.freebus.fts.elements.AbstractApplicationWindow;
import org.freebus.fts.elements.components.ExtTabbedPane;

/**
 * A class for main application windows. This class contains no FTS specific
 * code. This class is meant to be sub-classed.
 */
public class WorkBench extends AbstractApplicationWindow
{
   private static final long serialVersionUID = 6080914627731378282L;

   private final JMenuBar menuBar = new JMenuBar();
   private final ExtTabbedPane leftTabbedPane, centerTabbedPane;
   private final JSplitPane leftCenterSplit;
   private final JPanel bottomLeftPanel;
   private final StatusBar statusBar = new StatusBar();
   private final Map<EditorKey, WorkBenchEditor> editors = new HashMap<EditorKey, WorkBenchEditor>();

   // private final Map<WorkBenchPageId, WorkBenchPanel> pages = new
   // HashMap<WorkBenchPageId, WorkBenchPanel>();

   /**
    * @return the global {@link WorkBench} instance.
    */
   public static WorkBench getInstance()
   {
      return (WorkBench) AbstractApplicationWindow.getInstance();
   }

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
      // leftTabbedPane.addContainerListener(panesContainerAdapter);
      leftPanel.add(leftTabbedPane, BorderLayout.CENTER);

      bottomLeftPanel = new JPanel();
      bottomLeftPanel.setLayout(new BorderLayout(0, 0));
      leftPanel.add(bottomLeftPanel, BorderLayout.SOUTH);

      centerTabbedPane = new ExtTabbedPane();
      centerTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
      centerTabbedPane.setBorder(BorderFactory.createEmptyBorder());
      // centerTabbedPane.addContainerListener(panesContainerAdapter);

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

   /**
    * Add a {@link WorkBenchPanel panel} to the workbench. This is usually
    * either a {@link WorkBenchView view} or an {@link WorkBenchEditor editor}.
    * <p>
    * The panel is automatically added at a suitable place within the workbench.
    * 
    * @param panel - the panel to add.
    * 
    * @see WorkBenchEditor
    * @see WorkBenchView
    */
   public synchronized void addPanel(WorkBenchPanel panel)
   {
      if (panel instanceof WorkBenchEditor)
      {
         if (centerTabbedPane.indexOfComponent(panel) < 0)
            centerTabbedPane.add(panel);
         else panelChanged(panel);
      }
      else
      {
         if (leftTabbedPane.indexOfComponent(panel) < 0)
            leftTabbedPane.add(panel);
         else panelChanged(panel);
      }
   }

   /**
    * Remove a {@link WorkBenchPanel panel} from the workbench.
    * 
    * @param panel - the panel to remove.
    */
   public synchronized void removePanel(WorkBenchPanel panel)
   {
      final JTabbedPane parent = (JTabbedPane) panel.getParent();
      if (parent == null)
         return;

      synchronized (parent)
      {
         final int tabIndex = parent.indexOfComponent(panel);
         if (tabIndex >= 0)
            parent.removeTabAt(tabIndex);

         panel.setVisible(false);
      }
   }

   /**
    * Make the {@link WorkBenchPanel panel} visible. The panel is added to the
    * work bench if it was not previously added.
    * 
    * @param panel - the panel to show.
    */
   public synchronized void showPanel(WorkBenchPanel panel)
   {
      JTabbedPane parent = (JTabbedPane) panel.getParent();
      if (parent == null)
      {
         addPanel(panel);
         parent = (JTabbedPane) panel.getParent();
      }

      synchronized (parent)
      {
         final int tabIndex = parent.indexOfComponent(panel);

         if (tabIndex < 0)
            parent.addTab(getName(), panel);

         parent.setSelectedComponent(panel);
         panel.setVisible(true);
      }
   }

   /**
    * Find a specific panel.
    * 
    * @param clazz - the class of the panel that is searched.
    * @return The first panel that is found and is of the given class. Null if
    *         no matching panel was found.
    */
   public synchronized <T extends WorkBenchPanel> T findPanel(Class<T> clazz)
   {
      JTabbedPane tabbedPane;

      if (WorkBenchEditor.class.isAssignableFrom(clazz))
      {
         tabbedPane = centerTabbedPane;
      }
      else if (WorkBenchView.class.isAssignableFrom(clazz))
      {
         tabbedPane = leftTabbedPane;
      }
      else
      {
         throw new IllegalArgumentException("Unsupported class argument: " + clazz.getName());
      }

      for (int i = tabbedPane.getComponentCount() - 1; i >= 0; --i)
      {
         final Component component = tabbedPane.getComponent(i);
         if (clazz.isInstance(component))
         {
            @SuppressWarnings("unchecked")
            final T result = (T) component;
            return result;
         }
      }

      return null;
   }

   /**
    * Callback. Called by a {@link WorkBenchPanel} when for example it's name is
    * changed.
    * 
    * @param panel - the panel that was changed.
    */
   synchronized void panelChanged(WorkBenchPanel panel)
   {
      if (!(panel.getParent() instanceof JTabbedPane))
         return;

      final JTabbedPane parent = (JTabbedPane) panel.getParent();
      final int tabIndex = parent.indexOfComponent(this);
      if (tabIndex >= 0)
      {
         parent.setTitleAt(tabIndex, panel.getName());
         parent.getTabComponentAt(tabIndex).setName(panel.getName());
      }
   }

   /**
    * Callback. Called by a {@link WorkBenchPanel} when the panel is closed.
    * 
    * @param panel - the panel that was closed.
    */
   synchronized void panelClosed(WorkBenchPanel panel)
   {
      if (panel instanceof WorkBenchEditor)
      {
         final WorkBenchEditor editor = (WorkBenchEditor) panel;
         editors.remove(new EditorKey(editor.getClass(), editor.getObject()));
      }
   }

   // /**
   // * Add a page to the work-bench and show it.
   // *
   // * @param page - the page to add.
   // * @return true if the page was successfully added.
   // */
   // private synchronized boolean addPage(WorkBenchPanel page)
   // {
   // JTabbedPane pane = null;
   //
   // try
   // {
   // final PagePosition pagePos = page.getPagePosition();
   //
   // if (pagePos == PagePosition.CENTER)
   // pane = centerTabbedPane;
   // else if (pagePos == PagePosition.LEFT)
   // pane = leftTabbedPane;
   // else throw new Exception("Internal error: invalid page position");
   //
   // pane.addTab(page.getName(), page);
   // }
   // catch (Exception e)
   // {
   // Dialogs.showExceptionDialog(e,
   // I18n.formatMessage("WorkBench.errAddPage", page.getClass().getName()));
   // return false;
   // }
   //
   // page.setVisible(true);
   // pane.setSelectedComponent(page);
   //
   // return true;
   // }

   // /**
   // * Add a page to the work-bench and show it. When the page is created and
   // * visible, the page's {@link WorkBenchPanel#setObject(Object)} is called.
   // *
   // * @param page - the page to add.
   // * @param obj - the object that is given to the page via
   // * {@link WorkBenchPanel#setObject(Object)}.
   // * @return true if the page was successfully added.
   // */
   // public boolean addPage(WorkBenchPanel page, Object obj)
   // {
   // if (!addPage(page))
   // return false;
   // setPageObject(page, obj);
   // return true;
   // }

   // /**
   // * Remove a page from the work-bench's pages container.
   // * Called by the event handler when a page is removed.
   // *
   // * @param page - the page that is removed.
   // */
   // private synchronized void pageRemoved(WorkBenchPanel page)
   // {
   // page.closeEvent();
   //
   // for (Entry<WorkBenchPageId,WorkBenchPanel> entry: pages.entrySet())
   // {
   // if (entry.getValue() == page)
   // {
   // pages.remove(entry.getKey());
   // break;
   // }
   // }
   // }

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
    * Set the panel that is shown in the south/west (bottom-left) corner of the
    * workbench.
    */
   public void setBottomLeftPanel(JPanel panel)
   {
      bottomLeftPanel.removeAll();
      bottomLeftPanel.add(panel, BorderLayout.CENTER);
   }

   /**
    * Create or show the editor of the given class. Set the object that the
    * editor shall display to the object obj.
    * 
    * @param clazz - the class of the editor to show.
    * @param obj - the object that is displayed in the editor. May be null.
    * 
    * @return the shown editor.
    * 
    * @see #showUniquePanel(Class)
    */
   public WorkBenchEditor showEditor(Class<? extends WorkBenchEditor> clazz, final Object obj)
   {
      return showEditor(new EditorKey(clazz, obj), obj);
   }

   /**
    * Create or show the unique editor of the given class. Set the object that
    * the editor shall display to the object obj.
    * 
    * @param clazz - the class of the editor to show.
    * @param obj - the object that is displayed in the editor. May be null.
    * 
    * @return the shown editor.
    * 
    * @see #showUniquePanel(Class)
    */
   public WorkBenchEditor showUniqueEditor(Class<? extends WorkBenchEditor> clazz, final Object obj)
   {
      return showEditor(new EditorKey(clazz, null), obj);
   }

   /**
    * Show the editor with the given key and set the object obj to be displayed.
    * 
    * @param key - the key of the requested editor
    * @param obj - the object to show
    * @return The editor.
    */
   protected synchronized WorkBenchEditor showEditor(final EditorKey key, final Object obj)
   {
      WorkBenchEditor editor = editors.get(key);

      if (editor == null)
      {
         final Class<? extends WorkBenchEditor> clazz = key.getClazz();

         try
         {
            editor = clazz.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException(I18n.formatMessage("WorkBench.errCreatePage", clazz.getName()), e);
         }

         editors.put(key, editor);
         editor.setObject(obj);
         addPanel(editor);
      }
      else
      {
         showPanel(editor);
      }

      return editor;
   }

   /**
    * Create or show the unique panel of the given class.
    * 
    * @param editorClass - the class of the editor to show.
    * 
    * @return the shown editor.
    * 
    * @see #showUniqueEditor(Class, Object)
    */
   public synchronized WorkBenchPanel showUniquePanel(Class<? extends WorkBenchPanel> clazz)
   {
      WorkBenchPanel panel = findPanel(clazz);
      if (panel == null)
      {
         try
         {
            panel = clazz.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException(I18n.formatMessage("WorkBench.errCreatePage", clazz.getName()), e);
         }
      }

      showPanel(panel);
      return panel;
   }

   /**
    * Called by the {@link InteractiveProjectController#remove(Object) project controller}
    * when an object is removed.
    * 
    * @param obj - the removed object
    */
   public synchronized void objectRemoved(Object obj)
   {
      for (EditorKey key : editors.keySet())
      {
         if (obj == null ? key.getObj() == null : obj.equals(key.getObj()))
         {
            editors.get(key).close();
         }
      }
   }

   // ...
   // final PagePosition pagePos = page.getPagePosition();
   //
   // JTabbedPane pane = null;
   // if (pagePos == PagePosition.CENTER)
   // pane = centerTabbedPane;
   // else if (pagePos == PagePosition.LEFT)
   // pane = leftTabbedPane;
   // else throw new RuntimeException("Internal error: invalid page position");
   //
   // page.setVisible(true);
   // pane.setSelectedComponent(page);
   //
   // return page;
   // }

   // /**
   // * Call {@link WorkBenchPanel#updateContents()} for all pages.
   // */
   // public void updateContents()
   // {
   // for (Entry<WorkBenchPageId,WorkBenchPanel> entry: pages.entrySet())
   // entry.getValue().updateContents();
   // }
   //
   // //
   // // Container adapter that removes the page from the map of pages when the
   // page is closed.
   // //
   // private final ContainerAdapter panesContainerAdapter = new
   // ContainerAdapter()
   // {
   // @Override
   // public void componentRemoved(ContainerEvent e)
   // {
   // final Component child = e.getChild();
   // if (!(child instanceof WorkBenchPanel))
   // return;
   //
   // pageRemoved((WorkBenchPanel) child);
   // }
   // };
}
