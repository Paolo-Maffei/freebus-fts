package org.freebus.fts.project.service;

import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

/**
 * Interface for classes that want to be informed when the global project
 * or anything important inside the project changes.
 *
 * @see ProjectManager#addListener(ProjectListener)
 */
public interface ProjectListener
{
   /**
    * The (global) project has changes or was changed.
    * 
    * @param project - the project that has changes.
    */
   public void projectChanged(Project project);

   /**
    * Something was added to the project.
    * 
    * @param obj - the object that was added.
    */
   public void projectComponentAdded(Object obj);

   /**
    * Something was removed from the project.
    * 
    * @param obj - the object that is to be removed.
    */
   public void projectComponentRemoved(Object obj);

   /**
    * Something in the project was modified.
    * 
    * @param obj - the object that was modified.
    */
   public void projectComponentModified(Object obj);
}
