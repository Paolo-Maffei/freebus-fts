package org.freebus.fts.persistence.db;

/**
 * The class of a {@link DriverType}.
 */
public enum DriverClass
{
   /** No driver. Used to mark internal- and unit-test drivers. */
   NONE,

   /** A file-based database. */
   FILE_BASED,

   /** A database that has a standalone server. */
   SERVER_BASED;
}
