package org.freebus.fts.elements;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Graphical startup indicator that uses the splash screen that was given when
 * Java was started. Nothing is shown if the splash screen is not set.
 * <p>
 * To get a splash screen either use the java commandline argument
 * <code>-splash:path/to/mysplash.png</code>, or define the splash screen image
 * in the JAR's manifest.
 */
public class StartupIndicator
{
   private final java.awt.SplashScreen splash;
   private Graphics2D graphics;
   private Rectangle progressBounds;
   private Point textPos, versionTextPos;
   private final Color progressColor = new Color(0x6E88A2);
   private final Color progressLineColor = progressColor.darker();
   private final Color textColor = Color.white;
   private final Color versionTextColor = Color.white;
   private Font textFont, versionTextFont;
   private Dimension size;
   private int progress = 0;
   private String text, versionText;

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

      progressBounds = new Rectangle(28, size.height - 40, size.width - 56, 15);
      textPos = new Point(progressBounds.x, progressBounds.y - 20);

      textFont = graphics.getFont();
      versionTextFont = textFont.deriveFont(textFont.getSize2D() * 0.8f);
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
    * Set the position of the progress text.
    *
    * @param x - the x coordinate
    * @param y - the y coordinate
    */
   public void setTextPos(int x, int y)
   {
      textPos = new Point(x, y);
   }

   /**
    * @return The position of the progress text.
    */
   public Point getTextPos()
   {
      return textPos;
   }

   /**
    * Set the optional version text and it's position.
    * 
    * @param x - the x coordinate
    * @param y - the y coordinate
    * @param text - the text that is displayed
    */
   public void setVersionText(int x, int y, String text)
   {
      versionTextPos = new Point(x, y);
      versionText = text;
   }

   /**
    * @return The position of the version text.
    */
   public Point getVersionTextPos()
   {
      return versionTextPos;
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
      if (splash == null || !splash.isVisible())
         return;

      graphics.setComposite(AlphaComposite.Clear);
      graphics.fillRect(0, 0, size.width, size.height);

      graphics.setPaintMode();
      graphics.setColor(progressLineColor);
      graphics.drawLine(progressBounds.x, progressBounds.y + progressBounds.height, progressBounds.x
            + progressBounds.width - 1, progressBounds.y + progressBounds.height);
      graphics.setColor(progressColor);
      graphics.fillRect(progressBounds.x, progressBounds.y, progressBounds.width * progress / 100,
            progressBounds.height + 1);

      if (text != null && !text.isEmpty())
      {
         graphics.setFont(textFont);
         graphics.setColor(textColor);
         graphics.drawString(text, textPos.x, textPos.y);
      }

      if (versionText != null && !versionText.isEmpty())
      {
         graphics.setFont(versionTextFont);
         graphics.setColor(versionTextColor);
         graphics.drawString(versionText, versionTextPos.x, versionTextPos.y);
      }

      splash.update();
   }
}
