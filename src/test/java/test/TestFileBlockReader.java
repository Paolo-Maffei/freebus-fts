package test;

import java.io.IOException;
import java.io.RandomAccessFile;

import junit.framework.TestCase;

import org.freebus.fts.utils.FileBlockReader;

public class TestFileBlockReader extends TestCase
{
   private FileBlockReader reader = null;
   private RandomAccessFile in = null;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      in = new RandomAccessFile("src/test/resources/test-fileblockreader.txt", "r");
      reader = new FileBlockReader(in, 10);
   }

   @Override
   protected void tearDown()
   {
      try
      {
         in.close();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      reader = null;
      in = null;
   }

   public void testAtEnd() throws IOException
   {
      assertFalse(reader.atEnd());
      while (!reader.atEnd())
         reader.read();

      assertEquals(in.length(), in.getFilePointer());
   }

   public void testGetFilePointer() throws IOException
   {
      assertEquals(0, reader.getFilePointer());
      for (long bytesRead = 0; !reader.atEnd(); )
      {
         reader.read();
         if (reader.atEnd()) break;
         assertEquals(++bytesRead, reader.getFilePointer());
      }
   }

   public void testSeek() throws IOException
   {
      reader.seek(4);
      assertEquals(4, reader.getFilePointer());
      assertEquals('4', reader.read());

      reader.seek(8);
      assertEquals(8, reader.getFilePointer());
      assertEquals('8', reader.read());

      reader.seek(0);
      assertEquals(0, reader.getFilePointer());
      assertEquals('0', reader.read());

      reader.readLine();
      assertEquals('l', reader.read());
   }

   public void testRead() throws IOException
   {
      assertEquals('0', reader.read());
      assertEquals('1', reader.read());
      assertEquals('2', reader.read());
      assertEquals('3', reader.read());
   }

   public void testReadWord() throws IOException
   {
      assertEquals("0123456789", reader.readWord());
      assertEquals("linüä", reader.readWord());
      assertEquals("2", reader.readWord());
   }

   public void testReadLine() throws IOException
   {
      assertEquals("0123456789", reader.readLine());
      assertEquals("linüä 2  ", reader.readLine());
   }

}
