package org.freebus.fts.pages.productsbrowser;

import javax.swing.tree.DefaultMutableTreeNode;

import org.freebus.fts.products.FunctionalEntity;

public class FunctionalEntityTreeNode extends DefaultMutableTreeNode
{
   private static final long serialVersionUID = -2747918792682149868L;

   /**
    * Creates a tree node that has no parent and no children, but which allows
    * children.
    */
   public FunctionalEntityTreeNode()
   {
      super();
   }

   /**
    * Creates a tree node with no parent, no children, but which allows
    * children, and initializes it with the specified user object.
    * 
    * @param userObject an Object provided by the user that constitutes the
    *           node's data
    */
   public FunctionalEntityTreeNode(Object userObject)
   {
      super(userObject);
   }

   /**
    * Creates a tree node with no parent, no children, initialized with the
    * specified user object, and that allows children only if specified.
    * 
    * @param userObject an Object provided by the user that constitutes the
    *           node's data
    * @param allowsChildren if true, the node is allowed to have child nodes --
    *           otherwise, it is always a leaf node
    */
   public FunctionalEntityTreeNode(Object userObject, boolean allowsChildren)
   {
      super(userObject, allowsChildren);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      if (userObject instanceof FunctionalEntity)
      {
         final FunctionalEntity funcEntity = (FunctionalEntity) userObject;
         return funcEntity.getName();
      }

      return super.toString();
   }

}
