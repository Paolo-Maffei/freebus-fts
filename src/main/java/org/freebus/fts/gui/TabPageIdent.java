package org.freebus.fts.gui;

/**
 * An identifier for a {@link WorkBench}'s tab-page.
 * 
 * Objects of this class are used in the {@link WorkBench} class for
 * identifying tab-pages.
 */
final class TabPageIdent
{
   /**
    * The class of the tab-page that is identified.
    */
   public final Class<? extends TabPage> tabPageClass;
   
   /**
    * The object that the tab-page shows.
    */
   public final Object object;

   private int hashCode = 0;

   /**
    * Create a tab-page identifier object.
    *
    * @param tabPageClass - the class of the {@link TabPage}.
    * @param object - the object that is shown in the {@link TabPage}.
    */
   public TabPageIdent(Class<? extends TabPage> tabPageClass, Object object)
   {
      super();
      this.tabPageClass = tabPageClass;
      this.object = object;
   }

   /**
    * @return a hash-code for the object.
    */
   @Override
   public int hashCode()
   {
      if (hashCode == 0)
      {
         hashCode = tabPageClass.hashCode() * 13;
         if (object != null) hashCode += object.hashCode();
      }
      return hashCode;
   }

   /**
    * @return true if this object is the same as the object o.
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this) return true;
      if (!(o instanceof TabPageIdent)) return false;
      final TabPageIdent oo = (TabPageIdent) o;
      return tabPageClass == oo.tabPageClass && object == oo.object;
   }
}
