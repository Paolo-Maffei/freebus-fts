package org.freebus.fts.utils;

import java.io.File;

/**
 * Recursively delete a directory and it's contents.
 */
public final class DeleteDir
{
   /**
    * Recursively delete the directory path and it's contents.
    */
   static public boolean deleteDir(File path)
   {
      if (!path.exists()) return true;
      
      File[] files = path.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         if (files[i].isDirectory())
         {
            deleteDir(files[i]);
         }
         else
         {
            files[i].delete();
         }
      }

      return (path.delete());
   }
}
