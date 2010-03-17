package org.freebus.fts.persistence.db;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

/**
 * A session-log handler that redirects the JPA log output to Log4J.
 */
public final class CommonsLoggingSessionLog extends AbstractSessionLog
{
   private static final Map<Integer, Level> levelMap = new HashMap<Integer, Level>();
   private Logger lastLogger;

   /*
    * Initialize the log level mappings.
    */
   static
   {
      levelMap.put(SessionLog.OFF, Level.OFF);
      levelMap.put(SessionLog.SEVERE, Level.ERROR);
      levelMap.put(SessionLog.WARNING, Level.WARN);
      levelMap.put(SessionLog.INFO, Level.INFO);
      levelMap.put(SessionLog.CONFIG, Level.INFO);
      levelMap.put(SessionLog.FINE, Level.DEBUG);
      levelMap.put(SessionLog.FINER, Level.TRACE);
      levelMap.put(SessionLog.FINEST, Level.TRACE);
      levelMap.put(SessionLog.ALL, Level.ALL);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void log(SessionLogEntry entry)
   {
      final Logger logger = getLogger(entry);

      final Level level = mapLogLevel(entry.getLevel());
      if (!logger.isEnabledFor(level))
         return;

      final String message = formatMessage(entry);
      if (!message.isEmpty())
         logger.log(level, message);
   }

   /**
    * @return a suitable logger
    */
   private Logger getLogger(SessionLogEntry entry)
   {
      String nameSpace = entry.getNameSpace();
      if (nameSpace == null)
         nameSpace = "default";
      final String name = "org.eclipselink." + nameSpace;

      Logger logger = lastLogger;
      if (logger == null || !logger.getName().equals(name))
      {
         logger = Logger.getLogger(name);
         lastLogger = logger;
      }

      return logger;
   }

   /**
    * Map the log-level to log4j log levels.
    */
   private Level mapLogLevel(int sessionLogLevel)
   {
      Level level = levelMap.get(sessionLogLevel);
      if (level == null)
         level = Level.DEBUG;
      return level;
   }
}
