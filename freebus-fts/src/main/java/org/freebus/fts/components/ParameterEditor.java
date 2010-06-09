package org.freebus.fts.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Comparator;
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
import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.freebus.fts.components.parametereditor.DeviceParamData;
import org.freebus.fts.components.parametereditor.Page;
import org.freebus.fts.components.parametereditor.ParamData;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.project.Device;

/**
 * A widget that allows to edit the parameters of a program.
 */
public class ParameterEditor extends JPanel
{
   private static final long serialVersionUID = -2143429346277511397L;

   private JTabbedPane paramTabs = new JTabbedPane(JTabbedPane.LEFT);
   private int tabWidth = 150;
   private int tabHeight;

   private Device device;
   private Map<Parameter, ParamData> paramDatas;
   private final List<Page> paramPages = new Vector<Page>();
   private final EventListenerList listenerList = new EventListenerList();
   private transient ChangeEvent changeEvent = null;
   private boolean updateContentsEnabled;
   private boolean inStateChanged;

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
    * Adds a <code>ChangeListener</code> to this object.
    *
    * @param l the <code>ChangeListener</code> to add
    *
    * @see #fireStateChanged
    * @see #removeChangeListener
    */
   public void addChangeListener(ChangeListener l)
   {
      listenerList.add(ChangeListener.class, l);
   }

   /**
    * Removes a <code>ChangeListener</code> from this object.
    *
    * @param l the <code>ChangeListener</code> to remove
    * @see #fireStateChanged
    * @see #addChangeListener
    */
   public void removeChangeListener(ChangeListener l)
   {
      listenerList.remove(ChangeListener.class, l);
   }

   /**
    * Sends a {@code ChangeEvent}, with this {@code ParameterEditor} as the
    * source, to each registered listener. This method is called each time there
    * is a change of a parameter value.
    */
   protected void fireStateChanged()
   {
      Object[] listeners = listenerList.getListenerList();

      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length - 2; i >= 0; i -= 2)
      {
         if (listeners[i] == ChangeListener.class)
         {
            // Lazily create the event:
            if (changeEvent == null)
               changeEvent = new ChangeEvent(this);
            ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
         }
      }
   }

   /**
    * Update the contents of the editor.
    */
   public void updateContents()
   {
      updateContentsEnabled = false;

      paramPages.clear();
      paramTabs.removeAll();
      paramDatas = DeviceParamData.createParamData(device);

      // Sort the parameter-data objects by display-order
      final ParamData[] paramDataArr = new ParamData[paramDatas.size()];
      paramDatas.values().toArray(paramDataArr);
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
                  String.format("Parameter #%1$d has an unknown parent parameter #%2$d", param.getId(),
                        parentParam.getId()));
            continue;
         }

         paramDatas.get(parentParam).addChild(paramDatas.get(param));
      }

      // Create the parameter pages and add the parameter-data objects to their
      // pages
      Page page = null;
      for (final ParamData data : paramDataArr)
      {
         if (data.isPage() && (page == null || page.getDisplayOrder() != data.getDisplayOrder()))
         {
            page = new Page(data);
            page.addChangeListener(paramValueChangedListener);

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
      // Logger.getLogger(getClass()).debug("start updateVisibility");
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
      // Logger.getLogger(getClass()).debug("end updateVisibility");
   }

   /**
    * Apply the parameter values and visibility to the device.
    */
   public void apply()
   {
      if (device != null)
         DeviceParamData.applyParamData(device, paramDatas);
   }

   /**
    * Called when a parameter value was changed.
    */
   private final ChangeListener paramValueChangedListener = new ChangeListener()
   {
      @Override
      public void stateChanged(final ChangeEvent e)
      {
         if (!updateContentsEnabled)
            return;

         final ParamData data = (ParamData) e.getSource();
         fireStateChanged();

         if (!inStateChanged && data.hasChildren())
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
   };
}
