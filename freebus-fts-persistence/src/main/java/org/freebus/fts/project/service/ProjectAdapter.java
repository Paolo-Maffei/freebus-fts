package org.freebus.fts.project.service;

import org.freebus.fts.project.Project;

/**
 * Adapter class that implements the {@link ProjectListener}
 * interface. All methods are empty.
 */
public class ProjectAdapter implements ProjectListener
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void projectChanged(Project project)
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void projectComponentAdded(Object obj)
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void projectComponentRemoved(Object obj)
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void projectComponentModified(Object obj)
   {
   }
}
