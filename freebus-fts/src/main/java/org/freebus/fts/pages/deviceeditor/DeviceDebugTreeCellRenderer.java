package org.freebus.fts.pages.deviceeditor;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.freebus.fts.core.ImageCache;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceParameter;

/**
 * A {@link DefaultTreeCellRenderer tree cell renderer} that renders a tree that
 * consists of {@link Parameter parameters} and {@link CommunicationObject
 * communication objects}. The parameters and communication objects are compared
 * with the device objects and device parameters of a device, that has to be set
 * with {@link #setDevice(Device)}.
 */
public class DeviceDebugTreeCellRenderer extends DefaultTreeCellRenderer
{
   private static final long serialVersionUID = -6988751970917478985L;

   private final Icon comObjectIcon = ImageCache.getIcon("icons/com-object");
   private final Icon parameterIcon = ImageCache.getIcon("icons/completion");
   private final Icon parameterLabelIcon = ImageCache.getIcon("icons/charset");
   private final Icon parameterPageIcon = ImageCache.getIcon("icons/empty-page");

   private Device device;

   /**
    * {@inheritDoc}
    */
   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
         int row, boolean hasFocus)
   {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if (value instanceof DefaultMutableTreeNode)
      {
         final Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

         if (userObject instanceof CommunicationObject)
         {
            setIcon(comObjectIcon);
            setTextFor((CommunicationObject) userObject);
         }
         else if (userObject instanceof Parameter)
         {
            final Parameter param = (Parameter) userObject;

            if (param.isPage())
               setIcon(parameterPageIcon);
            else if (param.isLabel())
               setIcon(parameterLabelIcon);
            else setIcon(parameterIcon);

            setTextFor((Parameter) userObject);
         }
         else setText(userObject.toString());
      }

      return this;
   }

   /**
    * Set the device that is used for rendering.
    * 
    * @param device - the device to set.
    */
   public void setDevice(Device device)
   {
      this.device = device;
   }

   /**
    * Set the text for the given communication object.
    * 
    * @param comObject - the communication object to use for the text.
    */
   private void setTextFor(CommunicationObject comObject)
   {
      final Parameter parentParam = comObject.getParameter();
      final Integer expectedParamValue = comObject.getParameterValue();
      boolean visible = device.isVisible(comObject);

      final StringBuilder sb = new StringBuilder();
      if (!visible)
         sb.append("<");
      sb.append("COM-").append(comObject.getNumber()).append(" #").append(comObject.getUniqueNumber()).append(" ")
            .append(comObject.getName());
      if (!visible)
         sb.append(">");

      sb.append(" ... ").append(comObject.getObjectType().getName());

      sb.append("  ... [visible: ");
      if (parentParam == null)
         sb.append("always");
      else if (parentParam.getHighAccess() == 0)
         sb.append("never");
      else if (expectedParamValue == null)
         sb.append("if #").append(parentParam.getNumber()).append(" is visible");
      else sb.append("if #").append(parentParam.getNumber()).append(".value == ").append(expectedParamValue);
      sb.append("]");

      setText(sb.toString());
   }

   /**
    * Set the text for the given parameter.
    * 
    * @param param - the parameter to use for the text.
    */
   private void setTextFor(Parameter param)
   {
      final DeviceParameter devParam = device.getDeviceParameter(param);
      final Parameter parentParam = param.getParent();
      boolean visible = devParam.isVisible();

      final StringBuilder sb = new StringBuilder();
      if (!visible)
         sb.append("<");
      sb.append("#").append(param.getNumber()).append(" ").append(param.getDescription().replaceAll("\\\\r\\\\n", " "));
      if (!visible)
         sb.append(">");
      sb.append(" ...");
      if (param.getAtomicType() != ParameterAtomicType.NONE)
         sb.append(" value ").append(devParam.getIntValue());

      sb.append(" ...  [visible: ");

      if (parentParam == null)
      {
         if (param.getHighAccess() == 0)
            sb.append("never");
         else sb.append("always");
      }
      else
      {
         final Integer expectedParentValue = param.getParentValue();
         if (expectedParentValue == null)
            sb.append("if #").append(parentParam.getNumber()).append(" is visible");
         else sb.append("if #").append(parentParam.getNumber()).append(".value == ").append(expectedParentValue);
      }
      sb.append("]");

      setText(sb.toString());
   }
}
