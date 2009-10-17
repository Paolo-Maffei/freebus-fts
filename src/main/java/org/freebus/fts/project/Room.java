package org.freebus.fts.project;

/**
 * A room on a {@link Floor}.
 */
public class Room
{
   private String name;

   /**
    * Create a new room with the given name.
    */
   public Room(String name)
   {
      this.name = name;
   }
   
   /**
    * Set the name of the room.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the name of the room.
    */
   public String getName()
   {
      return name;
   }

}
