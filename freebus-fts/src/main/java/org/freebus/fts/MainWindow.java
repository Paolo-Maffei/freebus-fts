package org.freebus.fts;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.freebus.fts.actions.Actions;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.JobQueueView;
import org.freebus.fts.components.LogLine;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.components.WorkBench;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.dialogs.Dialogs;
import org.freebus.fts.pages.LogicalView;
import org.freebus.fts.pages.PhysicalView;
import org.freebus.fts.pages.TopologyView;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectAdapter;
import org.freebus.knxcomm.jobs.JobQueue;
import org.freebus.knxcomm.jobs.JobQueueErrorEvent;
import org.freebus.knxcomm.jobs.JobQueueEvent;
import org.freebus.knxcomm.jobs.JobQueueListener;

/**
 * The main application window.
 */
public final class MainWindow extends WorkBench implements JobQueueListener
{
   private static final long serialVersionUID = 4384074439505445519L;
   private static MainWindow instance;

   private final JobQueueView jobQueueView;
   private Timer tmrJobQueueView;

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

      setTitle(FTS.getInstance().getName());
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      final ImageIcon appIcon = ImageCache.getIcon("app-icon");
      if (appIcon != null)
         setIconImage(appIcon.getImage());

      createMenuBar();
      createToolBar();

      getStatusBar().add(new LogLine());

      jobQueueView = new JobQueueView();
      jobQueueView.setVisible(false);
      setBottomLeftPanel(jobQueueView);

      JobQueue.getDefaultJobQueue().addListener(this);

      AbstractPage startPage;
      startPage = showPage(TopologyView.class, null);
      showPage(PhysicalView.class, null);
      showPage(LogicalView.class, null);

      ProjectManager.addListener(new ProjectAdapter()
      {
         @Override
         public void projectChanged(Project project)
         {
            if (project == null)
               setTitle(FTS.getInstance().getName());
            else setTitle(project.getName() + " - " + FTS.getInstance().getName());
         }
      });

      setSelectedPage(startPage);

      final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setSize((int) (screenSize.width * 0.9), (int) (screenSize.height * 0.9));
   }

   /**
    * Create the menu-bar.
    */
   private void createMenuBar()
   {
      final JMenu fileMenu = createJMenu(I18n.getMessage("MainWindow.FileMenu"));
      Actions.NEW_PROJECT.addTo(fileMenu);
      Actions.OPEN_PROJECT.addTo(fileMenu);
      Actions.SAVE_PROJECT.addTo(fileMenu);
      fileMenu.addSeparator();
      Actions.PROJECT_PROPERTIES.addTo(fileMenu);
      fileMenu.addSeparator();
      Actions.RESTART.addTo(fileMenu);
      Actions.EXIT.addTo(fileMenu);

      final JMenu productsMenu = createJMenu(I18n.getMessage("MainWindow.ProductsMenu"));
      Actions.IMPORT_PRODUCTS.addTo(productsMenu);
      productsMenu.addSeparator();
      Actions.BROWSE_PRODUCTS_VDX.addTo(productsMenu);
      Actions.INSPECT_VDX_FILE.addTo(productsMenu);

      final JMenu viewMenu = createJMenu(I18n.getMessage("MainWindow.ViewMenu"));
      Actions.LOGICAL_VIEW.addTo(viewMenu);
      Actions.PHYSICAL_VIEW.addTo(viewMenu);
      Actions.TOPOLOGY_VIEW.addTo(viewMenu);

      final JMenu toolsMenu = createJMenu(I18n.getMessage("MainWindow.ToolsMenu"));
      Actions.BUS_MONITOR.addTo(toolsMenu);
      Actions.BUS_TRACE_VIEWER.addTo(toolsMenu);
      viewMenu.addSeparator();
      Actions.SET_PHYSICAL_ADDRESS.addTo(toolsMenu);
      Actions.DEVICE_SCANNER.addTo(toolsMenu);
//      Actions.DEVICE_STATUS.addTo(toolsMenu);

      final JMenu settingsMenu = createJMenu(I18n.getMessage("MainWindow.SettingsMenu"));
      Actions.SETTINGS.addTo(settingsMenu);

      final JMenu helpMenu = createJMenu(I18n.getMessage("MainWindow.HelpMenu"));
      Actions.ABOUT.addTo(helpMenu);
   }

   /**
    * Create the tool-bar.
    */
   private void createToolBar()
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
      Actions.BUS_TRACE_VIEWER.addTo(toolBar);
      Actions.ADD_DEVICES.addTo(toolBar);
      Actions.SETTINGS.addTo(toolBar);

//      toolBar.addSeparator();
//      Actions.DEVICE_STATUS.addTo(toolBar);
   }

   /**
    * Callback: A job-queue event occurred.
    */
   @Override
   public synchronized void jobQueueEvent(JobQueueEvent event)
   {
      if (event.job != null)
      {
         if (tmrJobQueueView != null)
         {
            tmrJobQueueView.cancel();
            tmrJobQueueView = null;
         }

         if (!jobQueueView.isVisible())
            jobQueueView.setVisible(true);

         if (event.message != null)
            Logger.getLogger(getClass()).info(event.job.getLabel() + ": " + event.message);
      }

      jobQueueView.jobQueueEvent(event);

      if (event.job == null)
      {
         // Hide the job-queue view after some seconds of inactivity
         tmrJobQueueView = new Timer();
         tmrJobQueueView.schedule(new TimerTask()
         {
            @Override
            public void run()
            {
               SwingUtilities.invokeLater(new Runnable()
               {
                  @Override
                  public void run()
                  {
                     jobQueueView.setVisible(false);
                  }
               });
               tmrJobQueueView = null;
            }
         }, 5000);
      }
   }

   /**
    * Callback: A job-queue error occurred.
    */
   @Override
   public void jobQueueErrorEvent(final JobQueueErrorEvent event)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            if (event.exception == null)
               Dialogs.showErrorDialog(event.message);
            else Dialogs.showExceptionDialog(event.exception, event.message);
         }
      });
   }
}
