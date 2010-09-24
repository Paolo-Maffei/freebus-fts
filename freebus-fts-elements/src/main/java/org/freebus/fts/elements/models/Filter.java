package org.freebus.fts.elements.models;

/**
 * Interface for filters that can test objects.
 *
 * @see FilteredListModel
 */
public interface Filter
{
   /**
    * Test if the filter matches the object.
    *
    * @param o - the object to match.
    *
    * @return true if the filter matches the object.
    */
   public boolean matches(final Object o);
}
