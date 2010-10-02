package org.freebus.fts.core;

import org.freebus.fts.MainWindow;
import org.freebus.fts.dialogs.AddDeviceDialog;
import org.freebus.fts.pages.AreaDetails;
import org.freebus.fts.pages.BuildingDetails;
import org.freebus.fts.pages.DeviceDetails;
import org.freebus.fts.pages.LineDetails;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
import org.freebus.fts.project.Room;
import org.freebus.fts.project.service.ProjectController;

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
