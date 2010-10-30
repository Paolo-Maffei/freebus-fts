package org.freebus.fts.project.service;

import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;

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
   void add(VirtualDevice dev);

   /**
    * Add a building to the project. The building may have a parent building.
    * 
    * @param building - the building to add.
    */
   void add(Building building);

   /**
    * Add a room to a building.
    * 
    * @param building - the building to add the room to.
    * @param room - the room to add.
    */
   void add(Building building, Room room);

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
   boolean edit(Object obj);

   /**
    * Remove an object. Calls the proper remove method to do the job (e.g.
    * {@link #remove(Device)}). Does nothing and returns false if
    * <code>obj</code> is null or has an unsupported type.
    * 
    * @param obj - the object to remove from the project.
    */
   boolean remove(Object obj);

   /**
    * Program an object. Programs the device, or the devices that the object
    * contains if the object is no device.
    * 
    * @param obj - the object to program.
    */
   void program(Object obj);

   /**
    * The project has changed. Call after the new project has been loaded or
    * created.
    * 
    * @param project - the new project.
    */
   void projectChanged(Project project);

   /**
    * The parameters of a device have been changed. Update the device parameters
    * and device objects.
    * 
    * @param device - the device.
    */
   void parametersChanged(Device device);
}
