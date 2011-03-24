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
   private static final Map<Integer, Level> LEVEL_MAP = new HashMap<Integer, Level>();
   private Logger lastLogger;

   /*
    * Initialize the log level mappings.
    */
   static
   {
      LEVEL_MAP.put(SessionLog.OFF, Level.OFF);
      LEVEL_MAP.put(SessionLog.SEVERE, Level.ERROR);
      LEVEL_MAP.put(SessionLog.WARNING, Level.WARN);
      LEVEL_MAP.put(SessionLog.INFO, Level.INFO);
      LEVEL_MAP.put(SessionLog.CONFIG, Level.INFO);
      LEVEL_MAP.put(SessionLog.FINE, Level.DEBUG);
      LEVEL_MAP.put(SessionLog.FINER, Level.TRACE);
      LEVEL_MAP.put(SessionLog.FINEST, Level.TRACE);
      LEVEL_MAP.put(SessionLog.ALL, Level.ALL);
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
    * Get a logger for a session log entry.
    * 
    * @param entry - the session log entry
    * 
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
    * Map the log-level to Log4j log levels.
    * 
    * @param sessionLogLevel - the session log level.
    * @return The Log4j log level.
    */
   private Level mapLogLevel(int sessionLogLevel)
   {
      Level level = LEVEL_MAP.get(sessionLogLevel);
      if (level == null)
         level = Level.DEBUG;
      return level;
   }
}
