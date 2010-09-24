package org.freebus.fts.elements.services;

import java.net.URL;
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

         final URL imgURL = classLoader.getResource(iconName + ".png");
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

   /**
    * Load the icon with the given name, and add the overlay icon with the given
    * name.
    *
    * @param iconName - the name of the icon, without extension. The path to the
    *           icon shall be a resource path that can be resolved by
    *           {@link ClassLoader#getResource(String)}.
    * @param overlayName - the name of the overlay icon, without extension. The
    *           path to the overlay icon shall be a resource path that can be
    *           resolved by {@link ClassLoader#getResource(String)}.
    *
    * @return the combined icon
    */
   public static synchronized ImageIcon getIcon(String iconName, String overlayName)
   {
      final String cacheKey = iconName + '|' + overlayName;
      ImageIcon icon = iconCache.get(cacheKey);
      if (icon != null)
         return icon;

      final ClassLoader classLoader = ImageCache.class.getClassLoader();
      final URL imgURL = classLoader.getResource(iconName + ".png");
//      final URL ovlURL = classLoader.getResource(overlayName + ".png");

      // TODO, see http://www.jguru.com/faq/view.jsp?EID=130031

      if (imgURL != null)
      {
         icon = new ImageIcon(imgURL);
      }
      else
      {
         Logger.getLogger(ImageCache.class).error("Could not find icon: " + iconName);
      }

      iconCache.put(cacheKey, icon);
      return icon;
   }
}
