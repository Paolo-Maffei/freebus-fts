package org.freebus.fts;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JToolBar;

import org.freebus.fts.actions.Actions;
import org.freebus.fts.components.JobQueueView;
import org.freebus.fts.components.LogLine;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.components.WorkBench;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.jobs.JobQueue;
import org.freebus.fts.jobs.JobQueueEvent;
import org.freebus.fts.jobs.JobQueueListener;
import org.freebus.fts.pages.LogicalView;
import org.freebus.fts.pages.PhysicalView;
import org.freebus.fts.pages.TopologyView;
import org.freebus.fts.persistence.db.DatabaseResources;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectListener;
import org.freebus.fts.project.ProjectManager;

/**
 * The main application window.
 */
public final class MainWindow extends WorkBench implements JobQueueListener, ProjectListener
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

      getStatusBar().add(new LogLine());
      Logger.getAnonymousLogger().info("Initializing...");

      jobQueueView = new JobQueueView();
      jobQueueView.setVisible(false);
      setBottomLeftPanel(jobQueueView);

      JobQueue.getDefaultJobQueue().addListener(this);

      showUniquePage(TopologyView.class, null);
      showUniquePage(PhysicalView.class, null);
      showUniquePage(LogicalView.class, null);

      ProjectManager.addListener(this);

      setSelectedPage(getUniquePage(TopologyView.class));

      Dimension size = Config.getInstance().getMainWindowSize();
      if (size == null)
      {
         final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         size = new Dimension((int) (screenSize.width * 0.9), (int) (screenSize.height * 0.9));
      }
      setSize(size);

      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent event)
         {
            final Config cfg = Config.getInstance();
            cfg.setMainWindowSize(getSize());

            DatabaseResources.close();

            cfg.save();
         }
      });
   }

   /**
    * Create the menu-bar.
    */
   protected void createMenuBar()
   {
      final JMenu fileMenu = createJMenu(I18n.getMessage("MainWindow.FileMenu"));
      Actions.NEW_PROJECT.addTo(fileMenu);
      Actions.OPEN_PROJECT.addTo(fileMenu);
      Actions.SAVE_PROJECT.addTo(fileMenu);
      fileMenu.addSeparator();
      Actions.PROJECT_PROPERTIES.addTo(fileMenu);
      fileMenu.addSeparator();
      Actions.EXIT.addTo(fileMenu);

      final JMenu productsMenu = createJMenu(I18n.getMessage("MainWindow.ProductsMenu"));
      Actions.IMPORT_PRODUCTS.addTo(productsMenu);
      Actions.BROWSE_PRODUCTS_VDX.addTo(productsMenu);

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

      final JToolBar toolBar = new ToolBar();
      content.add(toolBar, BorderLayout.NORTH);

      Actions.EXIT.addTo(toolBar);
      toolBar.addSeparator();

      Actions.NEW_PROJECT.addTo(toolBar);
      Actions.OPEN_PROJECT.addTo(toolBar);
      Actions.SAVE_PROJECT.addTo(toolBar);
      toolBar.addSeparator();

      Actions.BUS_MONITOR.addTo(toolBar);
      Actions.BROWSE_PRODUCTS_VDX.addTo(toolBar);
      Actions.SETTINGS.addTo(toolBar);

      toolBar.addSeparator();
      Actions.SEND_TEST_TELEGRAM.addTo(toolBar);
   }

   /**
    * Update the contents of the main window.
    * Call this method if the active project was changed.
    */

   /**
    * Callback: A job-queue event occurred.
    */
   @Override
   public void jobQueueEvent(JobQueueEvent event)
   {
      if (!jobQueueView.isVisible())
         jobQueueView.setVisible(true);
   }

   /**
    * Callback: The active project was changed.
    */
   @Override
   public void projectChange(Project project)
   {
      updateContents();
      setTitle(I18n.formatMessage("MainWindow.Title", new Object[] { project.getName() }));
   }
}
