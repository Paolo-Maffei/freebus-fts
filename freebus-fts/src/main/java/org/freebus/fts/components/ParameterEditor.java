package org.freebus.fts.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.freebus.fts.components.parametereditor.Page;
import org.freebus.fts.components.parametereditor.ParamData;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;
import org.freebus.fts.project.Device;

/**
 * A widget that allows to edit the parameters of a program.
 */
public class ParameterEditor extends JPanel implements ChangeListener
{
   private static final long serialVersionUID = -2143429346277511397L;

   private JTabbedPane paramTabs = new JTabbedPane(JTabbedPane.LEFT);
   private int tabWidth = 150;
   private int tabHeight;

   private Device device;
   private final List<Page> paramPages = new Vector<Page>();
   private final Map<Parameter, ParamData> paramDatas = new HashMap<Parameter, ParamData>();
   private boolean updateContentsEnabled = false;
   private boolean inStateChanged = false;

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
      updateContentsEnabled = false;

      paramPages.clear();
      paramDatas.clear();
      paramTabs.removeAll();

      // Create a parameter-data object for every parameter
      final Program program = device.getProgram();
      final Set<Parameter> paramsSet = program.getParameters();
      final ParamData[] paramDataArr = new ParamData[paramsSet.size()];
      int i = -1;
      for (final Parameter param : paramsSet)
      {
         final ParamData data = new ParamData(param);
         paramDataArr[++i] = data;
         paramDatas.put(param, data);
      }

      // Sort the parameter-data objects by display-order
      Arrays.sort(paramDataArr, new Comparator<ParamData>()
      {
         @Override
         public int compare(ParamData a, ParamData b)
         {
            return a.getDisplayOrder() - b.getDisplayOrder();
         }
      });

      // Set the dependent parameters of the parameter-data objects
      for (final ParamData data : paramDataArr)
      {
         final Parameter param = data.getParameter();
         final Parameter parentParam = param.getParent();
         if (parentParam == null)
            continue;

         if (!paramDatas.containsKey(parentParam))
         {
            Logger.getLogger(getClass()).error(
                  String.format("Parameter #{0} has an unknown parent parameter #{1}", new Object[] { param.getId(),
                        parentParam.getId() }));
            continue;
         }

         paramDatas.get(parentParam).addDependent(paramDatas.get(param));
      }

      // Create the parameter pages and add the parameter-data objects to their
      // pages
      Page page = null;
      for (final ParamData data : paramDataArr)
      {
         if (data.isPage() && (page == null || page.getDisplayOrder() != data.getDisplayOrder()))
         {
            page = new Page(data);
            page.addChangeListener(this);

            paramPages.add(page);
         }
         else if (page != null)
         {
            page.addParamData(data);
         }
         else
         {
            // Parameter does not belong to a page
            Logger.getLogger(getClass()).debug("Skipping orphaned parameter: " + data.getParameter());
         }
      }

      updateVisibility();

      if (paramTabs.getComponentCount() > 0)
      {
         updateContentsEnabled = false;
         ((Page) paramTabs.getComponentAt(0)).updateContents();
         paramTabs.setSelectedIndex(0);
         updateContentsEnabled = true;
      }
   }

   /**
    * Update the visibility of the parameter pages.
    */
   public void updateVisibility()
   {
//      Logger.getLogger(getClass()).debug("start updateVisibility");
      updateContentsEnabled = false;

      // Collect the pages that shall be visible
      final Set<Page> visiblePages = new HashSet<Page>();
      for (final Page page : paramPages)
      {
         if (page.getPageData().isVisible())
            visiblePages.add(page);
      }

      // Remove all pages that are currently visible but that shall
      // not be visible anymore. The pages that are visible and shall stay
      // visible are removed from visiblePages. Afterwards, visiblePages only
      // contains those pages that need to be shown.
      for (int i = paramTabs.getTabCount() - 1; i >= 0; --i)
      {
         final Page page = (Page) paramTabs.getComponentAt(i);

         if (visiblePages.contains(page))
         {
            paramTabs.setTitleAt(i, page.getName());
            paramTabs.setToolTipTextAt(i, "Debug: parameter-id is " + page.getPageParameter().getId());
            visiblePages.remove(page);
         }
         else paramTabs.remove(i);
      }

      // Add pages that shall be visible but are not yet.
      for (final Page page : visiblePages)
      {
         final int displayOrder = page.getDisplayOrder();

         int i;
         for (i = paramTabs.getTabCount() - 1; i >= 0; --i)
         {
            final Page pg = (Page) paramTabs.getComponentAt(i);
            if (pg.getDisplayOrder() < displayOrder)
               break;
         }

         final JLabel tabLabel = new JLabel(page.getName());
         int width = tabLabel.getPreferredSize().width;
         if (width > tabWidth)
            tabWidth = width;
         if (tabHeight <= 0)
            tabHeight = tabLabel.getPreferredSize().height;

         ++i;
         paramTabs.add(page, i);
         paramTabs.setTabComponentAt(i, tabLabel);
         paramTabs.setToolTipTextAt(i, "Debug: parameter-id is " + page.getPageParameter().getId());
      }

      // Ensure that all tabs have the same width and that the tabs will not
      // shrink
      // if the widest tab is removed.
      if (paramTabs.getTabCount() > 0)
      {
         final Dimension preferredSize = new Dimension(tabWidth, tabHeight + 4);
         for (int i = paramTabs.getTabCount() - 1; i >= 0; --i)
            paramTabs.getTabComponentAt(i).setPreferredSize(preferredSize);
      }

      updateContentsEnabled = true;
//      Logger.getLogger(getClass()).debug("end updateVisibility");
   }

   /**
    * Apply the parameter values and visibility to the device.
    */
   public void apply()
   {
      if (device == null)
         return;

      device.clearParameterValues();

      for (final ParamData data: paramDatas.values())
      {
         device.setParameterValue(data.getParameter(), data.getValue());
         if (!data.isVisible())
            device.setParameterVisible(data.getParameter(), false);
      }
   }

   /**
    * Called when a parameter value was changed.
    */
   @Override
   public void stateChanged(ChangeEvent e)
   {
      final ParamData data = (ParamData) e.getSource();

      if (updateContentsEnabled && !inStateChanged && data.hasDependents())
      {

         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {
                  inStateChanged = true;
                  updateVisibility();

                  final Component currentPageComp = paramTabs.getSelectedComponent();
                  if (currentPageComp instanceof Page)
                     ((Page) currentPageComp).updateContents();
               }
               finally
               {
                  inStateChanged = false;
               }
            }
         });
      }
   }
}
