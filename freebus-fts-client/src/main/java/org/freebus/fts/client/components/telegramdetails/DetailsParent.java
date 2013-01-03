package org.freebus.fts.client.components.telegramdetails;

/**
 * The parent of {@link DetailsPart details parts}.
 */
public interface DetailsParent
{
   /**
    * The part was changed by the user.
    *
    * @param part - the changed part.
    */
   public void partChanged(DetailsPart part);
}
