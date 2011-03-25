package org.freebus.fts.persistence.exception;

/**
 * Base class for DAO exceptions.
 */
public class DAOException extends Exception
{
   private static final long serialVersionUID = 1998918849929753204L;

   /**
    * Create an empty exception.
    */
   public DAOException()
   {
      super();
   }

   /**
    * Create an exception.
    * 
    * @param message - the message.
    */
   public DAOException(String message)
   {
      super(message);
   }

   /**
    * Create an exception.
    * 
    * @param cause - the cause for the exception.
    */
   public DAOException(Throwable cause)
   {
      super(cause);
   }

   /**
    * Create an exception.
    * 
    * @param message - the message.
    * @param cause - the cause for the exception.
    */
   public DAOException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
