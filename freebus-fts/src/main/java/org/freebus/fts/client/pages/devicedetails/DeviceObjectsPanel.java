package org.freebus.fts.client.pages.devicedetails;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;
import org.freebus.fts.project.MainGroup;
import org.freebus.fts.project.MidGroup;
import org.freebus.fts.project.SubGroup;
import org.freebus.fts.service.devicecontroller.DeviceController;

/**
 * An editor for the communication objects of a device.
 * Part of the {@link DeviceDetails}.
 */
public class DeviceObjectsPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -6987571415817658896L;

   private final JPanel contents = new JPanel();
   private final JScrollPane contentsView = new JScrollPane(contents);

   private Device device;
   private boolean dirty = false;

   /**
    * Create a communication-objects editor.
    */
   public DeviceObjectsPanel()
   {
      setLayout(new BorderLayout());

      contentsView.getVerticalScrollBar().setUnitIncrement(25);
      contentsView.getVerticalScrollBar().setBlockIncrement(50);
      add(contentsView, BorderLayout.CENTER);

      contents.setLayout(new GridBagLayout());
      contents.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

      addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentShown(ComponentEvent e)
         {
            if (dirty)
               updateContents();
         }
      });
   }

   /**
    * Set the edited device.
    */
   @Override
   public void setDevice(Device device, DeviceController adapter)
   {
      this.device = device;
      updateContents();
   }

   /**
    * Update the contents of the panel
    */
   public void updateContents()
   {
      if (!isVisible())
      {
         dirty = true;
         return;
      }

      Logger.getLogger(getClass()).info("updateContents");
      dirty = false;
      contents.removeAll();

      if (device == null)
         return;

      final GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

      final Collection<DeviceObject> deviceObjects = device.getDeviceObjects();
      final DeviceObject[] sortedDeviceObjects = new DeviceObject[deviceObjects.size()];
      deviceObjects.toArray(sortedDeviceObjects);
      Arrays.sort(sortedDeviceObjects, new Comparator<DeviceObject>()
            {
         @Override
         public int compare(DeviceObject a, DeviceObject b)
         {
            final CommunicationObject ca = a.getComObject();
            final CommunicationObject cb = b.getComObject();

            final int keyA = ca == null ? 0 : (ca.getDisplayOrder() << 10) | ca.getNumber();
            final int keyB = cb == null ? 0 : (cb.getDisplayOrder() << 10) | cb.getNumber();

            return keyA - keyB;
         }
            });

      int gridy = -1;
      for (final DeviceObject deviceObject: sortedDeviceObjects)
      {
         final DeviceObjectPanel pnl = new DeviceObjectPanel(deviceObject);
         pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
         c.gridy = ++gridy;
         contents.add(pnl, c);
      }

      c.gridy = ++gridy;
      c.weighty = 100;
      contents.add(Box.createVerticalGlue(), c);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void componentChanged(Object obj)
   {
      if (obj instanceof MainGroup || obj instanceof MidGroup || obj instanceof SubGroup)
         updateContents();
   }
}
