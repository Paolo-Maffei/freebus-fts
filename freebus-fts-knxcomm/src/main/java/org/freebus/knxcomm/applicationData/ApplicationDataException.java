package org.freebus.knxcomm.applicationData;

public class ApplicationDataException extends Exception
{

   private static final long serialVersionUID = -4418866152143176572L;
   String ErrorMessage;

   /**
    * Create a new ApplicationDataException Object
    *
    * @param msg - the Error Message
    */
   ApplicationDataException(String msg)
   {
      super();
      this.ErrorMessage = msg;

   }

   /**
    * @return the Error Message
    */
   public String getErrorMessage()
   {
      return ErrorMessage;
   }
}
