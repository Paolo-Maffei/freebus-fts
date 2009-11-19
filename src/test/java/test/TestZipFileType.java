package test;

import java.io.ByteArrayInputStream;

import org.freebus.fts.utils.ZipFileType;

import junit.framework.TestCase;

public class TestZipFileType extends TestCase
{
   public void testShortFile() throws Exception
   {
      final byte[] fileData = new byte[] { 1, 2, 3, 4 };
      final ByteArrayInputStream in = new ByteArrayInputStream(fileData, 0, fileData.length);
      assertEquals(ZipFileType.NONE, ZipFileType.inspectStream(in));
   }

   public void testOtherFile() throws Exception
   {
      final byte[] fileData = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
      final ByteArrayInputStream in = new ByteArrayInputStream(fileData, 0, fileData.length);
      assertEquals(ZipFileType.NONE, ZipFileType.inspectStream(in));
   }

   public void testNormalZip() throws Exception
   {
      final byte[] fileData = new byte[] { 0x50, 0x4b, 0x03, 0x04, 0x14, 0x00, 0x00, 0x00, 0x08 };
      final ByteArrayInputStream in = new ByteArrayInputStream(fileData, 0, fileData.length);
      assertEquals(ZipFileType.PLAIN, ZipFileType.inspectStream(in));
   }

   public void testEncryptedZip() throws Exception
   {
      final byte[] fileData = new byte[] { 0x50, 0x4b, 0x03, 0x04, 0x14, 0x00, 0x01, 0x00, 0x08 };
      final ByteArrayInputStream in = new ByteArrayInputStream(fileData, 0, fileData.length);
      assertEquals(ZipFileType.ENCRYPTED, ZipFileType.inspectStream(in));
   }

   public void testStrongEncryptedZip() throws Exception
   {
      final byte[] fileData = new byte[] { 0x50, 0x4b, 0x03, 0x04, 0x14, 0x00, 0x41, 0x00, 0x08 };
      final ByteArrayInputStream in = new ByteArrayInputStream(fileData, 0, fileData.length);
      assertEquals(ZipFileType.ENCRYPTED_STRONG, ZipFileType.inspectStream(in));
   }
}
