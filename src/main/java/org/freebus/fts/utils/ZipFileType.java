package org.freebus.fts.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Types of ZIP files.
 */
public enum ZipFileType
{
   /**
    * No ZIP file.
    */
   NONE,

   /**
    * ZIP file, not password protected.
    */
   PLAIN,

   /**
    * ZIP file, password protected with normal encryption.
    */
   ENCRYPTED,

   /**
    * ZIP file, password protected with strong encryption.
    */
   ENCRYPTED_STRONG;

   /**
    * Inspect the given file.
    * 
    * @param fileName - the file to inspect.
    * @return the ZIP file type.
    * @throws IOException if the file is not readable.
    */
   public static ZipFileType inspectFile(String fileName) throws IOException
   {
      return inspectStream(new FileInputStream(fileName));
   }

   /**
    * Inspect the given input stream.
    * 
    * @param in - the input stream that points to a (potential) ZIP file.
    * @return the ZIP file type.
    * @throws IOException if the stream is not readable.
    */
   public static ZipFileType inspectStream(InputStream in) throws IOException
   {
      // Format of a ZIP file:
      //
      // ZIP file header signature    4 bytes
      // version needed to extract    2 bytes
      // general purpose bit flag     2 bytes

      final byte[] data = new byte[8];
      if (in.read(data) < data.length) return ZipFileType.NONE;

      for (int i = zipSignature.length - 1; i >= 0; --i)
         if (data[i] != zipSignature[i]) return ZipFileType.NONE;

      final int flags = ((data[7] & 0xff) << 8) | (data[6] & 0xff);

      if ((flags & 1) == 0) return ZipFileType.PLAIN;
      else if ((flags & (1<<6)) == 0) return ZipFileType.ENCRYPTED;
      return ZipFileType.ENCRYPTED_STRONG;
   }

   // ZIP file signature
   private static final byte[] zipSignature = { 0x50, 0x4b, 0x03, 0x04 };
}
