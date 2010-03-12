package org.freebus.fts.common.log;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A java.util.logging handler that redirects java.util.logging messages to
 * Log4J.
 * 
 * Adapted from http://wiki.apache.org/myfaces/Trinidad_and_Common_Logging
 */
public class LogToLog4jHandler extends Handler
{
   /**
    * {@inheritDoc}
    */
   @Override
   public void publish(LogRecord record)
   {
      final Logger log4j = Logger.getLogger(record.getLoggerName());
      final Level priority = translateLevel(record.getLevel());

      if (priority.isGreaterOrEqual(Level.DEBUG))
         log4j.log(priority, formatLogMessage(record), record.getThrown());
   }

   /**
    * Create a log4j log message from a java.util.logging log record
    * 
    * @param record - the log record to process
    * @return a log4j log message
    */
   public String formatLogMessage(LogRecord record)
   {
      String message = record.getMessage();
      try
      {
         Object parameters[] = record.getParameters();
         if (parameters != null && parameters.length != 0)
         {
            // Check for the first few parameters
            if (message.indexOf("{0}") >= 0 || message.indexOf("{1}") >= 0 || message.indexOf("{2}") >= 0
                  || message.indexOf("{3}") >= 0)
            {
               message = MessageFormat.format(message, parameters);
            }
         }
      }
      catch (Exception ex)
      {
      }
      return message;
   }

   /**
    * Convert a j.u.l log level to a log4j log level.
    * 
    * @param level - the java.util.logging log level to convert
    * @return the corresponding log4j log level
    */
   public Level translateLevel(java.util.logging.Level level)
   {
      if (java.util.logging.Level.SEVERE == level)
         return org.apache.log4j.Level.ERROR;
      else if (java.util.logging.Level.WARNING == level)
         return org.apache.log4j.Level.WARN;
      else if (java.util.logging.Level.INFO == level)
         return org.apache.log4j.Level.INFO;
      else if (java.util.logging.Level.OFF == level)
         return org.apache.log4j.Level.OFF;
      return org.apache.log4j.Level.DEBUG;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void flush()
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
   }
}
