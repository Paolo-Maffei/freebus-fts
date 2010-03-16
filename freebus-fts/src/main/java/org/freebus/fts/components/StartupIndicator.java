package org.freebus.fts.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Graphical startup indicator that uses the splash screen that was given when
 * Java was started. Nothing is shown if no splash screen was set.
 * <p>
 * To get a splash screen either use <code>-splash=mysplash.png</code> or define
 * the splash screen image in the JAR's manifest.
 */
public class StartupIndicator
{
   private final java.awt.SplashScreen splash;
   private Graphics2D graphics;
   private Rectangle progressBounds, textBounds;
   private final Color progressColor = new Color(0x6E88A2);
   private final Color progressLineColor = progressColor.darker();
   private final Color textColor = Color.white;
   private Dimension size;
   private int progress = 0;
   private String text;

   /**
    * Create a splash screen.
    */
   public StartupIndicator()
   {
      splash = java.awt.SplashScreen.getSplashScreen();
      if (splash == null)
         return;

      graphics = splash.createGraphics();
      size = splash.getSize();

      progressBounds = new Rectangle(20, size.height - 40, size.width - 40, 15);
      textBounds = new Rectangle(progressBounds.x, progressBounds.y - 20, progressBounds.width, 20);
   }

   /**
    * Close the splash screen.
    */
   public void close()
   {
      if (splash != null && splash.isVisible())
         splash.close();
   }

   /**
    * Set the progress. Does not change the progress message.
    *
    * @param progress - the progress, in percent.
    */
   public void setProgress(final int progress)
   {
      this.progress = progress;
      redraw();
   }

   /**
    * Set the progress.
    *
    * @param progress - the progress, in percent.
    * @param text - an optional text. If null, the previously set text remains.
    */
   public void setProgress(final int progress, final String text)
   {
      this.progress = progress;
      this.text = text;
      redraw();
   }

   /**
    * Render the splash screen.
    */
   protected synchronized void redraw()
   {
      if (splash == null)
         return;

      graphics.setComposite(AlphaComposite.Clear);
      graphics.fillRect(0, 0, size.width, size.height);

      graphics.setPaintMode();
      graphics.setColor(progressLineColor);
      graphics.drawLine(progressBounds.x, progressBounds.y + progressBounds.height, progressBounds.x + progressBounds.width - 1, progressBounds.y + progressBounds.height);
      graphics.setColor(progressColor);
      graphics.fillRect(progressBounds.x, progressBounds.y, progressBounds.width * progress / 100, progressBounds.height + 1);

      graphics.setColor(textColor);
      if (text != null && !text.isEmpty())
         graphics.drawString(text, textBounds.x, textBounds.y);

      splash.update();
   }
}
