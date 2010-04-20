package org.freebus.fts.components.telegramdetails;

import javax.swing.JPanel;

/**
 * An abstract base implementation of a details part.
 */
public abstract class AbstractDetailsPart extends JPanel implements DetailsPart
{
   private static final long serialVersionUID = 3125178812213451030L;

   private final DetailsParent parent;

   /**
    * Create a details part.
    *
    * @param parent - the parent that owns the details part.
    */
   protected AbstractDetailsPart(DetailsParent parent)
   {
      this.parent = parent;
   }

   /**
    * Notify the parent that the part's contents has changed.
    */
   public void notifyChanged()
   {
      parent.partChanged(this);
   }
}
