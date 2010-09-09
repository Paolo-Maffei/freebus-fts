package org.freebus.fts.pages.deviceeditor;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.freebus.fts.pages.DeviceEditor;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.project.Device;

/**
 * An editor for the communication objects of a device.
 * Part of the {@link DeviceEditor}.
 */
public class ComObjectsPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -6987571415817658896L;

   private final JPanel contents = new JPanel();

   private Device device;
   private boolean dirty = false;

   /**
    * Create a communication-objects editor.
    */
   public ComObjectsPanel()
   {
      setLayout(new BorderLayout());

      add(contents, BorderLayout.NORTH);
      contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

      add(new JPanel(), BorderLayout.CENTER);

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

//      final DeviceParameters devParams = device.getDeviceParameters();
      for (final CommunicationObject comObject: device.getVisibleCommunicationObjects())
      {
         contents.add(new ComObjectPanel(comObject));

//         final JPanel base = new JPanel();
//         base.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
//         contents.add(base);
//         final Parameter param = comObject.getParameter();
//         base.add(newLabel(comObject.getName() + " - " + comObject.getFunction() + " #" + comObject.getId()));
//         base.add(newLabel("DispOrder " + comObject.getDisplayOrder()));
//         base.add(newLabel("Param #" + (param == null ? -1 : param.getId())));
//         base.add(newLabel("ExpectedValue=" + comObject.getParameterValue()));
//         base.add(newLabel("ParamValue=" + (param == null ? "" : devParams.getIntValue(param))));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void componentChanged(Object obj)
   {
   }
}
