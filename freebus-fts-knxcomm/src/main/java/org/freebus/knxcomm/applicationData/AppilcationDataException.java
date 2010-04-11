package org.freebus.knxcomm.applicationData;

public class AppilcationDataException extends Exception
{

   private static final long serialVersionUID = -4418866152143176572L;
   String ErrorMessage;

   /**
    * Create a new AppilcationDataException Object
    * 
    * @param msg - the Error Message
    */
   AppilcationDataException(String msg)
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
