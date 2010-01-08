package org.freebus.fts.project;

/**
 * Interface for classes that want to be informed when the global project
 * changes.
 * 
 * @see {@link ProjectManager#addListener}.
 */
public interface ProjectListener
{
   /**
    * The global project has changes or was changed.
    */
   public void projectChange(Project project);
}
