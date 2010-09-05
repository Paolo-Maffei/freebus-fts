package org.freebus.fts.pages.deviceeditor;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.freebus.fts.pages.DeviceEditor;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceParameter;
import org.freebus.fts.project.DeviceParameters;

/**
 * Displays details of the edited {@link Device}, mainly for debugging. Part of
 * the {@link DeviceEditor}.
 */
public class DebugPanel extends JPanel implements DeviceEditorPart
{
   private static final long serialVersionUID = -5987571412817658216L;

   private final JTree tree;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
   private final JScrollPane treeView;

   private Device device;
   private boolean dirty = false;

   /**
    * Create a debug panel.
    */
   public DebugPanel()
   {
      setLayout(new BorderLayout());

      tree = new JTree(rootNode);
      tree.setRootVisible(false);

      treeView = new JScrollPane(tree);
      add(treeView, BorderLayout.CENTER);

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
      rootNode.removeAllChildren();

      if (device == null)
         return;

      final Map<Parameter, DefaultMutableTreeNode> paramNodes = new HashMap<Parameter, DefaultMutableTreeNode>();
      final DeviceParameters devParams = device.getDeviceParameters();

      for (final Parameter param : sortParametersByDisplayOrder(device.getProgram().getParameters()))
      {
         final DeviceParameter devParam = device.getDeviceParameter(param);
         final Parameter parentParam = param.getParent();
         boolean visible = devParam.isVisible();

         final StringBuilder sb = new StringBuilder();
         if (!visible)
            sb.append("<");
         sb.append("#").append(param.getId()).append(" ").append(param.getDescription());
         if (!visible)
            sb.append(">");
         sb.append(":  value ").append(devParams.getIntValue(param));

         sb.append("  [visible: ");

         if (param.getHighAccess() == 0)
            sb.append("never");
         else if (parentParam == null)
            sb.append("always");
         else
         {
            final Integer expectedParentValue = param.getParentValue();
            if (expectedParentValue == null)
               sb.append("if #").append(parentParam.getId()).append(" is visible");
            else sb.append("if #").append(parentParam.getId()).append(".value == ")
                  .append(expectedParentValue);
         }
         sb.append("]");

         DefaultMutableTreeNode parentNode = parentParam == null ? null : paramNodes.get(parentParam);
         if (parentNode == null || param.isPage())
            parentNode = rootNode;

         final DefaultMutableTreeNode paramNode = new DefaultMutableTreeNode(sb.toString(), true);
         parentNode.add(paramNode);
         paramNodes.put(param, paramNode);
      }

      for (final CommunicationObject comObj : sortCommunicationObjectsByDisplayOrder(device.getProgram()
            .getCommunicationObjects()))
      {
         final Parameter parentParam = comObj.getParameter();
         final Integer expectedParamValue = comObj.getParameterValue();
         boolean visible = device.isVisible(comObj);

         final StringBuilder sb = new StringBuilder();
         if (!visible)
            sb.append("<");
         sb.append("COM #").append(comObj.getId()).append(" ").append(comObj.getName());
         if (!visible)
            sb.append(">");

         sb.append("  [visible: ");
         if (parentParam == null)
            sb.append("always");
         else if (parentParam.getLowAccess() == 0)
            sb.append("never");
         else if (expectedParamValue == null)
            sb.append("if #").append(parentParam.getId()).append(" is visible");
         else sb.append("if #").append(parentParam.getId()).append(".value == ")
               .append(expectedParamValue);
         sb.append("]");

         DefaultMutableTreeNode parentNode = parentParam == null ? null : paramNodes.get(parentParam);
         if (parentNode == null)
            parentNode = rootNode;

         final DefaultMutableTreeNode paramNode = new DefaultMutableTreeNode(sb.toString(), true);
         parentNode.add(paramNode);
      }

      ((DefaultTreeModel) tree.getModel()).reload();
   }

   /**
    * Sort the parameters by display order {@link Parameter#getDisplayOrder()}.
    * 
    * @param params - the parameters to sort.
    * 
    * @return a sorted array of parameters.
    */
   public static Parameter[] sortParametersByDisplayOrder(final Collection<Parameter> params)
   {
      final Parameter[] paramsSorted = new Parameter[params.size()];
      params.toArray(paramsSorted);
      Arrays.sort(paramsSorted, new Comparator<Parameter>()
      {
         @Override
         public int compare(Parameter a, Parameter b)
         {
            return a.getDisplayOrder() - b.getDisplayOrder();
         }
      });

      return paramsSorted;
   }

   /**
    * Sort the communication objects by display order
    * {@link Parameter#getDisplayOrder()}.
    * 
    * @param params - the communication objects to sort.
    * 
    * @return a sorted array of communication objects.
    */
   public static CommunicationObject[] sortCommunicationObjectsByDisplayOrder(final Collection<CommunicationObject> objs)
   {
      final CommunicationObject[] objsSorted = new CommunicationObject[objs.size()];
      objs.toArray(objsSorted);
      Arrays.sort(objsSorted, new Comparator<CommunicationObject>()
      {
         @Override
         public int compare(CommunicationObject a, CommunicationObject b)
         {
            return a.getDisplayOrder() - b.getDisplayOrder();
         }
      });

      return objsSorted;
   }
}
