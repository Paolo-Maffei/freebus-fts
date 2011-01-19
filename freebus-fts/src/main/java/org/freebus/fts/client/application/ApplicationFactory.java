package org.freebus.fts.client.application;


public class ApplicationFactory
{
   static public String[] args;

   public Application createApplication()
   {
      Application.launch(Application.class, args);

      Application app = null;
      for (int wait = 10; wait > 0 && app == null; --wait)
      {
         try
         {
            Thread.sleep(100);
         }
         catch (InterruptedException e)
         {
            throw new RuntimeException(e);
         }

         app = Application.getInstance();
      }

      return app;
   }
}
