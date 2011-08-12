package org.freebus.fts.service.project;

import java.util.Set;

import org.freebus.fts.common.address.GroupAddress;
import org.freebus.fts.common.exception.FtsRuntimeException;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.ProjectUtils;
import org.freebus.fts.project.Room;
import org.freebus.fts.project.RoomType;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.internal.I18n;
import org.freebus.fts.project.service.ProjectController;
import org.freebus.fts.service.devicecontroller.DeviceController;
import org.freebus.fts.service.devicecontroller.DeviceControllerFactory;
import org.freebus.fts.service.exception.DeviceControllerException;
import org.freebus.fts.service.job.JobQueue;
import org.freebus.fts.service.job.device.DeviceProgrammerJob;

/**
 * Project controller implementation.
 *
 * All non-GUI stuff of a project controller is in this class.
 * All GUI handling stuff is in the InteractiveProjectController of the FTS client.
 */
public class BasicProjectController implements ProjectController
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void add(VirtualDevice virtualDevice)
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void add(Building building)
   {
      ProjectManager.getProject().add(building);
      ProjectManager.fireComponentAdded(building);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void add(Building building, Room room)
   {
      building.add(room);
      ProjectManager.fireComponentAdded(room);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Area createArea()
   {
      final Project project = ProjectManager.getProject();

      final int address = ProjectUtils.getFreeAddress(project.getAreas(), 0, 15);
      if (address < 0)
         throw new FtsRuntimeException(I18n.getMessage("ProjectControllerImpl.ErrMaxAreas"));

      final Area area = new Area();
      area.setAddress(address);
      area.setName(I18n.formatMessage("BasicProjectController.NewAreaName", Integer.toString(address)));
      project.add(area);

      ProjectManager.fireComponentAdded(area);
      edit(area);
      
      return area;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Line createLine(Area area)
   {
      final int address = ProjectUtils.getFreeAddress(area.getLines(), 0, 15);
      if (address < 0)
         throw new FtsRuntimeException(I18n.getMessage("ProjectControllerImpl.ErrMaxLines"));

      final Line line = new Line();
      line.setAddress(address);
      line.setName(I18n.formatMessage("ProjectControllerImpl.NewLineName", Integer.toString(address)));
      area.add(line);

      ProjectManager.fireComponentAdded(line);
      edit(line);
      
      return line;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Building createBuilding()
   {
      return createBuilding(null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Building createBuilding(Building parent)
   {
      final Project project = ProjectManager.getProject();
      final Set<String> names = ProjectUtils.getNames(project.getBuildings());

      String name = "";
      for (int i = 1; i < 1000; ++i)
      {
         name = I18n.formatMessage(parent == null ? "NewBuilding" : "NewBuildingPart", Integer.toString(i));
         if (!names.contains(name))
            break;
      }

      final Building building = new Building();
      building.setName(name);
      project.add(building);

      ProjectManager.fireComponentAdded(building);
      edit(building);

      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Room createRoom(Building building, RoomType type)
   {
      final Set<Room> rooms = building.getRooms();

      final Room room = new Room();
      room.setType(type);

      for (int i = 1; i < 1000; ++i)
      {
         room.setName(I18n.formatMessage(type == RoomType.ROOM ? "NewRoom" : "NewCabinet", Integer.toString(i)));
         if (!rooms.contains(room))
            break;
      }

      building.add(room);
      ProjectManager.fireComponentAdded(room);
      edit(room);
      
      return room;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MainGroup createMainGroup()
   {
      final Project project = ProjectManager.getProject();

      final int address = ProjectUtils.getFreeAddress(project.getMainGroups(), 0, 15);
      if (address < 0)
         throw new FtsRuntimeException(I18n.getMessage("ProjectControllerImpl.ErrMaxGroups"));

      return createMainGroup(address);
   }

   /**
    * Create a main-group with a specific group address. It is not ensured
    * that the address is unique.
    * 
    * @param address - the group address.
    * 
    * @return The created main-group.
    */
   public MainGroup createMainGroup(int address)
   {
      final Project project = ProjectManager.getProject();

      final MainGroup grp = new MainGroup();
      grp.setAddress(address);
      grp.setName(I18n.formatMessage("ProjectControllerImpl.NewMainGroupName", Integer.toString(address)));
      project.add(grp);

      ProjectManager.fireComponentAdded(grp);
      edit(grp);
      
      return grp;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MidGroup createMidGroup(MainGroup parent)
   {
      final int address = ProjectUtils.getFreeAddress(parent.getMidGroups(), 0, 7);
      if (address < 0)
         throw new FtsRuntimeException(I18n.getMessage("ProjectControllerImpl.ErrMaxGroups"));

      return createMidGroup(parent, address);
   }

   /**
    * Create a mid-group with a specific group address. It is not ensured
    * that the address is unique.
    * 
    * @param parent - the parent main-group.
    * @param address - the mid-group address.
    * 
    * @return The created mid-group.
    */
   public MidGroup createMidGroup(MainGroup parent, int address)
   {
      final MidGroup grp = new MidGroup();
      grp.setAddress(address);
      grp.setName(I18n.formatMessage("ProjectControllerImpl.NewMidGroupName", Integer.toString(address)));
      parent.add(grp);

      ProjectManager.fireComponentAdded(grp);
      edit(grp);
      
      return grp;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public SubGroup createSubGroup(MidGroup parent)
   {
      final int address = ProjectUtils.getFreeAddress(parent.getSubGroups(), 0, 7);
      if (address < 0)
         throw new FtsRuntimeException(I18n.getMessage("ProjectControllerImpl.ErrMaxGroups"));
      
      return createSubGroup(parent, address);
   }

   /**
    * Create a sub-group with a specific group address. It is not ensured
    * that the address is unique.
    * 
    * @param parent - the parent mid-group.
    * @param address - the group address.
    * 
    * @return The created mid-group.
    */
   public SubGroup createSubGroup(MidGroup parent, int address)
   {
      final SubGroup grp = new SubGroup();
      grp.setAddress(address);
      grp.setName(I18n.formatMessage("ProjectControllerImpl.NewSubGroupName", Integer.toString(address)));
      parent.add(grp);

      ProjectManager.fireComponentAdded(grp);
      edit(grp);
      
      return grp;
   }

   /**
    * {@inheritDoc}
    */
   public SubGroup findOrCreateSubGroup(GroupAddress addr)
   {
      final Project project = ProjectManager.getProject();

      final int mainAddr = addr.getMain();
      MainGroup mainGroup = project.findMainGroupByAddr(mainAddr);
      if (mainGroup == null)
      {
         mainGroup = createMainGroup(mainAddr);
      }

      final int midAddr = addr.getMiddle();
      MidGroup midGroup = mainGroup.findMidGroupByAddr(midAddr);
      if (midGroup == null)
      {
         midGroup = createMidGroup(mainGroup, midAddr);
      }

      final int subAddr = addr.getSub();
      SubGroup subGroup = midGroup.findSubGroupByAddr(subAddr);
      if (subGroup == null)
      {
         subGroup = createSubGroup(midGroup, subAddr);
      }

      return subGroup;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean edit(Object obj)
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean remove(Object obj)
   {
      if (obj instanceof Area)
         remove((Area) obj);
      else if (obj instanceof Building)
         remove((Building) obj);
      else if (obj instanceof Device)
         remove((Device) obj);
      else if (obj instanceof Line)
         remove((Line) obj);
      else if (obj instanceof Room)
         remove((Room) obj);
      else if (obj instanceof MainGroup)
         remove((MainGroup) obj);
      else if (obj instanceof MidGroup)
         remove((MidGroup) obj);
      else if (obj instanceof SubGroup)
         remove((SubGroup) obj);
      else return false;

      return true;
   }

   /**
    * Remove an area.
    */
   public void remove(Area area)
   {
      area.detach();

      for (final Object line : area.getLines().toArray())
         remove((Line) line);

      ProjectManager.fireComponentRemoved(area);
   }

   /**
    * Remove a building.
    */
   public void remove(Building building)
   {
      building.detach();

      for (final Object room : building.getRooms().toArray())
         remove((Room) room);

      ProjectManager.fireComponentRemoved(building);
   }

   /**
    * Remove a device.
    */
   public void remove(Device device)
   {
      device.detach();

      ProjectManager.fireComponentRemoved(device);
   }

   /**
    * Remove a line.
    */
   public void remove(Line line)
   {
      line.detach();

      for (final Object device : line.getDevices().toArray())
         remove((Device) device);

      ProjectManager.fireComponentRemoved(line);
   }

   /**
    * Remove a room.
    */
   public void remove(Room room)
   {
      room.detach();

      for (final Object device : room.getDevices().toArray())
         remove((Device) device);

      ProjectManager.fireComponentRemoved(room);
   }

   /**
    * Remove a main group.
    */
   public void remove(MainGroup mainGroup)
   {
      mainGroup.detach();

      for (final Object obj : mainGroup.getMidGroups().toArray())
         remove((MidGroup) obj);

      ProjectManager.fireComponentRemoved(mainGroup);
   }

   /**
    * Remove a mid group.
    */
   public void remove(MidGroup midGroup)
   {
      midGroup.detach();

      for (final Object obj : midGroup.getSubGroups().toArray())
         remove((SubGroup) obj);

      ProjectManager.fireComponentRemoved(midGroup);
   }

   /**
    * Remove a sub group.
    */
   public void remove(SubGroup subGroup)
   {
      subGroup.detach();

      for (final Object obj : subGroup.getSubGroupToObjects().toArray())
         remove(obj);

      ProjectManager.fireComponentRemoved(subGroup);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void program(Object obj)
   {
      if (obj instanceof Device)
         program((Device) obj);
   }

   /**
    * Program a device
    */
   public void program(Device device)
   {
      try
      {
         final DeviceController controller = DeviceControllerFactory.getDeviceController(device);
         for (DeviceProgrammerJob job: controller.getRequiredProgrammerJobs())
            JobQueue.getDefaultJobQueue().add(job);
      }
      catch (DeviceControllerException e)
      {
         showException(e);
      }
   }

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
   public void parametersChanged(Device device)
   {
      device.updateDeviceParameters();
      device.updateDeviceObjects();
   }

   /**
    * Show an exception.
    * 
    * @param e - the exception to show.
    */
   protected void showException(Exception e)
   {
      e.printStackTrace();
   }
}
