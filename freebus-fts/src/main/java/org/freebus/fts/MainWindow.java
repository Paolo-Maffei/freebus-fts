package org.freebus.fts;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.freebus.fts.actions.Actions;
import org.freebus.fts.components.JobQueueView;
import org.freebus.fts.components.LogLine;
import org.freebus.fts.components.ToolBar;
import org.freebus.fts.components.WorkBench;
import org.freebus.fts.core.Config;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;
import org.freebus.fts.core.ProjectController;
import org.freebus.fts.jobs.JobQueue;
import org.freebus.fts.jobs.JobQueueEvent;
import org.freebus.fts.jobs.JobQueueListener;
import org.freebus.fts.pages.LogicalView;
import org.freebus.fts.pages.PhysicalView;
import org.freebus.fts.pages.TopologyView;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectListener;
import org.freebus.fts.project.ProjectManager;

/**
 * The main application window.
 */
public final class MainWindow extends WorkBench implements JobQueueListener, ProjectListener, ProjectController
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

      setTitle(I18n.getMessage("MainWindow.Title"));
      setIconImage(ImageCache.getIcon("app-icon").getImage());
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      createMenuBar();
      createToolBar();

      getStatusBar().add(new LogLine());

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
            Config.getInstance().setMainWindowSize(getSize());
         }
      });
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
      Actions.BUS_MONITOR.addTo(viewMenu);
      viewMenu.addSeparator();
      Actions.LOGICAL_VIEW.addTo(viewMenu);
      Actions.PHYSICAL_VIEW.addTo(viewMenu);
      Actions.TOPOLOGY_VIEW.addTo(viewMenu);

      final JMenu toolsMenu = createJMenu(I18n.getMessage("MainWindow.ToolsMenu"));
      Actions.SET_PHYSICAL_ADDRESS.addTo(toolsMenu);
      Actions.DEVICE_STATUS.addTo(toolsMenu);

      final JMenu settingsMenu = createJMenu(I18n.getMessage("MainWindow.SettingsMenu"));
      Actions.SETTINGS.addTo(settingsMenu);
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
      Actions.ADD_DEVICES.addTo(toolBar);
      Actions.SETTINGS.addTo(toolBar);

      toolBar.addSeparator();
      Actions.SEND_TEST_TELEGRAM.addTo(toolBar);
      Actions.DEVICE_STATUS.addTo(toolBar);
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
    * Callback: The active project was changed.
    */
   @Override
   public void projectChange(Project project)
   {
      updateContents();
      setTitle(I18n.formatMessage("MainWindow.Title", new Object[] { project.getName() }));
   }

   /**
    * Add a virtual device to the current project.
    *
    * @param virtualDevice the device to add.
    */
   @Override
   public void addDevice(VirtualDevice virtualDevice)
   {
      final Device device = new Device(0, virtualDevice);

      final TopologyView topologyView = (TopologyView) getUniquePage(TopologyView.class);
      if (topologyView == null) return;

      topologyView.addDevice(device);
   }
}
