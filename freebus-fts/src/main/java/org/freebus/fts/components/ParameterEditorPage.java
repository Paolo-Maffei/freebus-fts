package org.freebus.fts.components;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.ParameterType;
import org.freebus.fts.products.ParameterValue;

/**
 * A tab page of the parameter editor.
 * 
 * @see {@link ParameterEditor}.
 */
public class ParameterEditorPage extends JPanel
{
   private static final long serialVersionUID = 719661697391054660L;

   private final Parameter pageParam;
   private final Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
   private final Set<Parameter> childParams = new HashSet<Parameter>();
   private final Map<Parameter, Object> paramValues;
   private boolean childsChanged = false;
   private final JPanel content;

   /**
    * Create a tab page for the parameter editor.
    * 
    * @param pageParam the parameter which is displayed in the page, including
    *           its children.
    */
   public ParameterEditorPage(final Parameter pageParam, final Map<Parameter, Object> paramValues)
   {
      super(new GridBagLayout());

      this.pageParam = pageParam;
      this.paramValues = paramValues;

      setName(pageParam.getDescription());

      final Insets insets = new Insets(0, 0, 0, 0);
      final JLabel title = new JLabel(pageParam.getDescription());
      title.setFont(title.getFont().deriveFont(Font.BOLD));
      add(title, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(6, 4, 6, 4), 0, 0));

