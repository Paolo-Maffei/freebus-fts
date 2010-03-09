package org.freebus.fts.components.parametereditor;

import java.util.HashSet;
import java.util.Set;

import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.products.Parameter;

/**
 * Data / details about a parameter that gets edited in the
 * {@link ParameterEditor}.
 */
public class ParamData
{
   private final Parameter param;
   private final Set<ParamData> dependents = new HashSet<ParamData>();
   private ParamData parentData;
   private Object value;

   /**
    * Create a parameter-data object.
    */
   public ParamData(Parameter param)
   {
      this.param = param;

      value = param.getDefault();
   }

   /**
    * @return the parent parameter-data. This is the parameter on which the
    *         visibility of this parameter depends.
    */
   public ParamData getParentData()
   {
      return parentData;
   }

   /**
    * @return the value of the parameter.
    */
   public Object getValue()
   {
      return value;
   }

   /**
    * Set the value of the parameter.
    * 
    * @param value the value to set
    */
   public void setValue(Object value)
   {
      this.value = value;
   }

   /**
    * @return the parameter.
    */
   public Parameter getParameter()
   {
      return param;
   }

   /**
    * Test the visibility of the parameter.
    * 
    * @return true if the parameter is visible.
    */
   public boolean isVisible()
   {
//      if (param.getId() == 23315)
//      {
//         // Debug hook
//         Logger.getLogger(getClass()).debug("Param: " + param);
//      }

      if (parentData == null)
         return true;

      if (!parentData.isVisible())
         return false;

      final Integer expectedParentValue = param.getParentValue();
      if (expectedParentValue == null)
         return parentData.isVisible();

      return expectedParentValue.equals(parentData.getValue());
   }

   /**
    * Get the expected parent-value of this or any parent parameter.
    *
    * @return the expected parent-value. This is the first non-null
    *         {@link Parameter#getParentValue} of the parameter or one of its
    *         parents. However, the result can still be null.
    */
   public Integer getExpectedValue()
   {
      for (ParamData data = this; data != null; data = data.getParentData())
      {
         Integer val = data.getParameter().getParentValue();
         if (val != null)
            return val;
      }

      return null;
   }

   /**
    * Get the current value of any parent parameter.
    *
    * @return the current value of the parent parameter, or of the parent's
    *         parents. The first non-null value that is found is returned. If no
    *         value is found, null is returned.
    */
   public Integer getParentValue()
   {
      for (ParamData data = getParentData(); data != null; data = data.getParentData())
      {
         final Object obj = data.getValue();
         if (obj != null)
         {
            if (obj instanceof Integer) return (Integer) obj;
            return null;
         }
      }

      return null;
   }
   

   /**
    * Test if the parameter denotes a page. 
    * 
    * @return true if the parameter is a page.
    */
   public boolean isPage()
   {
      return param.getAddress() == null;
   }

   /**
    * @return the display order of the parameter.
    */
   public int getDisplayOrder()
   {
      return param.getDisplayOrder();
   }

   /**
    * Add a dependent parameter-data. This is a parameter-data object whose
    * {@link Parameter} has its parent-parameter set to the parameter of this
    * object.
    */
   public void addDependent(ParamData dependent)
   {
      dependent.parentData = this;
      dependents.add(dependent);
   }

   /**
    * Returns the dependent parameter-data objects.
    */
   public Set<ParamData> getDependents()
   {
      return dependents;
   }

   /**
    * Clear the dependent parameter-data objects.
    */
   public void removeAllDependents()
   {
      for (final ParamData dependent : dependents)
         dependent.parentData = null;

      dependents.clear();
   }

   /**
    * @return if the object has dependent objects.
    */
   public boolean hasDependents()
   {
      return !dependents.isEmpty();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      return param.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;
      if (!(o instanceof ParamData))
         return false;
      final ParamData oo = (ParamData) o;

      return param.getId() == oo.param.getId();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return param.getName() + "[" + param.getId() + "] = " + value;
   }
}
