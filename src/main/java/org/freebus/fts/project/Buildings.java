package org.freebus.fts.project;

import java.util.Vector;

/**
 * All buildings of a {@link Project}.
 */
public class Buildings
{
   private final Vector<Building> buildings = new Vector<Building>(); 
   
   /**
    * @return the number of buildings.
    */
   public int size()
   {
      return buildings.size();
   }
   
   /**
    * @return the idx-th building.
    */
   public Building get(int idx)
   {
      return buildings.elementAt(idx);
   }

   /**
    * Add a building.
    */
   public void add(Building building)
   {
      buildings.add(building);
   }

   /**
    * Remove a building.
    */
   public void remove(Building building)
   {
      buildings.remove(building);
   }

   /**
    * Remove the idx-th building.
    */
   public void remove(int idx)
   {
      buildings.remove(idx);
   }

   /**
    * Removes all buildings.
    */
   public void removeAll()
   {
      buildings.removeAllElements();
   }

   /**
    * Create a new {@link Building} with the given name.
    * The building is appended to the end of the list of buildings.
    */
   public Building createBuilding(String name)
   {
      final Building building = new Building(name);
      buildings.add(building);
      return building;
   }
}
