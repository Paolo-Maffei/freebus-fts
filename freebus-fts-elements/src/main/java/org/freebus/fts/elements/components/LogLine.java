package org.freebus.fts.elements.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.freebus.fts.elements.internal.I18n;
import org.freebus.fts.elements.services.ImageCache;

/**
 * A component that displays the first line of the latest log event and has a
 * history that can be shown by pressing the history button.
 * <p>
 * The class contains a log appender that is automatically registered in Log4j
 * to capture the log events.
 */
public class LogLine extends JPanel
{
   private static final long serialVersionUID = 3863672762089494126L;
   private static final int MAX_HISTORY_EVENTS = 30;
   private static final int MESSAGE_TIMEOUT_MS = 5000;
   private static final Map<Level, Color> LEVEL_COLORS = new HashMap<Level, Color>();

   private final LinkedList<LogEvent> logEventsHistory = new LinkedList<LogEvent>();
   private final Color defaultColor = getForeground();
   private final JLabel lblLine = new JLabel();
   private final JButton btnHistory;
   private JPopupMenu pmnHistory;
   private Timer timer;

   /**
    * Static initializer for the log-level colors.
    */
   static
   {
      LEVEL_COLORS.put(Level.INFO, new Color(16, 16, 254));
      LEVEL_COLORS.put(Level.WARN, new Color(254, 128, 0));
      LEVEL_COLORS.put(Level.ERROR, new Color(254, 48, 16));
   }

   /**
    * Create a log-line component.
    */
   public LogLine()
   {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      setOpaque(false);

      btnHistory = new JButton(ImageCache.getIcon("icons/up_arrow"));
      btnHistory.setToolTipText(org.freebus.fts.elements.internal.I18n.getMessage("LogLine.HistoryTip"));
      btnHistory.setBorderPainted(false);
      btnHistory.setFocusable(false);
      add(btnHistory);

      add(Box.createHorizontalStrut(4));

      add(lblLine);
      lblLine.setText(" ");

      btnHistory.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            showLogHistory();
         }
      });

      appender.setLayout(new SimpleLayout());
      Logger.getRootLogger().addAppender(appender);
   }

   /**
    * Show the log-event history as a popup.
    */
   private void showLogHistory()
   {
      pmnHistory = new JPopupMenu();
      pmnHistory.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

      for (final LogEvent ev : logEventsHistory)
      {
         final JMenuItem item = new JMenuItem(ev.message);
         item.setForeground(getLevelColor(ev.level));
         pmnHistory.add(item);
      }

      pmnHistory.pack();
      pmnHistory.show(btnHistory, 0, 0);
      updateLogHistoryPos();
   }

   /**
    * Update the position of the history popup.
    */
   private void updateLogHistoryPos()
   {
      final Point pos = btnHistory.getLocationOnScreen();
      pmnHistory.setLocation(pos.x + btnHistory.getWidth() + 2,
                             pos.y + btnHistory.getHeight() - pmnHistory.getHeight() - 2);
   }

   /**
    * Get the foreground color for a message of the given log level.
    * 
    * @param level - the log level to get the color for.
    * @return The color for the log level.
    */
   private Color getLevelColor(final Level level)
   {
      final Color c = LEVEL_COLORS.get(level);
      return c == null ? defaultColor : c;
   }

   /**
    * Append a log event. Automatically called by the internal log appender when
    * a new log event arrives.
    * 
    * @param level - the log level of the message.
    * @param message - the message to append.
    */
   public void append(Level level, String message)
   {
      lblLine.setText(message);
      setForeground(getLevelColor(level));

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
      }, MESSAGE_TIMEOUT_MS);

      logEventsHistory.add(new LogEvent(level, message));

      final boolean historyVisible = pmnHistory != null && pmnHistory.isVisible();
      if (historyVisible)
      {
         final JMenuItem item = new JMenuItem(message);
         item.setForeground(getLevelColor(level));
         pmnHistory.add(item);
      }

      for (int numHistoryEvents = logEventsHistory.size(); numHistoryEvents > MAX_HISTORY_EVENTS; --numHistoryEvents)
      {
         logEventsHistory.pop();
         if (historyVisible)
            pmnHistory.remove(0);
      }

      if (historyVisible)
      {
         pmnHistory.validate();
         pmnHistory.pack();
         updateLogHistoryPos();
      }
   }

   /**
    * Clear the log line.
    */
   public void clear()
   {
      lblLine.setText(" ");

      if (timer != null)
      {
         timer.cancel();
         timer.purge();
         timer = null;
      }
   }

   /**
    * The internal log-appender of {@link LogLine} that captures the log events.
    */
   protected final transient Appender appender = new WriterAppender()
   {
      @Override
      public void append(final LoggingEvent event)
      {
         final Level level = event.getLevel();
         if (!level.isGreaterOrEqual(Level.INFO))
            return;

         final String message = layout.format(event);

         SwingUtilities.invokeLater(new Runnable()
         {
            @Override
            public void run()
            {
               LogLine.this.append(level, message);

               if (level.isGreaterOrEqual(Level.FATAL))
               {
                  final StringBuffer sb = new StringBuffer();

                  sb.append("<html><body width=\"500px\"><h2>").append(I18n.getMessage("LogLine.ErrorCaption"));
                  sb.append("</h2>").append(message.replace("\n", "<br />"));
                  sb.append("</body></html>");

                  Dialogs.showErrorDialog(sb.toString());
               }
            }
         });
      }
   };

   /**
    * Internal class of {@link LogLine}, holding a log event.
    */
   static private class LogEvent
   {
      final public Level level;
      final public String message;

      /**
       * Create a log event. Internal class of {@link LogLine}.
       *
       * @param level - the log level.
       * @param message - the log message.
       */
      public LogEvent(Level level, String message)
      {
         this.level = level;
         this.message = message;
      }
   }
}
