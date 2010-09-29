package org.freebus.fts.project.service;

import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
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
   public void add(VirtualDevice dev);

   /**
    * Edit an object. Calls the proper edit method to do the job
    * (e.g. {@link #edit(Device)}). Does nothing if <pre>obj</pre> is
    * null or has an unsupported type.
    * 
    * @param obj - the object to edit.
    */
   public void edit(Object obj);

   /**
    * Edit an area.
    * 
    * @param area - the area to edit.
    */
   public void edit(Area area);

   /**
    * Edit a line.
    * 
    * @param line - the line to edit.
    */
   public void edit(Line line);

   /**
    * Edit a device.
    * 
    * @param dev - the device to edit.
    */
   public void edit(Device dev);

   /**
    * Edit a building.
    * 
    * @param building - the building to edit.
    */
   public void edit(Building building);

   /**
    * Remove an object. Calls the proper remove method to do the job
    * (e.g. {@link #remove(Device)}). Does nothing if <pre>obj</pre> is
    * null or has an unsupported type.
    * 
    * @param obj - the object to remove from the project.
    */
   public void remove(Object obj);

   /**
    * Remove an area from the project.
    * 
    * @param area - the area to remove.
    */
   public void remove(Area area);

   /**
    * Remove a building from the project.
    * 
    * @param building - the building to remove.
    */
   public void remove(Building building);

   /**
    * Remove a device from the project.
    * 
    * @param dev - the device to remove.
    */
   public void remove(Device dev);

   /**
    * Remove a line from the project.
    * 
    * @param line - the line to remove.
    */
   public void remove(Line line);

   /**
    * Remove a room from the project.
    * 
    * @param room - the room to remove.
    */
   public void remove(Room room);

   /**
    * The parameters of a device have been changed. Update the
    * device parameters and device objects.
    * 
    * @param device - the device.
    */
   public void parametersChanged(Device device);
}
