package org.freebus.fts.products.dao;

/**
 * Base class for DAO exceptions.
 */
public class DAOException extends Exception
{
   private static final long serialVersionUID = 1998918849929753204L;

   public DAOException()
   {
      super();
   }

   public DAOException(String message)
   {
      super(message);
   }

   public DAOException(Exception e)
   {
      super(e);
   }

   public DAOException(String message, Exception e)
   {
      super(message, e);
   }
}
