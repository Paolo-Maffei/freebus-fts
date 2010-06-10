package org.freebus.fts.components.parametereditor;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.freebus.fts.components.ParameterEditor;
import org.freebus.fts.products.Parameter;

/**
 * Data / details about a parameter that gets edited in the
 * {@link ParameterEditor}.
 */
public class ParamData
{
   private final Parameter param;
   private final Set<ParamData> childs = new HashSet<ParamData>();
   private ParamData parent;
   private Object value;

   /**
    * Create a parameter-data object.
    */
   public ParamData(Parameter param)
   {
      this.param = param;
      if (param.getId() == 162206)
      {
         // Debug hook
         Logger.getLogger(getClass()).debug("Param: " + param);
      }

      value = param.getDefault();
   }

   /**
    * @return the parent parameter-data object. This is the parameter on which
    *         the visibility of this parameter depends.
    */
   public ParamData getParent()
   {
      return parent;
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
      if (param.getId() == 42869)
      {
         // Debug hook
         Logger.getLogger(getClass()).debug("Param: " + param);
      }

      if (parent == null)
      {
         if (param.getLowAccess() == 0 && param.getHighAccess() == 0)
            return false;
         else return true;
      }

      if (!parent.isVisible())
         return false;

      final Integer expectedParentValue = param.getParentValue();
      if (expectedParentValue == null)
         return parent.isVisible();

      return expectedParentValue.equals(parent.getValue());
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
      for (ParamData data = this; data != null; data = data.getParent())
      {
         Integer val = data.getParameter().getParentValue();
         if (val != null)
            return val;
      }

      return null;
   }

   /**
    * Get the current value of any parent parameter. The first non-null value
    * that is found is returned. If no value is found, null is returned.
    *
    * @return the current value of the any parent parameter.
    */
   public Integer getParentValue()
   {
      for (ParamData data = getParent(); data != null; data = data.getParent())
      {
         final Object obj = data.getValue();
         if (obj != null)
         {
            if (obj instanceof Integer)
               return (Integer) obj;
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
    * Add a child parameter-data. This is a parameter-data object whose
    * {@link Parameter} has its parent-parameter set to the parameter of this
    * object. Sets the {@link #getParent() parent} of the child.
    *
    * @param child - the child to add
    */
   public void addChild(ParamData child)
   {
      if (child.parent != null)
         child.parent.removeChild(child);

      child.parent = this;
      childs.add(child);
   }

   /**
    * Remove a child parameter-data. Clears the {@link #getParent() parent} of
    * the child.
    *
    * @param child - the child to remove.
    */
   public void removeChild(ParamData child)
   {
      child.parent = null;
      childs.remove(child);
   }

   /**
    * Returns the child parameter-data objects.
    */
   public Set<ParamData> getChildren()
   {
      return childs;
   }

   /**
    * Clear the child parameter-data objects.
    */
   public void removeAllChildren()
   {
      for (final ParamData dependent : childs)
         dependent.parent = null;

      childs.clear();
   }

   /**
    * @return if the object has child parameter-data objects.
    */
   public boolean hasChildren()
   {
      return !childs.isEmpty();
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
