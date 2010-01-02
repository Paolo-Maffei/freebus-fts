package org.freebus.fts;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JMenu;
import javax.swing.JToolBar;

import org.freebus.fts.actions.Actions;
import org.freebus.fts.components.JobQueueView;
import org.freebus.fts.components.WorkBench;
import org.freebus.fts.core.I18n;
import org.freebus.fts.jobs.JobQueue;
import org.freebus.fts.jobs.JobQueueEvent;
import org.freebus.fts.jobs.JobQueueListener;
import org.freebus.fts.pages.LogicalView;
import org.freebus.fts.pages.PhysicalView;
import org.freebus.fts.pages.TopologyView;

/**
 * The main application window.
 */
public final class MainWindow extends WorkBench implements JobQueueListener
{
   private static final long serialVersionUID = 4384074439505445519L;
   private static MainWindow instance;

   private final JobQueueView jobQueueView;

   /**
    * @return the global {@link MainWindow} instance.
    */
   public static MainWindow getInstance()
   {
      return instance;
   }

   /**
    * Set the global {@link MainWindow} instance.
    */
   public static void setInstance(MainWindow mainWindow)
   {
      instance = mainWindow;
   }

   /**
    * Create a main window. Calls {@link #setInstance}.
    */
   public MainWindow()
   {
      super();
      setInstance(this);

      setTitle(I18n.getMessage("MainWindow.Title"));

      createMenuBar();
      createToolBar();

      jobQueueView = new JobQueueView();
      jobQueueView.setVisible(false);
      setBottomLeftPanel(jobQueueView);

      JobQueue.getDefaultJobQueue().addListener(this);

      showUniquePage(TopologyView.class, null);
      showUniquePage(PhysicalView.class, null);
      showUniquePage(LogicalView.class, null);

      setSelectedPage(getUniquePage(TopologyView.class));

      final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setSize(new Dimension((int) (screenSize.width * 0.9), (int) (screenSize.height * 0.9)));
   }

   /**
    * Create the menu-bar.
    */
   protected void createMenuBar()
   {
      final JMenu fileMenu = createJMenu(I18n.getMessage("MainWindow.FileMenu"));
      Actions.EXIT.addTo(fileMenu);

      final JMenu viewMenu = createJMenu(I18n.getMessage("MainWindow.ViewMenu"));
      Actions.BUS_MONITOR.addTo(viewMenu);
      viewMenu.addSeparator();
      Actions.LOGICAL_VIEW.addTo(viewMenu);
      Actions.PHYSICAL_VIEW.addTo(viewMenu);
      Actions.TOPOLOGY_VIEW.addTo(viewMenu);

      final JMenu settingsMenu = createJMenu(I18n.getMessage("MainWindow.SettingsMenu"));
      Actions.SETTINGS.addTo(settingsMenu);
   }

   /**
    * Create the tool-bar.
    */
   protected void createToolBar()
   {
      final Container content = getContentPane();

      final JToolBar toolBar = new JToolBar();
      content.add(toolBar, BorderLayout.NORTH);

      Actions.EXIT.addTo(toolBar);
      Actions.BUS_MONITOR.addTo(toolBar);
      Actions.BROWSE_PRODUCTS_VDX.addTo(toolBar);
      Actions.SETTINGS.addTo(toolBar);

      toolBar.addSeparator();
      Actions.SEND_TEST_TELEGRAM.addTo(toolBar);
   }

   /**
    * Callback: a job-queue event occurred.
    */
   @Override
   public void jobQueueEvent(JobQueueEvent event)
   {
      if (!jobQueueView.isVisible())
         jobQueueView.setVisible(true);
   }
}
