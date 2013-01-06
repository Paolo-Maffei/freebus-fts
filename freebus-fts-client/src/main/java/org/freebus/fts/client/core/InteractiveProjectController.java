package org.freebus.fts.client.core;

import org.freebus.fts.client.application.MainWindow;
import org.freebus.fts.client.dialogs.AddDeviceDialog;
import org.freebus.fts.client.editors.AreaDetails;
import org.freebus.fts.client.editors.BuildingDetails;
import org.freebus.fts.client.editors.LineDetails;
import org.freebus.fts.client.editors.RoomDetails;
import org.freebus.fts.client.editors.MainGroupDetails;
import org.freebus.fts.client.editors.MidGroupDetails;
import org.freebus.fts.client.editors.SubGroupDetails;
import org.freebus.fts.client.editors.devicedetails.DeviceDetails;
import org.freebus.fts.client.workbench.WorkBench;
import org.freebus.fts.products.VirtualDevice;
import org.freebus.fts.project.Area;
import org.freebus.fts.project.Building;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.Line;
import org.freebus.fts.project.Room;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.service.project.BasicProjectController;

/**
 * Project controller that contains the connections to the FTS client.
 * All GUI handling stuff of a project controller is in this class.
 * All non-GUI stuff is in the {@link BasicProjectController} base class.
 */
public final class InteractiveProjectController extends BasicProjectController
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
      final WorkBench workBench = WorkBench.getInstance();

      if (obj instanceof Area)
      {
         workBench.showEditor(AreaDetails.class, obj);
      }
      else if (obj instanceof Line)
      {
         workBench.showEditor(LineDetails.class, obj);
      }
      else if (obj instanceof Device)
      {
         workBench.showEditor(DeviceDetails.class, obj);
      }
      else if (obj instanceof Building)
      {
         workBench.showEditor(BuildingDetails.class, obj);
      }
      else if (obj instanceof Room)
      {
         workBench.showEditor(RoomDetails.class, obj);
      }
      else if (obj instanceof MainGroup)
      {
         workBench.showEditor(MainGroupDetails.class, obj);
      }
      else if (obj instanceof MidGroup)
      {
         workBench.showEditor(MidGroupDetails.class, obj);
      }
      else if (obj instanceof SubGroup)
      {
         workBench.showEditor(SubGroupDetails.class, obj);
      }
      else return false;

      return true;
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
