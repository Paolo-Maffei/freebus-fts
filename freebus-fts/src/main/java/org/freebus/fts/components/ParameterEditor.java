package org.freebus.fts.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.freebus.fts.components.parametereditor.Page;
import org.freebus.fts.components.parametereditor.ParamData;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;

/**
 * A panel that allows to edit the parameters of a program.
 */
public class ParameterEditor extends JPanel implements ChangeListener
{
   private static final long serialVersionUID = -2143429346277511397L;

   private JTabbedPane paramTabs = new JTabbedPane(JTabbedPane.LEFT);

   private Device device;
   private final Map<ParamData, Page> paramPages = new HashMap<ParamData, Page>();
   private final Map<Parameter, ParamData> paramData = new LinkedHashMap<Parameter, ParamData>();
   private boolean updateContentsEnabled = false;

   /**
    * Create a parameter editor.
    */
   public ParameterEditor()
   {
      super(new BorderLayout());

      add(paramTabs, BorderLayout.CENTER);
      paramTabs.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            final Component comp = paramTabs.getSelectedComponent();
            if (!(comp instanceof Page))
               return;

            if (updateContentsEnabled)
               ((Page) comp).updateContents();
         }
      });
   }

   /**
    * Set the device whose parameters are edited. Calls
    * {@link #updateContents()}.
    */
   public void setDevice(Device device)
   {
      this.device = device;
      updateContents();
   }

   /**
    * @return the device whose parameters are edited, or <code>null</code> if
    *         none.
    */
   public Device getDevice()
   {
      return device;
   }

   /**
    * Update the contents of the editor.
    */
   public void updateContents()
   {
      paramPages.clear();
      paramData.clear();
      paramTabs.removeAll();

      // Get the parameters and sort them by parameter-number
      final Program program = device.getProgram();
      final Set<Parameter> paramsSet = program.getParameters();
      final Parameter[] params = new Parameter[paramsSet.size()];
      paramsSet.toArray(params);
      Arrays.sort(params, new Comparator<Parameter>()
      {
         @Override
         public int compare(Parameter a, Parameter b)
         {
            return a.getNumber() - b.getNumber();
         }
      });

      // Create a ParamData object for every parameter
      for (final Parameter param : params)
         paramData.put(param, new ParamData(param));

      // Set the dependent parameters in the parameter-data objects
      for (final ParamData data : paramData.values())
      {
         final Parameter param = data.getParameter();
         final Parameter parentParam = param.getParent();
         if (parentParam == null)
            continue;

         if (!paramData.containsKey(parentParam))
         {
            Logger.getLogger(getClass()).error(
                  String.format("Parameter #{0} has an unknown parent parameter #{1}", new Object[] { param.getId(),
                        parentParam.getId() }));
            continue;
         }

         paramData.get(parentParam).addDependent(paramData.get(param));
      }

      // Create the parameter pages
      for (final ParamData data : paramData.values())
      {
         if (data.isPage())
         {
            final Page page = new Page(data, paramData);
            page.addChangeListener(this);
   
            paramPages.put(data, page);
         }
      }

      // Add the non-page parameters to the corresponding pages
      Page page = null;
      for (final Parameter param: params)
      {
         final ParamData data = paramData.get(param);
         if (data.isPage())
         {
            page = paramPages.get(data.getParameter());
         }
         else if (page != null)
         {
            page.addParamData(data);
         }
      }

      updateVisibility();

      ((Page) paramTabs.getComponentAt(0)).updateContents();
      paramTabs.setSelectedIndex(0);
   }

   /**
    * Update the visibility of the parameter pages.
    */
   public void updateVisibility()
   {
      updateContentsEnabled = false;

      // Collect the parameter-pages that shall be visible
      final Set<Page> visiblePages = new HashSet<Page>();
      for (final ParamData data : paramPages.keySet())
      {
         if (data.isVisible())
            visiblePages.add(paramPages.get(data));
      }

      // Remove all parameter-pages that are currently visible but that shall
      // not be visible anymore. The pages that are visible and shall stay visible
      // are removed from visiblePages. Afterwards, visiblePages only contains
      // those pages that need to be shown.
      for (int i = paramTabs.getComponentCount() - 1; i >= 0; --i)
      {
         final Page page = (Page) paramTabs.getComponentAt(i);

         if (visiblePages.contains(page))
            visiblePages.remove(page);
         else paramTabs.remove(i);
      }

      // Add pages that shall be visible but are not yet.
      for (final Page page: visiblePages)
      {
         final int displayOrder = page.getDisplayOrder();

         int i;
         for (i = paramTabs.getComponentCount() - 1; i >= 0; --i)
         {
            final Page pg = (Page) paramTabs.getComponentAt(i);
            if (pg.getDisplayOrder() < displayOrder)
               break;
         }

         paramTabs.add(page, i + 1);
      }

      updateContentsEnabled = true;
   }

   /**
    * Called when a parameter value was changed.
    */
   @Override
   public void stateChanged(ChangeEvent e)
   {
      updateVisibility();
   }
}
