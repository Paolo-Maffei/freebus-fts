package org.freebus.fts.project;

import java.util.Vector;

/**
 * All children of a {@link Project}.
 */
public class Areas
{
   private final Vector<Area> children = new Vector<Area>(); 
   
   /**
    * @return the number of children.
    */
   public int size()
   {
      return children.size();
   }
   
   /**
    * @return the idx-th area.
    */
   public Area get(int idx)
   {
      return children.elementAt(idx);
   }

   /**
    * Add a area.
    */
   public void add(Area area)
   {
      children.add(area);
   }

   /**
    * Remove a area.
    */
   public void remove(Area area)
   {
      children.remove(area);
   }

   /**
    * Remove the idx-th area.
    */
   public void remove(int idx)
   {
      children.remove(idx);
   }

   /**
    * Removes all children.
    */
   public void removeAll()
   {
      children.removeAllElements();
   }

   /**
    * Reassign all physical addresses of the areas.
    */
   public void assignIds()
   {
      for (int i=children.size()-1; i>=0; --i)
      {
         final Area area = children.get(i);
         area.setId(i + 1);
      }
   }

   /**
    * Create a new {@link Area} with the given name.
    * The area is appended to the end of the list of children.
    */
   public Area createArea(String name)
   {
      final Area area = new Area(name);
      children.add(area);
      return area;
   }
}
