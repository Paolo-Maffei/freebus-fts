package org.freebus.fts.project;

import org.freebus.fts.project.generic.Node;


/**
 * A main-group in a {@link Project}. A main-group is the top-level
 * group for the logical structure of an EIB bus.
 */
public class MainGroup extends Node<MiddleGroup>
{
   /**
    * Create a new main-group with the given name.
    */
   public MainGroup(String name)
   {
      super(name, 0);
   }

   /**
    * Create a new main-group with the given name and identifier.
    */
   public MainGroup(String name, int id)
   {
      super(name, id);
   }

   /**
    * Assign id's to all middle-groups.
    */
   public void renumber()
   {
      for (int i=children.size()-1; i>=0; --i)
      {
         final MiddleGroup e = children.get(i);
         e.setId(i + 1);
         e.renumber(getId());
      }
   }
   
   /**
    * Create a new {@link MiddleGroup} with the given name.
    * The middleGroup is appended to the end of the list of children.
    */
   public MiddleGroup createMiddleGroup(String name)
   {
      final MiddleGroup middleGroup = new MiddleGroup(name);
      add(middleGroup);
      return middleGroup;
   }
}