      add(new JSeparator(JSeparator.HORIZONTAL), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, insets, 0, 0));

      content = new JPanel(new GridBagLayout());
      content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      add(content, new GridBagConstraints(0, 2, 1, 1, 1, 10, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            insets, 0, 0));

      add(new JPanel(), new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
            insets, 0, 0));
   }

   /**
    * @return the parameter that represents the page.
    */
   public Parameter getPageParameter()
   {
      return pageParam;
   }

   /**
    * @return the display order of the page.
    */
   public int getDisplayOrder()
   {
      return pageParam.getDisplayOrder();
   }

   /**
    * Add a listener that gets called when a parameter value is changed.
    * 
    * @param listener the listener to be added.
    */
   public void addChangeListener(ChangeListener listener)
   {
      changeListeners.add(listener);
   }

   /**
    * Remove a change listener.
    * 
    * @param listener the listener to be removed.
    */
   public void removeChangeListener(ChangeListener listener)
   {
      changeListeners.remove(listener);
   }

   /**
    * Add a parameter to the page.
    */
   public void addParameter(Parameter param)
   {
      childParams.add(param);
      childsChanged = true;
   }

   /**
    * Update the contents of the page. Called when the page gets visible.
    */
   public void updateContents()
   {
      if (childsChanged)
      {
         childsChanged = false;
         content.removeAll();
   
         int gridRow = -1;
         for (final Parameter param : sortByDisplayOrder(childParams))
         {
            createParamComponent(param, ++gridRow);
         }
      }
   }

   /**
    * Create a component that can be used to edit the value of the parameter
    * <code>param</code>.
    */
   public void createParamComponent(final Parameter param, int gridRow)
   {
      final ParameterAtomicType atomicType = param.getParameterType().getAtomicType();
      if (atomicType == ParameterAtomicType.NONE)
      {
         createParamNoneComponent(param, gridRow);
      }
      else if (atomicType == ParameterAtomicType.STRING)
      {
         createParamStringComponent(param, gridRow);
      }
      else if (atomicType == ParameterAtomicType.SIGNED || atomicType == ParameterAtomicType.UNSIGNED)
      {
         createParamNumberComponent(param, gridRow);
      }
      else if (atomicType == ParameterAtomicType.ENUM || atomicType == ParameterAtomicType.LONG_ENUM)
      {
         createParamEnumComponent(param, gridRow);
      }
      else
      {
         createParamLabel(param, gridRow, 1);
         contentsAddComponent(new JLabel("Unsupported atomic type " + atomicType), gridRow);
      }
   }

   /**
    * Create a {@link JComponent} for a parameter of the atomic type
    * {@link ParameterAtomicType.ENUM}.
    * 
    * @param param the parameter to process.
    * @param gridRow the row of the contents grid in which the component(s) are
    *           to be added.
    */
   public void createParamEnumComponent(final Parameter param, int gridRow)
   {
      final Set<ParameterValue> valuesSet = param.getParameterType().getValues();
      final ParameterValue[] values = new ParameterValue[valuesSet.size()];
      valuesSet.toArray(values);

      Arrays.sort(values, new Comparator<ParameterValue>()
      {
         @Override
         public int compare(ParameterValue a, ParameterValue b)
         {
            return b.getDisplayOrder() - a.getDisplayOrder();
         }
      });

      final JComboBox combo = new JComboBox(new DefaultComboBoxModel(values));
      final ListCellRenderer origRenderer = combo.getRenderer();

      combo.setRenderer(new ListCellRenderer()
      {
         @Override
         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
               boolean cellHasFocus)
         {
            if (value instanceof ParameterValue)
               value = ((ParameterValue) value).getDisplayedValue();

            return origRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         }
      });

      combo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final ParameterValue val = (ParameterValue) combo.getSelectedItem();
            if (val != null)
            {
               paramValues.put(param, val.getIntValue()); 
               fireStateChanged(param);
            }
         }
      });

      final int defaultValue = (Integer) paramValues.get(param);
      for (final ParameterValue val: values)
      {
         if (val.getIntValue() == defaultValue)
         {
            combo.setSelectedItem(val);
            break;
         }
      }

      createParamLabel(param, gridRow, 1);
      contentsAddComponent(combo, gridRow);
   }

   /**
    * Create a {@link JComponent} for a parameter of the atomic type
    * {@link ParameterAtomicType.SIGNED} or {@link ParameterAtomicType.UNSIGNED}
    * .
    * 
    * @param param the parameter to process.
    * @param gridRow the row of the contents grid in which the component(s) are
    *           to be added.
    */
   public void createParamNumberComponent(final Parameter param, int gridRow)
   {
      final ParameterType paramType = param.getParameterType();

      final int value = (Integer)paramValues.get(param);
      final int min = paramType.getMinValue();
      final int max = paramType.getMaxValue();
      final SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, 1);
      final JSpinner spinner = new JSpinner(model);

      model.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            paramValues.put(param, model.getValue());
            fireStateChanged(param);
         }
      });

      createParamLabel(param, gridRow, 1);
      contentsAddComponent(spinner, gridRow);
   }

   /**
    * Create a {@link JComponent} for a parameter of the atomic type
    * {@link ParameterAtomicType.STRING}.
    * 
    * @param param the parameter to process.
    * @param gridRow the row of the contents grid in which the component(s) are
    *           to be added.
    */
   public void createParamStringComponent(final Parameter param, int gridRow)
   {
      createParamLabel(param, gridRow, 1);

      final JTextArea input = new JTextArea();
      input.setText((String) paramValues.get(param));

      contentsAddComponent(input, gridRow);
   }

   /**
    * Create a {@link JComponent} for a parameter of the atomic type
    * {@link ParameterAtomicType.NONE}.
    * 
    * @param param the parameter to process.
    * @param gridRow the row of the contents grid in which the component(s) are
    *           to be added.
    */
   public void createParamNoneComponent(final Parameter param, int gridRow)
   {
      final JLabel lbl = createParamLabel(param, gridRow, 2);
      lbl.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
   }

   /**
    * Create a label for the parameter. The label is added to the contents grid
    * in column zero, spanning one or more columns.
    * 
    * @param param the parameter whose label shall be used.
    * @param gridRow the row of the contents grid in which the label is placed.
    * @param gridWidth the number of grid columns the label shall use (usually 1
    *           or 2).
    *
    * @return The created label.
    */
   public JLabel createParamLabel(final Parameter param, int gridRow, int gridWidth)
   {
      final JLabel lbl = new JLabel(param.getDescription());

      content.add(lbl, new GridBagConstraints(0, gridRow, gridWidth, 1, 1, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(0, 4, 0, 4), 0, 0));

      return lbl;
   }

   /**
    * Add the component to the grid in column zero, row <code>gridRow</code>,
    * using suitable grid bag constraints.
    * 
    * @param comp the component to be added.
    * @param gridRow the row of the contents grid in which the component is
    *           added.
    */
   public void contentsAddComponent(final JComponent comp, int gridRow)
   {
      content.add(comp, new GridBagConstraints(1, gridRow, 1, 1, 4, 0, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 4), 0, 0));
   }

   /**
    * Inform all change listeners that a parameter was changed.
    * 
    * @param param the parameter that was changed.
    */
   public void fireStateChanged(final Parameter param)
   {
      final ChangeEvent e = new ChangeEvent(param);

      for (ChangeListener listener: changeListeners)
         listener.stateChanged(e);
   }

   /**
    * Sort the parameters by display order {@link Parameter#getDisplayOrder()}.
    * 
    * @param params the parameters to be sorted.
    * 
    * @return a sorted array of parameters.
    */
   public static Parameter[] sortByDisplayOrder(final Set<Parameter> params)
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
}
