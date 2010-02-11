package org.freebus.fts.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.Program;

/**
 * A panel that allows to edit the parameters of a program.
 */
public class ParameterEditor extends JPanel implements ChangeListener
{
   private static final long serialVersionUID = -2143429346277511397L;

   private JTabbedPane paramTabs = new JTabbedPane(JTabbedPane.LEFT);

   private Program program;
   private final Map<Parameter,ParameterEditorPage> paramPages = new HashMap<Parameter,ParameterEditorPage>();
   private final Map<Parameter,Object> paramValues = new HashMap<Parameter,Object>();
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
            if (!(comp instanceof ParameterEditorPage)) return;

            if (updateContentsEnabled)
               ((ParameterEditorPage) comp).updateContents();
         }
      });
   }

   /**
    * Set the program whose parameters are edited. Calls {@link #updateContents()}.
    */
   public void setProgram(Program program)
   {
      this.program = program;
      updateContents();
   }

   /**
    * @return the program whose parameters are edited, or <code>null</code> if none.
    */
   public Program getProgram()
   {
      return program;
   }

   /**
    * Update the contents of the editor.
    */
   public void updateContents()
   {
      paramPages.clear();
      paramValues.clear();
      paramTabs.removeAll();

      final Set<Parameter> params = program.getParameters();
      for (Parameter param: params)
      {
         paramValues.put(param, param.getDefault());

         if (param.getParent() == null || param.getAddress() == null)
         {
            final ParameterEditorPage page = new ParameterEditorPage(param, paramValues);
            page.addChangeListener(this);

            paramPages.put(param, page);
         }
      }

      for (Parameter param: params)
      {
         final Parameter parentParam = param.getParent();

         if (!paramPages.containsKey(param))
            paramPages.get(parentParam).addParameter(param);
      }      

      updateVisibility();

      ((ParameterEditorPage) paramTabs.getComponentAt(0)).updateContents();
      paramTabs.setSelectedIndex(0);
   }

   /**
    * Update the visibility of the tab pages.
    */
   public void updateVisibility()
   {
      final Set<Parameter> visiblePageParams = new HashSet<Parameter>();
      final Integer nullValue = Integer.valueOf(0);

      for (final Parameter param: paramPages.keySet())
      {
         final Parameter parentParam = param.getParent();
         final Object parentValue = parentParam == null ? nullValue : paramValues.get(parentParam);

         if (parentParam == null || !(parentValue instanceof Integer) ||
               ((Integer) parentValue).equals(param.getParentValue()))
         {
            visiblePageParams.add(param);
         }
      }

      updateContentsEnabled = false;

      // Remove all visible pages that shall not be visible anymore
      for (int i = paramTabs.getComponentCount() - 1; i >= 0; --i)
      {
         final ParameterEditorPage page = (ParameterEditorPage) paramTabs.getComponentAt(i);
         final Parameter param = page.getPageParameter();

         if (visiblePageParams.contains(param))
            visiblePageParams.remove(param);
         else paramTabs.remove(i);
      }

      // Add pages that shall be visible but are not yet
      for (final Parameter param: visiblePageParams)
      {
         final ParameterEditorPage page = paramPages.get(param);
         final int displayOrder = page.getDisplayOrder();

         int i;
         for (i = paramTabs.getComponentCount() - 1; i >= 0; --i)
         {
            final ParameterEditorPage pg = (ParameterEditorPage) paramTabs.getComponentAt(i);
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
