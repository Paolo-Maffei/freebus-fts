package org.freebus.fts.project.service;

import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;

/**
 * Interface for project controllers. Project controllers manipulate the
 * project, and shall be used for the more complex tasks.
 * 
 * @see ProjectManager#getController()
 */
public interface ProjectController
{
   /**
    * Add a virtual device to the project.
    * 
    * @param dev - the device to add.
    */
   public void add(VirtualDevice dev);

   /**
    * Edit an object. Calls the proper edit method to do the job (e.g.
    * {@link #edit(Device)}). Does nothing and returns false if <code>obj</code>
    * is null or has an unsupported type.
    * 
    * @param obj - the object to edit.
    * 
    * @return true if the object is edited, false if the object is of an
    *         unsupported class.
    */
   public boolean edit(Object obj);

   /**
    * Remove an object. Calls the proper remove method to do the job (e.g.
    * {@link #remove(Device)}). Does nothing and returns false if <code>obj</code>
    * is null or has an unsupported type.
    * 
    * @param obj - the object to remove from the project.
    */
   public boolean remove(Object obj);

   /**
    * The project has changed. Call after the new project has been loaded or
    * created.
    * 
    * @param project - the new project.
    */
   public void projectChanged(Project project);

   /**
    * The parameters of a device have been changed. Update the device parameters
    * and device objects.
    * 
    * @param device - the device.
    */
   public void parametersChanged(Device device);
}
