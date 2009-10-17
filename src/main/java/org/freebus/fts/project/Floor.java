package org.freebus.fts.project;

import java.util.Vector;

/**
 * A floor in a {@link Building}.
 */
public class Floor
{
   private String name;
   private final Vector<Room> rooms = new Vector<Room>(); 

   /**
    * Create a new floor with the given name.
    */
   public Floor(String name)
   {
      this.name = name;
   }
   
   /**
    * Set the name of the floor.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the name of the floor.
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * @return the number of rooms on the floor.
    */
   public int size()
   {
      return rooms.size();
   }
   
   /**
    * @return the idx-th room.
    */
   public Room get(int idx)
   {
      return rooms.elementAt(idx);
   }

   /**
    * Add a room.
    */
   public void add(Room room)
   {
      rooms.add(room);
   }

   /**
    * Remove a room.
    */
   public void remove(Room room)
   {
      rooms.remove(room);
   }

   /**
    * Remove the idx-th room.
    */
   public void remove(int idx)
   {
      rooms.remove(idx);
   }

   /**
    * Remove all rooms.
    */
   public void removeAll()
   {
      rooms.removeAllElements();
   }

   /**
    * Create a new {@link Room} with the given name.
    * The room is appended to the end of the room's list of rooms.
    */
   public Room createRoom(String name)
   {
      final Room room = new Room(name);
      rooms.add(room);
      return room;
   }
}
