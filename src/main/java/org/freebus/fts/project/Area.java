package org.freebus.fts.project;

import org.freebus.fts.project.generic.Node;


/**
 * An area in a {@link Project}. An area is the top-level
 * group for the topological structure of an EIB bus.
 */
public class Area extends Node<Line>
{
   /**
    * Create a new area with the given name.
    */
   public Area(String name)
   {
      super(name, 0);
   }

   /**
    * Create a new area with the given name and identifier.
    */
   public Area(String name, int id)
   {
      super(name, id);
   }

   /**
    * Set the id of the area.
    * Updates the id's of all devices on the lines.
    */
   @Override
   public void setId(int id)
   {
      super.setId(id);
      for (int i=children.size()-1; i>=0; --i)
      {
         final Line e = children.get(i);
         e.setId(i + 1);
         e.updateIds(id);
      }
   }

   /**
    * Create a new {@link Line} with the given name.
    * The line is appended to the end of the list of lines.
    */
   public Line createLine(String name)
   {
      final Line line = new Line(name);
      add(line);
      return line;
   }
}
