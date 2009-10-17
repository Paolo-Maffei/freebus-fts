package org.freebus.fts.project.generic;

/**
 * A generic, named leaf of a node-tree.
 */
public class Leaf
{
   private String name;
   private int id;

   /**
    * Create a new object with the given name and identifier.
    */
   public Leaf(String name, int id)
   {
      this.name = name;
      this.id = id;
   }

   /**
    * @return the name of the object.
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * Set the name of the object.
    */
   public void setName(String name)
   {
      this.name = name;
   }
   
   /**
    * @return the id of the object.
    */
   public int getId()
   {
      return id;
   }
   
   /**
    * Set the id of the object.
    */
   public void setId(int id)
   {
      this.id = id;
   }
}
