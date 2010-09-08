package org.freebus.fts;

import org.freebus.fts.dialogs.AddDeviceDialog;
import org.freebus.fts.pages.DeviceEditor;
import org.freebus.fts.pages.TopologyView;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Project;
import org.freebus.fts.project.ProjectManager;
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
      if (!dlg.isAccepted())
         return;
      
      final Device device = new Device(virtualDevice);

      final TopologyView topologyView = (TopologyView) mainWin.getPage(TopologyView.class, null);
      if (topologyView == null)
         return;

      topologyView.addDevice(device);
      ProjectManager.fireComponentAdded(device);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void edit(Object obj)
   {
      if (obj instanceof Device)
         edit((Device) obj);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void edit(Device device)
   {
      MainWindow.getInstance().showPage(DeviceEditor.class, device);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Object obj)
   {
      if (obj instanceof Area)
         remove((Area) obj);
      else if (obj instanceof Line)
         remove((Line) obj);
      else if (obj instanceof Device)
         remove((Device) obj);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Area area)
   {
      final Project project = area.getProject();
      if (project != null)
         project.remove(area);

      for (final Object line : area.getLines().toArray())
         remove((Line) line);

      ProjectManager.fireComponentRemoved(area);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Line line)
   {
      final Area area = line.getArea();
      if (area != null)
         area.remove(line);

      for (final Object device : line.getDevices().toArray())
         remove((Device) device);

      ProjectManager.fireComponentRemoved(line);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void remove(Device device)
   {
      final Line line = device.getLine();
      if (line != null)
         line.remove(device);

      ProjectManager.fireComponentRemoved(device);
   }
}
