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
   public void edit(Object obj)
   {
      if (obj instanceof Area)
         edit((Area) obj);
      else if (obj instanceof Line)
         edit((Line) obj);
      else if (obj instanceof Device)
         edit((Device) obj);
      else if (obj instanceof Building)
         edit((Building) obj);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void edit(Area area)
   {
      MainWindow.getInstance().showPage(AreaDetails.class, area);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void edit(Line line)
   {
      MainWindow.getInstance().showPage(LineDetails.class, line);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void edit(Device device)
   {
      MainWindow.getInstance().showPage(DeviceDetails.class, device);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void edit(Building building)
   {
      MainWindow.getInstance().showPage(BuildingDetails.class, building);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Object obj)
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
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Area area)
   {
      area.detach();

      for (final Object line : area.getLines().toArray())
         remove((Line) line);

      ProjectManager.fireComponentRemoved(area);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Building building)
   {
      building.detach();

      for (final Object room : building.getRooms().toArray())
         remove((Room) room);

      ProjectManager.fireComponentRemoved(building);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Device device)
   {
      device.detach();

      ProjectManager.fireComponentRemoved(device);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Line line)
   {
      line.detach();

      for (final Object device : line.getDevices().toArray())
         remove((Device) device);

      ProjectManager.fireComponentRemoved(line);
   }

   /**
    * {@inheritDoc}
    */
   @Override
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
   public void parametersChanged(Device device)
   {
      device.updateDeviceParameters();
      device.updateDeviceObjects();
   }
}
