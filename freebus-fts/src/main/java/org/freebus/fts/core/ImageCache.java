package org.freebus.fts.core;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * A global class that loads and caches the images.
 */
public final class ImageCache
{
   private static final Map<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();

   /**
    * Loads the icon with the given name.
    * 
    * @param iconName - the name of the icon, without extension. The path to the
    *           icon shall be a resource path that can be resolved by
    *           {@link ClassLoader#getResource(String)}.
    * 
    * @return the icon
    */
   public static synchronized ImageIcon getIcon(String iconName)
   {
      ImageIcon icon = iconCache.get(iconName);
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
