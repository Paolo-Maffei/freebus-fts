package org.freebus.fts.backend.memory;

/**
 * Types of memory values.
 */
public enum MemoryValueType
{
   /**
    * The value is unassigned / unset.
    */
   UNSET,

   /**
    * The value is the initial value.
    */
   INITIAL,

   /**
    * The value was modified.
    */
   MODIFIED;
}
