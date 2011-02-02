package org.freebus.fts.client.application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.freebus.fts.client.components.JobQueueView;
import org.freebus.fts.client.core.XmlMenuFactory;
import org.freebus.fts.client.core.XmlToolBarFactory;
import org.freebus.fts.client.views.LogicalView;
import org.freebus.fts.client.views.PhysicalView;
import org.freebus.fts.client.views.TopologyView;
import org.freebus.fts.client.workbench.WorkBench;
import org.freebus.fts.common.exception.FtsRuntimeException;
import org.freebus.fts.elements.ApplicationWindow;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.elements.components.LogLine;
import org.freebus.fts.elements.services.ImageCache;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.service.ProjectAdapter;
import org.freebus.fts.service.job.JobQueue;
import org.freebus.fts.service.job.JobQueueListener;
import org.freebus.fts.service.job.event.JobQueueErrorEvent;
import org.freebus.fts.service.job.event.JobQueueEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * The main application window.
 */
@Component
@Lazy
public final class MainWindow extends WorkBench implements JobQueueListener
{
   private static final long serialVersionUID = 4384074439505445519L;

   private JobQueueView jobQueueView;
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
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   }

   /**
    * Setup the object.
    */
   @PostConstruct
   protected void postConstruct()
   {
      final ImageIcon appIcon = ImageCache.getIcon("misc/app-icon");
      if (appIcon != null)
         setIconImage(appIcon.getImage());

//    setTitle(application.getName());

      createMenuBar();
      createToolBar();

      getStatusBar().add(new LogLine());

      jobQueueView = new JobQueueView();
      jobQueueView.setVisible(false);
      setBottomLeftPanel(jobQueueView);

      JobQueue.getDefaultJobQueue().addListener(this);

      final TopologyView topologyView = new TopologyView();
      addPanel(topologyView);
      showPanel(new PhysicalView());
      showPanel(new LogicalView());
      showPanel(topologyView);

      ProjectManager.addListener(new ProjectAdapter()
      {
         @Override
         public void projectChanged(Project project)
         {
            if (project == null)
               setTitle(application.getName());
            else setTitle(project.getName() + " - " + application.getName());
         }

         @Override
         public void projectComponentRemoved(Object obj)
         {
            objectRemoved(obj);
         }
      });

      final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setSize((int) (screenSize.width * 0.8), (int) (screenSize.height * 0.9));
   }

   /**
    * Create the menu-bar.
    * @throws IOException 
    */
   private void createMenuBar()
   {
      final XmlMenuFactory menuFactory = new XmlMenuFactory();
      final ClassPathResource menuResource = new ClassPathResource("menubars/main.xml");

      try
      {
         setJMenuBar(menuFactory.createMenuBar(menuResource.getFile()));
      }
      catch (IOException e)
      {
         throw new FtsRuntimeException(e);
      }
   }

   /**
    * Create the tool-bar.
    */
   private void createToolBar()
   {
      final XmlToolBarFactory toolBarFactory = new XmlToolBarFactory();
      final ClassPathResource toolBarResource = new ClassPathResource("toolbars/main.xml");

      try
      {
         final Container content = getContentPane();
         content.add(toolBarFactory.createToolBar(toolBarResource.getFile()), BorderLayout.NORTH);
      }
      catch (IOException e)
      {
         throw new FtsRuntimeException(e);
      }
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
