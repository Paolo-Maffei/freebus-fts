package org.freebus.fts.components.parametereditor;

import java.util.HashSet;
import java.util.Set;

import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.products.Parameter;

/**
 * Data / details about a parameter that gets edited in the {@link ParameterEditor}.
 */
public class ParamData
{
   private final Parameter param;
   private final Set<ParamData> dependents = new HashSet<ParamData>();
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
    * Cleanup. Call when the parameter data is no longer required.
    */
   public void dispose()
   {
      dependents.clear();
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
      return true;
   }

   /**
    * Test if the parameter is a page.
    * 
    * @return true if the parameter is a page.
    */
   public boolean isPage()
   {
      return param.getParent() == null || param.getAddress() == null;
   }

   /**
    * @return the display order of the parameter.
    */
   public int getDisplayOrder()
   {
      return param.getDisplayOrder();
   }

   /**
    * Add a dependent parameter-data. This is a parameter-data object
    * whose {@link Parameter} has its parent-parameter set to the
    * parameter of this object.
    */
   public void addDependent(ParamData dependent)
   {
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
      dependents.clear();
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
      if (o == this) return true;
      if (!(o instanceof ParamData)) return false;
      final ParamData oo = (ParamData) o;

      return param.equals(oo.param);
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
