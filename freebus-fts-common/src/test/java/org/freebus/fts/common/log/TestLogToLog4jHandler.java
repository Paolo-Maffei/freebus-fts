package org.freebus.fts.common.log;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;


public class TestLogToLog4jHandler
{
   static
   {
      // Setup log4j
      BasicConfigurator.configure();
   }
   
   private static class ExceptionThrower
   {
      @Override
      public String toString()
      {
         throw new RuntimeException("a test exception");
      }
   }

   @Test
   public final void testTranslateLevel()
   {
      final LogToLog4jHandler handler = new LogToLog4jHandler();

      assertEquals(org.apache.log4j.Level.OFF, handler.translateLevel(java.util.logging.Level.OFF));
      assertEquals(org.apache.log4j.Level.INFO, handler.translateLevel(java.util.logging.Level.INFO));
      assertEquals(org.apache.log4j.Level.DEBUG, handler.translateLevel(java.util.logging.Level.FINE));
      assertEquals(org.apache.log4j.Level.DEBUG, handler.translateLevel(java.util.logging.Level.FINER));
      assertEquals(org.apache.log4j.Level.DEBUG, handler.translateLevel(java.util.logging.Level.FINEST));
      assertEquals(org.apache.log4j.Level.WARN, handler.translateLevel(java.util.logging.Level.WARNING));
      assertEquals(org.apache.log4j.Level.ERROR, handler.translateLevel(java.util.logging.Level.SEVERE));
      assertEquals(org.apache.log4j.Level.DEBUG, handler.translateLevel(java.util.logging.Level.CONFIG));
   }

   @Test
   public final void testFormatLogMessage()
   {
      final LogToLog4jHandler handler = new LogToLog4jHandler();

      assertEquals("", handler.formatLogMessage(new LogRecord(Level.FINEST, "")));
      assertEquals("test-1", handler.formatLogMessage(new LogRecord(Level.FINEST, "test-1")));
      assertEquals("test {0} test", handler.formatLogMessage(new LogRecord(Level.FINEST, "test {0} test")));
   }

   @Test
   public final void testFormatLogMessageParams()
   {
      final LogToLog4jHandler handler = new LogToLog4jHandler();

      final LogRecord rec1 = new LogRecord(Level.FINE, "arg0={0} arg1={1}");
      rec1.setParameters(new Object[] { "<0>", "<1>" });
      assertEquals("arg0=<0> arg1=<1>", handler.formatLogMessage(rec1));

      final LogRecord rec2 = new LogRecord(Level.FINE, "arg3={3}");
      rec2.setParameters(new Object[] { "<0>", "<1>", "<2>", "<3>" });
      assertEquals("arg3=<3>", handler.formatLogMessage(rec2));

      final LogRecord rec3 = new LogRecord(Level.FINE, "arg0={0}");
      rec3.setParameters(new Object[] { new ExceptionThrower() });
      assertEquals("arg0={0}", handler.formatLogMessage(rec3));
   }

   @Test
   public final void testPublish()
   {
      final LogToLog4jHandler handler = new LogToLog4jHandler();

      final LogRecord rec1 = new LogRecord(Level.OFF, "this message should not appear");
      rec1.setLoggerName("TestLogToLog4jHandler");
      handler.publish(rec1);

      final LogRecord rec2 = new LogRecord(Level.FINEST, "test message");
      rec2.setLoggerName("TestLogToLog4jHandler");
      handler.publish(rec2);

      // Call empty methods. Just to be sure that they do not fail
      handler.flush();
      handler.close();
   }
}
