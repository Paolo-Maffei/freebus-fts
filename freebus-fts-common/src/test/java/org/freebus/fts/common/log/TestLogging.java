package org.freebus.fts.common.log;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestLogging
{
   @Test
   public final void testSetup()
   {
      Logging.setup();
      Logger.getLogger(getClass()).debug("a test message");

      Logging.setup();
      Logger.getLogger(getClass()).debug("another test message");
   }

   @Test(expected = IllegalAccessException.class)
   public final void testLogging() throws Exception
   {
      Logging.class.newInstance();
   }
}
