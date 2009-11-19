package org.freebus.fts.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * A global class that loads and caches the images.
 */
public final class ImageCache
{
   private static final Map<String,Image> cache = new HashMap<String,Image>();

   /**
    * Loads the image with the given name.
    * The images are cached for better performance.
    * 
    * @param imageName is the name of the image, without path, without extension.
    * @return the requested image.
    * @throws MissingResourceException if the image file is not found.
    */
   public static synchronized Image getImage(String imageName)
   {
      Image img = cache.get(imageName);
      if (img!=null) return img;

      final ClassLoader classLoader = ImageCache.class.getClassLoader();
      
      String imageFullName =  imageName + ".png";
      InputStream in = classLoader.getResourceAsStream(imageFullName);
      if (in == null) in = classLoader.getResourceAsStream("src/main/resources/" + imageFullName);

      if (in == null) throw new MissingResourceException("Image not found: "+imageFullName, ImageCache.class.getCanonicalName(), imageName);
      
      final ImageData imgData = new ImageData(in);
      if (imgData==null) throw new MissingResourceException("Image file is broken: "+imageFullName, ImageCache.class.getCanonicalName(), in.toString());

      img = new Image(null, imgData);
      cache.put(imageName, img);

      return img;
   }
}
