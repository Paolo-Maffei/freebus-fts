package org.freebus.fts.core;

import org.freebus.fts.components.AbstractPage;
import org.freebus.fts.components.WorkBench;

/**
 * Class for IDs of pages of a {@link WorkBench}.
 */
public class WorkBenchPageId
{
   private final Class<? extends AbstractPage> pageClass;
   private final Object obj;
   private int hashCode;

   /**
    * Create a workbench-page ID
    * 
    * @param pageClass - the class of the workbench-page.
    * @param obj - the object that describes the page's contents.
    */
   public WorkBenchPageId(final Class<? extends AbstractPage> pageClass, final Object obj)
   {
      this.pageClass = pageClass;
      this.obj = obj;
   }

   /**
    * @return The class of the workbench-page.
    */
   public Class<? extends AbstractPage> getPageClass()
   {
      return pageClass;
   }

   /**
    * @return The object that describes the page's contents.
    */
   public Object getObject()
   {
      return obj;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object o)
   {
      if (o == this)
         return true;
      if (!(o instanceof WorkBenchPageId))
         return false;
      final WorkBenchPageId oo = (WorkBenchPageId)o;
      return pageClass == oo.pageClass && (obj == null ? oo.obj == null : obj.equals(oo.obj));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      if (hashCode == 0)
         hashCode = 1 + (pageClass.hashCode() << 15) + (obj == null ? 0 : obj.hashCode());
      return hashCode;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return pageClass.getSimpleName() + " " + obj.toString();
   }
}
