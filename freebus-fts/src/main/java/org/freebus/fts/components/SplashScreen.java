package org.freebus.fts.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.freebus.fts.core.I18n;
import org.freebus.fts.core.ImageCache;

/**
 * Application loading (splash) screen.
 */
public class SplashScreen extends JFrame
{
   private static final long serialVersionUID = 1797628425145863442L;

   final JProgressBar progress = new JProgressBar(SwingConstants.HORIZONTAL);
   final JLabel lblStep = new JLabel(".Mg.");

   /**
    * Create a splash screen.
    */
   public SplashScreen()
   {
      super(I18n.getMessage("FTS.SplashTitle"));

      final Icon backIcon = ImageCache.getIcon("gui-images/splash");
      final int w = backIcon.getIconWidth();
      final int h = backIcon.getIconHeight();

      setSize(w, h);
      setUndecorated(true);
      center();

      final JLayeredPane layers = new JLayeredPane();
      layers.setOpaque(false);
      layers.setBorder(BorderFactory.createLineBorder(Color.blue, 1));
      add(layers);

      final JLabel back = new JLabel(backIcon);
      back.setLocation(0, 0);
      back.setSize(getSize());
      layers.add(back, JLayeredPane.DEFAULT_LAYER);

      progress.setBorder(BorderFactory.createEmptyBorder());
      progress.setSize(new Dimension(w - 60, 15));
      progress.setLocation(30, getHeight() - progress.getHeight() - 25);
      progress.setMinimum(0);
      progress.setMaximum(100);
      layers.add(progress, JLayeredPane.PALETTE_LAYER);

      lblStep.setOpaque(false);
      lblStep.setForeground(Color.white);
      lblStep.setFont(lblStep.getFont().deriveFont(Font.PLAIN));
      lblStep.setSize(new Dimension(progress.getWidth(), lblStep.getPreferredSize().height + 6));
      lblStep.setLocation(30, progress.getY() - lblStep.getHeight() - 7);
      lblStep.setText("");
      layers.add(lblStep, JLayeredPane.PALETTE_LAYER);

      addMouseListener(new MouseAdapter()
      {
         @Override
         public void mousePressed(MouseEvent e)
         {
            setVisible(false);
         }
      });

      toFront();
   }

   /**
    * Center the frame on the screen.
    */
   public void center()
   {
      final Toolkit tk = Toolkit.getDefaultToolkit();
      final Dimension screenSize = tk.getScreenSize();
      setLocation((screenSize.width - getWidth()) >> 1, (screenSize.height - getHeight()) >> 1);
   }

   /**
    * Set the progress. Does not change the progress message.
    *
    * @param value - the progress, in percent.
    */
   public void setProgress(final int value)
   {
      setProgress(value, null);
   }

   /**
    * Set the progress.
    *
    * @param value - the progress, in percent.
    * @param text - an optional text. If null, the previously set text remains.
    */
   public void setProgress(final int value, final String text)
   {
      if (SwingUtilities.isEventDispatchThread())
      {
         Logger.getLogger(SplashScreen.class).debug(text);
         progress.setValue(value);

         if (text != null)
            lblStep.setText(text);
      }
      else
      {
         try
         {
            SwingUtilities.invokeAndWait(new Runnable()
            {
               @Override
               public void run()
               {
                  setProgress(value, text);
               }
            });
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
   }
}
