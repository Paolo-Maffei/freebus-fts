package org.freebus.fts.project;

import java.util.concurrent.CopyOnWriteArraySet;

import org.freebus.fts.project.service.ProjectFactory;
import org.freebus.fts.project.service.jpa.JpaProjectFactory;

/**
 * A static manager that holds the global (default) project instance.
 */
public final class ProjectManager
{
   static private Project project = new Project();
   static private final CopyOnWriteArraySet<ProjectListener> listeners = new CopyOnWriteArraySet<ProjectListener>();
   static private ProjectFactory projectFactory = new JpaProjectFactory();

   /**
    * Set the global project instance.
    * Informs all listeners.
    */
   static public void setProject(Project project)
   {
      ProjectManager.project = project;
      fireProjectChange();
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
   static public ProjectFactory getDAOFactory()
   {
      return projectFactory;
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
    * Inform all listeners that the global project has changed or got important changes.
    */
   static public void fireProjectChange()
   {
      for (ProjectListener listener: listeners)
         listener.projectChange(project);
   }
}
