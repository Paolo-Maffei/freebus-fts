package org.freebus.fts.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.freebus.fts.actions.Actions;
import org.freebus.fts.client.app.Application;
import org.freebus.fts.client.core.I18n;
import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.JobQueueView;
import org.freebus.fts.components.WorkBench;
import org.freebus.fts.elements.ApplicationWindow;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.components.LogLine;
import org.freebus.fts.elements.components.ToolBar;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectAdapter;
import org.freebus.fts.service.job.JobQueue;
import org.freebus.fts.service.job.JobQueueListener;
import org.freebus.fts.service.job.event.JobQueueErrorEvent;
import org.freebus.fts.service.job.event.JobQueueEvent;
import org.freebus.fts.view.LogicalView;
import org.freebus.fts.view.PhysicalView;
import org.freebus.fts.view.TopologyView;
import org.springframework.stereotype.Component;

/**
 * The main application window.
 */
@Component
public final class MainWindow extends WorkBench implements JobQueueListener
{
   private static final long serialVersionUID = 4384074439505445519L;

   private final JobQueueView jobQueueView;
   private Timer tmrJobQueueView;

   @Inject
   Application application;
   
   /**
    * @return the global {@link MainWindow} instance.
    */
   public static MainWindow getInstance()
   {
      return (MainWindow) ApplicationWindow.getInstance();
   }

   /**
    * Create a main window. Calls {@link #setInstance}.
    */
   public MainWindow()
   {
      super();
      setInstance(this);

//      setTitle(application.getName());
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
               setTitle(application.getName());
            else setTitle(project.getName() + " - " + application.getName());
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

//         if (event.message != null)
//            Logger.getLogger(getClass()).info(event.job.getLabel() + ": " + event.message);
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
