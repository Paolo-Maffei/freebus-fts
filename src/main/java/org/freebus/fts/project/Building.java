package org.freebus.fts.project;

import java.util.Vector;

/**
 * A building in a {@link Project}.
 */
public class Building
{
   private String name;
   private final Vector<Floor> floors = new Vector<Floor>(); 

   /**
    * Create a new building with the given name.
    */
   public Building(String name)
   {
      this.name = name;
   }
   
   /**
    * Set the name of the building.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the name of the building.
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * @return the number of floors.
    */
   public int size()
   {
      return floors.size();
   }
   
   /**
    * @return the idx-th floor.
    */
   public Floor get(int idx)
   {
      return floors.elementAt(idx);
   }

   /**
    * Add a floor.
    */
   public void add(Floor floor)
   {
      floors.add(floor);
   }

   /**
    * Remove a floor.
    */
   public void remove(Floor floor)
   {
      floors.remove(floor);
   }

   /**
    * Remove the idx-th floor.
    */
   public void remove(int idx)
   {
      floors.remove(idx);
   }

   /**
    * Removes all floors.
    */
   public void removeAll()
   {
      floors.removeAllElements();
   }

   /**
    * Create a new {@link Floor} with the given name.
    * The floor is appended to the end of the list of floors.
    */
   public Floor createFloor(String name)
   {
      final Floor floor = new Floor(name);
      floors.add(floor);
      return floor;
   }
}
