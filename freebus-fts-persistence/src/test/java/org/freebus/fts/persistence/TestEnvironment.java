package org.freebus.fts.persistence;

import static org.junit.Assert.*;

import java.io.File;

import org.freebus.fts.persistence.Environment;
import org.junit.Test;

public class TestEnvironment
{
   @Test
   public final void testGetOS()
   {
      assertEquals(System.getProperty("os.name", "").toLowerCase(), Environment.getOS());
   }

   @Test
   public final void testGetTempDir()
   {
      final File dir = new File(Environment.getTempDir());
      assertNotNull(dir);
      assertTrue(dir.exists());
   }

   @Test
   public final void testGetHomeDir()
   {
      final File dir = new File(Environment.getHomeDir());
      assertNotNull(dir);
      assertTrue(dir.exists());
   }

   @Test
   public final void testGetAppDir()
   {
      final File dir = new File(Environment.getAppDir());
      assertNotNull(dir);
      assertTrue(dir.exists());
      assertFalse(Environment.getAppDir().isEmpty());
   }

   @Test
   public final void testGetAppName()
   {
      assertFalse(Environment.getAppName().isEmpty());
   }

   @Test
   public final void testSetAppName()
   {
      Environment.setAppName("test1");
      assertEquals("test1", Environment.getAppName());

      Environment.setAppName("test2");
      assertEquals("test2", Environment.getAppName());
   }

}
