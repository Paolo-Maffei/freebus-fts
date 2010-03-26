package org.freebus.fts.components.parametereditor;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.products.Parameter;
import org.freebus.fts.products.ParameterAtomicType;
import org.freebus.fts.products.ParameterType;
import org.freebus.fts.products.ParameterValue;

/**
 * A tab page of the parameter editor.
 *
 * @see {@link ParameterEditor}.
 */
public class Page extends JPanel
{
   private static final long serialVersionUID = 719661697391054660L;

   private final Set<ChangeListener> changeListeners = new HashSet<ChangeListener>();
   private final List<ParamData> childDatas = new Vector<ParamData>();
   private final List<ParamData> pageDatas = new Vector<ParamData>();
   private final JPanel contents;
   private final JLabel title;
   private final int displayOrder;

   /**
    * Create a tab page for the parameter editor.
    *
    * @param data the parameter-data for the parameter which is displayed on
    *           this the page.
    */
   public Page(final ParamData data)
   {
      super(new GridBagLayout());

      displayOrder = data.getDisplayOrder();
      addParamData(data);

      final Insets insets = new Insets(8, 8, 8, 8);
      final Insets separatorInsets = new Insets(0, 8, 0, 8);

      title = new JLabel("???");
      title.setFont(title.getFont().deriveFont(Font.BOLD).deriveFont(title.getFont().getSize() * 1.1f));
      add(title, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            insets, 0, 0));

