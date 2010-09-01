package org.freebus.fts.pages.deviceeditor;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.freebus.fts.pages.DeviceEditor;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.project.Device;

/**
 * An editor for the communication objects of a device.
 * Part of the {@link DeviceEditor}.
 */
public class CommObjectsPanel extends JPanel implements DeviceEditorPart
{
   private static final long serialVersionUID = -6987571415817658896L;

   private final JPanel contents = new JPanel();

   private Device device;
   private boolean dirty = false;

   /**
    * Create a communication-objects editor.
    */
   public CommObjectsPanel()
   {
      setLayout(new BorderLayout());

      add(contents, BorderLayout.CENTER);
      contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));
      contents.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

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

      if (isVisible())
         updateContents();
      else dirty = true;
   }

   /**
    * Update the contents of the panel
    */
   public void updateContents()
   {
      dirty = false;
      contents.removeAll();

      if (device == null)
         return;

      for (final CommunicationObject comObject: device.getVisibleCommunicationObjects())
      {
         contents.add(new JLabel(comObject.getName()));
      }
   }
}
