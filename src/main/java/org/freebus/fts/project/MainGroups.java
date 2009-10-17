package org.freebus.fts.project;

import java.util.Vector;

/**
 * All main-groups of a {@link Project}.
 */
public class MainGroups
{
   private final Vector<MainGroup> mainGroups = new Vector<MainGroup>(); 
   
   /**
    * @return the number of mainGroups.
    */
   public int size()
   {
      return mainGroups.size();
   }
   
   /**
    * @return the idx-th mainGroup.
    */
   public MainGroup get(int idx)
   {
      return mainGroups.elementAt(idx);
   }

   /**
    * Add a mainGroup.
    */
   public void add(MainGroup mainGroup)
   {
      mainGroups.add(mainGroup);
   }

   /**
    * Remove a mainGroup.
    */
   public void remove(MainGroup mainGroup)
   {
      mainGroups.remove(mainGroup);
   }

   /**
    * Remove the idx-th mainGroup.
    */
   public void remove(int idx)
   {
      mainGroups.remove(idx);
   }

   /**
    * Removes all mainGroups.
    */
   public void removeAll()
   {
      mainGroups.removeAllElements();
   }

   /**
    * Assign id's to all main-groups.
    */
   public void renumber()
   {
      for (int i=mainGroups.size()-1; i>=0; --i)
      {
         final MainGroup e = mainGroups.get(i);
         e.setId(i + 1);
         e.renumber();
      }
   }

   /**
    * Create a new {@link MainGroup} with the given name.
    * The mainGroup is appended to the end of the list of mainGroups.
    */
   public MainGroup createMainGroup(String name)
   {
      final MainGroup mainGroup = new MainGroup(name);
      mainGroups.add(mainGroup);
      return mainGroup;
   }
}
