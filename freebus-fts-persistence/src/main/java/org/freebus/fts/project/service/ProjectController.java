package org.freebus.fts.project.service;

import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;
import org.freebus.fts.project.RoomType;
import org.freebus.fts.project.SubGroup;

/**
 * Interface for project controllers. Project controllers manipulate the
 * project, and shall be used for the more complex tasks and for comfort
 * functions. The {@link Project project} shall contain the low level
 * functionality.
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
   @Deprecated
   void add(Building building);

   /**
    * Add a room to a building.
    * 
    * @param building - the building to add the room to.
    * @param room - the room to add.
    */
   @Deprecated
   void add(Building building, Room room);

   /**
    * Create an area and add it to the project.
    * 
    * @return The created area.
    */
   Area createArea();

   /**
    * Create a line and add it to the given area.
    * 
    * @param area - the area to which the line will be added
    * 
    * @return The created line.
    */
   Line createLine(Area area);

   /**
    * Create a building and add it to the project.
    * 
    * @return The created building.
    */
   Building createBuilding();

   /**
    * Create a building and add it to the given parent building.
    * 
    * @param parent - the parent building
    * 
    * @return The created building.
    */
   Building createBuilding(Building parent);

   /**
    * Create a room and add it to the given building.
    * 
    * @param building - the building that will contain the room.
    * @param type - the {@link RoomType type} of the room.
    * 
    * @return The created room.
    */
   Room createRoom(Building building, RoomType type);

   /**
    * Create a {@link MainGroup main group} and add it to the project.
    * 
    * @return The created main group.
    */
   MainGroup createMainGroup();

   /**
    * Create a {@link MidGroup mid-group} and add it to the main group.
    * 
    * @param parent - the parent main group to add to.
    * 
    * @return The created mid-group.
    */
   MidGroup createMidGroup(MainGroup parent);

   /**
    * Create a {@link SubGroup sub-group} and add it to the mid group.
    * 
    * @param midGroup - the parent mid-group to add to.
    * 
    * @return The created sub-group.
    */
   SubGroup createSubGroup(MidGroup parent);

   /**
    * Find or create all groups down to a {@link SubGroup sub-group} such that
    * the given {@link GroupAddress group address} exists in the project
    * afterwards. Does nothing if the group address already exists.
    * 
    * @param addr - the group address to process.
    * 
    * @return The sub group for the given address.
    */
   SubGroup findOrCreateSubGroup(GroupAddress addr);

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
