package org.freebus.fts.client.editors.devicedetails;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.freebus.fts.client.core.I18n;
import org.freebus.fts.products.CommunicationObject;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.project.Device;
import org.freebus.fts.service.devicecontroller.DeviceController;

/**
 * Displays details of the edited {@link Device}, mainly for debugging. Part of
 * the {@link DeviceDetails}.
 */
public class DebugPanel extends JPanel implements DeviceEditorComponent
{
   private static final long serialVersionUID = -5987571412817658216L;

   private final JTree tree;
   private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
   private final DeviceDebugTreeCellRenderer treeCellRenderer = new DeviceDebugTreeCellRenderer();
   private final JScrollPane treeView;

   private Device device;
   private boolean dirty = false;

   /**
    * Create a debug panel.
    */
   public DebugPanel()
   {
      setLayout(new BorderLayout());

      final JLabel caption = new JLabel(I18n.getMessage("DeviceEditor.DebugPanel.Caption"));
      caption.setFont(caption.getFont().deriveFont(Font.BOLD));
      caption.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
      add(caption, BorderLayout.NORTH);

      tree = new JTree(rootNode);
      tree.setRootVisible(false);
      tree.setCellRenderer(treeCellRenderer);

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
   public void setDevice(Device device, DeviceController adapter)
   {
      this.device = device;
      treeCellRenderer.setDevice(device);

      createContents();
   }

   /**
    * Create the tree of parameters and com-objects.
    */
   public void createContents()
   {
      rootNode.removeAllChildren();

      if (device == null || device.getProgram() == null)
         return;

      final Map<Parameter, DefaultMutableTreeNode> paramNodes = new HashMap<Parameter, DefaultMutableTreeNode>();
      final Parameter[] params = sortParametersByDisplayOrder(device.getProgram().getParameters());

      boolean foundWork = true;
      for (int tries = 20; tries > 0 && foundWork; --tries)
      {
         foundWork = false;

         for (final Parameter param : params)
         {
            if (paramNodes.containsKey(param))
               continue;

            final Parameter parentParam = param.getParent();
            DefaultMutableTreeNode parentNode;

            if (parentParam == null)
            {
               parentNode = rootNode;
            }
            else
            {
               parentNode = paramNodes.get(parentParam);
               if (parentNode == null)
                  continue;
            }

            foundWork = true;

            final DefaultMutableTreeNode paramNode = new DefaultMutableTreeNode(param, true);
            parentNode.add(paramNode);
            paramNodes.put(param, paramNode);
         }
      }

      for (final CommunicationObject comObj : sortCommunicationObjectsByDisplayOrder(device.getProgram()
            .getCommunicationObjects()))
      {
         final Parameter parentParam = comObj.getParameter();

         DefaultMutableTreeNode parentNode = parentParam == null ? null : paramNodes.get(parentParam);
         if (parentNode == null)
            parentNode = rootNode;

         final DefaultMutableTreeNode paramNode = new DefaultMutableTreeNode(comObj, true);
         parentNode.add(paramNode);
      }

      ((DefaultTreeModel) tree.getModel()).reload();

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

      dirty = false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void componentChanged(Object obj)
   {
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