      add(new JSeparator(JSeparator.HORIZONTAL), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, separatorInsets, 0, 0));

      contents = new JPanel(new GridBagLayout());
      add(contents, new GridBagConstraints(0, 2, 1, 1, 1, 10, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            insets, 0, 0));

      add(new JPanel(), new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
            insets, 0, 0));

      updateName();
   }

   /**
    * @return the parameter that represents the page.
    */
   public Parameter getPageParameter()
   {
      return getPageData().getParameter();
   }

   /**
    * Returns the first visible parameter-data. If no parameter-data is visible,
    * the first parameter-data of the page parameter-data's is returned anyways.
    *
    * @return the parameter-data that represents the page.
    */
   public ParamData getPageData()
   {
      for (final ParamData data : pageDatas)
      {
         if (data.isVisible())
            return data;
      }

      if (pageDatas.isEmpty())
         return null;
      return pageDatas.get(0);
   }

   /**
    * @return the display order of the page.
    */
   public int getDisplayOrder()
   {
      return displayOrder;
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
    * Add a parameter-data object to the page.
    */
   public void addParamData(ParamData data)
   {
      if (data.isPage() && data.getDisplayOrder() == displayOrder)
         pageDatas.add(data);
      else childDatas.add(data);
   }

   /**
    * Update the contents of the page. Called when the page gets visible.
    */
   public void updateContents()
   {
      updateName();
      contents.removeAll();

      int gridRow = -1;
      for (final ParamData data : childDatas)
      {
         if (data.isVisible())
            createParamComponent(data, ++gridRow);
      }
   }

   /**
    * Update the name of the page.
    */
   public void updateName()
   {
      final ParamData pageData = getPageData();
      if (pageData == null)
      {
         setName("Unnamed");
         title.setText("Unnamed");
      }
      else
      {
         final Parameter pageParam = pageData.getParameter();

         String name = pageParam.getDescription();
         if (name == null || name.isEmpty())
            name = pageParam.getName();

         setName(name);
         title.setText(name);
      }
   }

   /**
    * Create a component that can be used to edit the value of the
    * parameter-data <code>data</code>.
    */
   public void createParamComponent(final ParamData data, int gridRow)
   {
      final Parameter param = data.getParameter();
      final ParameterAtomicType atomicType = param.getParameterType().getAtomicType();

      if (atomicType == ParameterAtomicType.NONE)
      {
         createParamNoneComponent(data, gridRow);
      }
      else if (atomicType == ParameterAtomicType.STRING)
      {
         createParamStringComponent(data, gridRow);
      }
      else if (atomicType == ParameterAtomicType.SIGNED || atomicType == ParameterAtomicType.UNSIGNED)
      {
         createParamNumberComponent(data, gridRow);
      }
      else if (atomicType == ParameterAtomicType.ENUM || atomicType == ParameterAtomicType.LONG_ENUM)
      {
         createParamEnumComponent(data, gridRow);
      }
      else
      {
         createParamLabel(param, gridRow, 1);
         contentAddComponent(new JLabel("Unsupported atomic type " + atomicType), gridRow);
      }
   }

   /**
    * Create a {@link JComponent} for a parameter of the atomic type
    * {@link ParameterAtomicType.ENUM}.
    *
    * @param data the parameter-data to process.
    * @param gridRow the row of the contents grid in which the component(s) are
    *           to be added.
    */
   public void createParamEnumComponent(final ParamData data, int gridRow)
   {
      final Parameter param = data.getParameter();
      final Set<ParameterValue> valuesSet = param.getParameterType().getValues();
      final ParameterValue[] values = new ParameterValue[valuesSet.size()];
      valuesSet.toArray(values);

      createParamLabel(param, gridRow, 1);

      if (values.length <= 1)
      {
         if (values.length == 1)
         {
            final JLabel lbl = new JLabel(values[0].getDisplayedValue());
            lbl.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            contentAddComponent(lbl, gridRow);
         }
         return;
      }

      Arrays.sort(values, new Comparator<ParameterValue>()
      {
         @Override
         public int compare(ParameterValue a, ParameterValue b)
         {
            return a.getDisplayOrder() - b.getDisplayOrder();
         }
      });

      final JComboBox combo = new JComboBox(new DefaultComboBoxModel(values));
      combo.setName("param-" + param.getId());

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

      final int defaultValue = (Integer) data.getValue();
      for (final ParameterValue val : values)
      {
         if (val.getIntValue() == defaultValue)
         {
            combo.setSelectedItem(val);
            break;
         }
      }

      combo.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            final ParameterValue val = (ParameterValue) combo.getSelectedItem();
            data.setValue(val == null ? null : val.getIntValue());
            fireStateChanged(data);
         }
      });

      contentAddComponent(combo, gridRow);
   }

   /**
    * Create a {@link JComponent} for a parameter of the atomic type
    * {@link ParameterAtomicType.SIGNED} or {@link ParameterAtomicType.UNSIGNED}
    * .
    *
    * @param data the parameter-data to process.
    * @param gridRow the row of the contents grid in which the component(s) are
    *           to be added.
    */
   public void createParamNumberComponent(final ParamData data, int gridRow)
   {
      final Parameter param = data.getParameter();
      final ParameterType paramType = param.getParameterType();

      final int min = paramType.getMinValue();
      final int max = paramType.getMaxValue();

      int value = (Integer) data.getValue();
      if (value < min)
         value = min;
      else if (value > max)
         value = max;

      final SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, 1);
      final JSpinner spinner = new JSpinner(model);
      spinner.setName("param-" + param.getId());

      model.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            data.setValue(model.getValue());
            fireStateChanged(data);
         }
      });

      createParamLabel(param, gridRow, 1);
      contentAddComponent(spinner, gridRow);
   }

   /**
    * Create a {@link JComponent} for a parameter of the atomic type
    * {@link ParameterAtomicType.STRING}.
    *
    * @param param the parameter to process.
    * @param gridRow the row of the contents grid in which the component(s) are
    *           to be added.
    */
   public void createParamStringComponent(final ParamData data, int gridRow)
   {
      final Parameter param = data.getParameter();
      createParamLabel(param, gridRow, 1);

      final JTextArea input = new JTextArea();
      input.setText((String) data.getValue());
      input.setName("param-" + param.getId());

      contentAddComponent(input, gridRow);

      input.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void removeUpdate(DocumentEvent e)
         {
            data.setValue(input.getText());
            fireStateChanged(data);
         }

         @Override
         public void insertUpdate(DocumentEvent e)
         {
            data.setValue(input.getText());
            fireStateChanged(data);
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
         }
      });
   }

   /**
    * Create a {@link JComponent} for a parameter of the atomic type
    * {@link ParameterAtomicType.NONE}.
    *
    * @param data the parameter-data to process.
    * @param gridRow the row of the contents grid in which the component(s) are
    *           to be added.
    */
   public void createParamNoneComponent(final ParamData data, int gridRow)
   {
      final Parameter param = data.getParameter();
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
      final String str = "<html>" + param.getDescription().replace("\\r", "").replace("\\n", "<br>") + "</html>";
      final JLabel lbl = new JLabel(str);
      lbl.setToolTipText("Debug: parameter-id is " + param.getId());
      lbl.setName("param-" + param.getId());

      contents.add(lbl, new GridBagConstraints(0, gridRow, gridWidth, 1, 1, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(2, 0, 2, 4), 0, 0));

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
   public void contentAddComponent(final JComponent comp, int gridRow)
   {
      contents.add(comp, new GridBagConstraints(1, gridRow, 1, 1, 4, 0, GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL, new Insets(6, 4, 6, 4), 0, 0));
   }

   /**
    * Inform all change listeners that a parameter was changed.
    *
    * @param param the parameter that was changed.
    */
   public void fireStateChanged(final ParamData data)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            final ChangeEvent e = new ChangeEvent(data);

            for (ChangeListener listener : changeListeners)
               listener.stateChanged(e);
         }
      });
   }

   /**
    * Sort the parameter-data by display order
    * {@link Parameter#getDisplayOrder()}.
    *
    * @param params the parameters to be sorted.
    *
    * @return a sorted array of parameters.
    */
   public static ParamData[] sortByDisplayOrder(final Collection<ParamData> paramsData)
   {
      final ParamData[] paramsDataSorted = new ParamData[paramsData.size()];
      paramsData.toArray(paramsDataSorted);
      Arrays.sort(paramsDataSorted, new Comparator<ParamData>()
      {
         @Override
         public int compare(ParamData a, ParamData b)
         {
            return a.getDisplayOrder() - b.getDisplayOrder();
         }
      });

      return paramsDataSorted;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return getPageData().hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;
      if (!(o instanceof Page))
         return false;
      final Page oo = (Page) o;

      return pageDatas.equals(oo.pageDatas);
   }
}
