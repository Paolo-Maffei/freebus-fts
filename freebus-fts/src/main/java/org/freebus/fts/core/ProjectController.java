package org.freebus.fts.core;

import org.freebus.fts.products.VirtualDevice;

/**
 * Interface for project controllers. Project controllers manipulate the project,
 * and shall be used for the more complex tasks.
 */
public interface ProjectController
{
   /**
    * Add the virtual device <code>dev</code> to the project. 
    */
   public void addDevice(final VirtualDevice dev);
}
