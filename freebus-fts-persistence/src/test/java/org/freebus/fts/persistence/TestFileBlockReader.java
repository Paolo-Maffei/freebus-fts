package org.freebus.fts.persistence;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFileBlockReader
{
   private org.freebus.fts.persistence.FileBlockReader reader = null;
   private RandomAccessFile in = null;

   @Before
   public void setUp() throws Exception
   {
      in = new RandomAccessFile("src/test/resources/test-fileblockreader.txt", "r");
      reader = new FileBlockReader(in, 8);
   }

   @After
   public void tearDown()
   {
      try
      {
         in.close();
      }
      catch (IOException e)
      {
      }
      reader = null;
      in = null;
   }

   @Test
   public void testFileBlockReader() throws IOException
   {
      assertNotNull(new FileBlockReader(in));
   }

   @Test
   public void testAtEndFileSize() throws IOException
   {
      assertFalse(reader.atEnd());
      while (!reader.atEnd())
         reader.read();

      assertEquals(in.length(), in.getFilePointer());
      assertEquals(in.length(), reader.getFileSize());
   }

   @Test
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

   @Test
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

   @Test
   public void testRead() throws IOException
   {
      assertEquals('0', reader.read());
      assertEquals('1', reader.read());
      assertEquals('2', reader.read());
      assertEquals('3', reader.read());
   }

   @Test
   public void testReadUntil() throws IOException
   {
      while (reader.readUntil('a'))
         ;
      assertTrue(reader.atEnd());
   }

   @Test
   public void testReadWord() throws IOException
   {
      assertEquals("0123456789", reader.readWord());
      assertEquals("lin\u00FC\u00E4", reader.readWord());
      assertEquals("2", reader.readWord());
   }

   @Test
   public void testReadLine() throws IOException
   {
      assertEquals("0123456789", reader.readLine());
      assertEquals("lin\u00FC\u00E4 2  ", reader.readLine());
   }
}
