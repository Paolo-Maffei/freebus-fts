package org.freebus.fts.persistence.exception;

/**
 * The object was not found.
 */
class DAONotFoundException extends DAOException
{
   private static final long serialVersionUID = -2467781232702845655L;

   /**
    * Create an empty exception.
    */
   public DAONotFoundException()
   {
      super();
   }

   /**
    * Create an exception.
    * 
    * @param message - the message.
    */
   public DAONotFoundException(String message)
   {
      super(message);
   }

   /**
    * Create an exception.
    * 
    * @param cause - the cause for the exception.
    */
   public DAONotFoundException(Throwable cause)
   {
      super(cause);
   }

   /**
    * Create an exception.
    * 
    * @param message - the message.
    * @param cause - the cause for the exception.
    */
   public DAONotFoundException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
