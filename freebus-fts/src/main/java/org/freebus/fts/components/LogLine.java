package org.freebus.fts.components;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A widget that displays the first line of the latest log event.
 */
public class LogLine extends JLabel
{
   private static final long serialVersionUID = 3863672762089494126L;
   private static final int messageTimeoutMS = 5000; 

   private final Map<Level,Color> levelColors = new HashMap<Level,Color>();
   private final Color defaultColor = getForeground();
   private Timer timer;

   /**
    * Create a log-line widget.
    */
   public LogLine()
   {
      levelColors.put(Level.INFO, new Color(16, 16, 254));
      levelColors.put(Level.WARN, new Color(254, 128, 0));
      levelColors.put(Level.ERROR, new Color(254, 48, 16));

      setText(" ");

      appender.setLayout(new SimpleLayout());
      Logger.getRootLogger().addAppender(appender);
   }

   /**
    * Append a log event. Automatically called by the internal
    * log appender when a new log event arrives.
    */
   public void append(Level level, String message)
   {
      setText(message);

      final Color c = levelColors.get(level);
      if (c == null) setForeground(defaultColor);
      else setForeground(c);

      if (timer != null)
      {
         timer.cancel();
         timer.purge();
      }

      timer = new Timer();
      timer.schedule(new TimerTask()
      {
         @Override
         public void run()
         {
            SwingUtilities.invokeLater(new Runnable()
            {
               @Override
               public void run()
               {
                  clear();
               }
            });
         }
      }, messageTimeoutMS);
   }

   /**
    * Clear the log line.
    */
   public void clear()
   {
      setText(" ");

      if (timer != null)
      {
         timer.cancel();
         timer.purge();
         timer = null;
      }
   }

   /*
    * The internal log-appender that feeds the log line. 
    */
   protected final transient Appender appender = new WriterAppender()
   {
      @Override
      public void append(final LoggingEvent event)
      {
         final String message = layout.format(event);
         final Level level = event.getLevel();

         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               LogLine.this.append(level, message);
            }
         });
      }
   };
}
