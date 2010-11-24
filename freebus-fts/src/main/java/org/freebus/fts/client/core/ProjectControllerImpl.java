package org.freebus.fts.client.core;

import org.freebus.fts.client.MainWindow;
import org.freebus.fts.dialogs.AddDeviceDialog;
import org.freebus.fts.elements.components.Dialogs;
import org.freebus.fts.pages.AreaDetails;
import org.freebus.fts.pages.BuildingDetails;
import org.freebus.fts.pages.LineDetails;
import org.freebus.fts.pages.devicedetails.DeviceDetails;
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
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.project.service.ProjectController;
import org.freebus.fts.service.devicecontroller.DeviceController;
import org.freebus.fts.service.devicecontroller.DeviceControllerFactory;
import org.freebus.fts.service.exception.DeviceControllerException;
import org.freebus.fts.service.job.JobQueue;
import org.freebus.fts.service.job.device.DeviceProgrammerJob;

/**
 * Implementation of the project controller.
 */
public final class ProjectControllerImpl implements ProjectController
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void add(VirtualDevice virtualDevice)
   {
      final MainWindow mainWin = MainWindow.getInstance();
      final AddDeviceDialog dlg = new AddDeviceDialog(virtualDevice, mainWin);
      dlg.setVisible(true);
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
   public boolean edit(Object obj)
   {
      final MainWindow mainWin = MainWindow.getInstance();

      if (obj instanceof Area)
         mainWin.showPage(AreaDetails.class, obj);
      else if (obj instanceof Line)
         mainWin.showPage(LineDetails.class, obj);
      else if (obj instanceof Device)
         mainWin.showPage(DeviceDetails.class, obj);
      else if (obj instanceof Building)
         mainWin.showPage(BuildingDetails.class, obj);
      else return false;

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
         Dialogs.showExceptionDialog(e, null);
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
}
