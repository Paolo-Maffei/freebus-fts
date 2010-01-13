package org.freebus.fts.core;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * A global class that loads and caches the images.
 */
public final class ImageCache
{
   private static final Map<String, Icon> iconCache = new HashMap<String, Icon>();

   /**
    * Loads the icon with the given name.
    */
   public static synchronized Icon getIcon(String iconName)
   {
      Icon icon = iconCache.get(iconName);
      if (icon == null)
      {
         final ClassLoader classLoader = ImageCache.class.getClassLoader();

         java.net.URL imgURL = classLoader.getResource(iconName + ".png");
         if (imgURL != null)
         {
            icon = new ImageIcon(imgURL);
         }
         else
         {
            Logger.getLogger(ImageCache.class).error("Could not find icon: " + iconName);
         }
         iconCache.put(iconName, icon);
      }
      return icon;
   }
}
