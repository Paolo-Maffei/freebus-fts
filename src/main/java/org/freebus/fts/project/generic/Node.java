package org.freebus.fts.project.generic;

import java.util.Vector;

/**
 * A generic, named node that can have children.
 */
public class Node<E> extends Leaf 
{
   protected final Vector<E> children = new Vector<E>(); 

   /**
    * Create a new object with the given name and identifier.
    */
   public Node(String name, int id)
   {
      super(name, id);
   }

   /**
    * @return the number of children.
    */
   public int size()
   {
      return children.size();
   }
   
   /**
    * @return the idx-th child.
    */
   public E get(int idx)
   {
      return children.elementAt(idx);
   }

   /**
    * Add a child.
    */
   public void add(E child)
   {
      children.add(child);
   }

   /**
    * Remove a child.
    */
   public void remove(E child)
   {
      children.remove(child);
   }

   /**
    * Remove the idx-th child.
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
}
