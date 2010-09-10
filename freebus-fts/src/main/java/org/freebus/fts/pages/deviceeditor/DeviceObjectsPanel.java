package org.freebus.fts.pages.deviceeditor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.freebus.fts.pages.DeviceEditor;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceObject;

/**
 * An editor for the communication objects of a device.
 * Part of the {@link DeviceEditor}.
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
   public void setDevice(Device device)
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
      
      int gridy = -1;
      for (final DeviceObject deviceObject: device.getVisibleDeviceObjects())
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
   }
}
