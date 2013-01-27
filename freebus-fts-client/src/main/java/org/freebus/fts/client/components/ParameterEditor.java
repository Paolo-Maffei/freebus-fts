package org.freebus.fts.client.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.freebus.fts.client.components.parametereditor.Page;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.project.Device;
import org.freebus.fts.project.DeviceParameter;
import org.freebus.fts.project.ProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A widget that allows to edit the parameters of a program.
 */
public class ParameterEditor extends JPanel
{
   private static final Logger LOGGER = LoggerFactory.getLogger(ParameterEditor.class);
   private static final long serialVersionUID = -2143429348377511397L;

   private JTabbedPane paramTabs = new JTabbedPane(JTabbedPane.LEFT);

   private Device device;
   private final Vector<Page> paramPages = new Vector<Page>();
   private final EventListenerList listenerList = new EventListenerList();
   private transient ChangeEvent changeEvent = null;
   private boolean updateContentsEnabled, inStateChanged;

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
            if (updateContentsEnabled && comp instanceof Page)
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
            // Lazily create the event
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

      if (device == null)
      {
         updateContentsEnabled = true;
         return;
      }

      //
      // Get the parameters and sort them by display order
      //
      final Set<Parameter> paramsSet = device.getProgram() != null ? device.getProgram().getParameters()
            : new HashSet<Parameter>();
      final Parameter[] paramsSorted = new Parameter[paramsSet.size()];
      paramsSet.toArray(paramsSorted);
      Arrays.sort(paramsSorted, new Comparator<Parameter>()
      {
         @Override
         public int compare(Parameter a, Parameter b)
         {
            return a.getDisplayOrder() - b.getDisplayOrder();
         }
      });

      //
      // Create the parameter pages.
      // Fill the "normal" parameters into the parameter pages.
      //
      Page page = null;
      for (final Parameter param : paramsSorted)
      {
         final DeviceParameter devParam = device.getDeviceParameter(param);
         if (!devParam.isVisible())
            continue;

         if (param.isPage())
         {
            if (page != null && page.getDisplayOrder() == param.getDisplayOrder())
               page.addPageParam(devParam);
            else
            {
               page = new Page(devParam);
               page.addChangeListener(paramValueChangedListener);
               paramPages.add(page);
               paramTabs.add(page.getName(), page);
               paramTabs.setToolTipTextAt(paramTabs.getTabCount() - 1, "Debug: parameter #"
                     + page.getVisibleDevParameter().getParameter().getNumber());
            }
         }
         else if (page != null)
         {
            page.addParam(devParam);
         }
         else
         {
            // Parameter does not belong to a page
            LOGGER.debug("Parameter does not belong to a page: " + param);
         }
      }

      // updatePagesVisibility();

      if (paramTabs.getComponentCount() > 0)
      {
         updateContentsEnabled = false;
         ((Page) paramTabs.getComponentAt(0)).updateContents();
         paramTabs.setSelectedIndex(0);
         updateContentsEnabled = true;
      }

      updateContentsEnabled = true;
   }

   /**
    * Update the visibility of the parameter pages.
    */
   public void updatePagesVisibility()
   {
      // get selected tab title
      int selectedIndex = paramTabs.getSelectedIndex();
      String selectedTitle = paramTabs.getTitleAt(selectedIndex);

      // process changes
      updateContents();

      // set selected tab
      for (int i = paramTabs.getTabCount() - 1; i >= 0; --i)
         if (selectedTitle.equals(paramTabs.getTitleAt(i)))
            paramTabs.setSelectedIndex(i);
   }

   /**
    * Apply the parameter values and visibility to the device.
    */
   public void apply()
   {
      if (device != null)
         ProjectManager.getController().parametersChanged(device);
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

         fireStateChanged();

         if (!inStateChanged)
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               @Override
               public void run()
               {
                  try
                  {
                     inStateChanged = true;
                     updatePagesVisibility();

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
