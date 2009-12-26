package org.freebus.fts.products.services;

/**
 * The object was not found.
 */
public class DAONotFoundException extends DAOException
{
   private static final long serialVersionUID = -2467781232702845655L;

   public DAONotFoundException()
   {
      super();
   }

   public DAONotFoundException(String message)
   {
      super(message);
   }

   public DAONotFoundException(Exception e)
   {
      super(e);
   }

   public DAONotFoundException(String message, Exception e)
   {
      super(message, e);
   }
}
