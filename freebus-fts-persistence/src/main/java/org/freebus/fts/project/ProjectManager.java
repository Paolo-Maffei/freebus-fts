package org.freebus.fts.project;

import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.freebus.fts.project.service.ProjectController;
import org.freebus.fts.project.service.ProjectFactory;
import org.freebus.fts.project.service.ProjectListener;
import org.freebus.fts.project.service.jpa.JpaProjectFactory;

/**
 * A static manager that holds the global (default) project instance.
 */
public final class ProjectManager
{
   static private Project project = new Project();
   static private ProjectController controller;
   static private final CopyOnWriteArraySet<ProjectListener> listeners = new CopyOnWriteArraySet<ProjectListener>();
   static private ProjectFactory projectFactory = new JpaProjectFactory();

   /**
    * Set the global project instance. Informs all listeners.
    */
   static public void setProject(Project project)
   {
      ProjectManager.project = project;
      fireProjectChanged();
   }

   /**
    * @return the global project instance.
    */
   static public Project getProject()
   {
      return project;
   }

   /**
    * @return the DAO factory for the global project instance.
    */
   static public ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   /**
    * Set the global project controller.
    *
    * @param controller - the project controller to set.
    */
   public static void setController(ProjectController controller)
   {
      ProjectManager.controller = controller;
   }

   /**
    * @return The global project controller.
    */
   public static ProjectController getController()
   {
      return controller;
   }

   /**
    * Register a listener that gets called when the global project changes.
    */
   static public void addListener(ProjectListener listener)
   {
      listeners.add(listener);
   }

   /**
    * Remove a listener.
    */
   static public void removeListener(ProjectListener listener)
   {
      listeners.remove(listener);
   }

   /**
    * Inform all listeners about changes in the project by calling the
    * {@link ProjectListener#projectChanged(Project)} callback method.
    * 
    * @param obj - the object that has changes.
    */
   static public void fireProjectChanged()
   {
      Logger.getLogger(ProjectManager.class).debug("fire project changed");

      for (ProjectListener listener : listeners)
         listener.projectChanged(project);
   }

   /**
    * Inform all listeners about changes in the project by calling the
    * {@link ProjectListener#projectComponentAdded(Object)} callback method.
    * 
    * @param obj - the object that was added.
    */
   static public void fireComponentAdded(Object obj)
   {
      Logger.getLogger(ProjectManager.class).debug("fire added: " + obj);

      for (ProjectListener listener : listeners)
         listener.projectComponentAdded(obj);
   }

   /**
    * Inform all listeners about changes in the project by calling the
    * {@link ProjectListener#projectComponentModified(Object)} callback method.
    * 
    * @param obj - the object that was modified.
    */
   static public void fireComponentModified(Object obj)
   {
      Logger.getLogger(ProjectManager.class).debug("fire modified: " + obj);

      for (ProjectListener listener : listeners)
         listener.projectComponentModified(obj);
   }

   /**
    * Inform all listeners about changes in the project by calling the
    * {@link ProjectListener#projectComponentRemoved(Object)} callback method.
    * 
    * @param obj - the object that was removed.
    */
   static public void fireComponentRemoved(Object obj)
   {
      Logger.getLogger(ProjectManager.class).debug("fire removed: " + obj);

      for (ProjectListener listener : listeners)
         listener.projectComponentRemoved(obj);
   }
}
