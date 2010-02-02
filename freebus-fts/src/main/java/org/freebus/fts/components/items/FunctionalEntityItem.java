package org.freebus.fts.components.items;

import org.freebus.fts.products.FunctionalEntity;

/**
 * An item that holds a {@link FunctionalEntity}, returns the functional entities
 * name in {@link #toString()}, and is comparable by name - and can therefore
 * be sorted.
 * 
 * This is a useful helper class for creating lists of functional entities,
 * like for {@link JList} or {@link JComboBox}.
 */
public class FunctionalEntityItem implements Comparable<FunctionalEntity>
{
   private FunctionalEntity funcEnt;

   /**
    * Create an empty object.
    */
   public FunctionalEntityItem()
   {
      this(null);
   }
   
   /**
    * Create an object with a functional entity.
    */
   public FunctionalEntityItem(FunctionalEntity funcEnt)
   {
      this.funcEnt = funcEnt;
   }

   /**
    * @return the manufacturer.
    */
   public FunctionalEntity getFunctionalEntity()
   {
      return funcEnt;
   }

   /**
    * Set the manufacturer.
    */
   public void setFunctionalEntity(FunctionalEntity manufacturer)
   {
      this.funcEnt = manufacturer;
   }

   /**
    * Compare two items by comparing their functional entities' names.
    */
   @Override
   public int compareTo(FunctionalEntity o)
   {
      if (funcEnt == o) return 0;
      if (funcEnt == null) return -1;
      return funcEnt.getName().compareTo(o.getName());
   }

   /**
    * @return the name of the functional entity.
    */
   @Override
   public String toString()
   {
      if (funcEnt == null) return null;
      return funcEnt.getName();
   }
}
