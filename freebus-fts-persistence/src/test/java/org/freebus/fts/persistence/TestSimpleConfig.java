package org.freebus.fts.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.freebus.fts.persistence.Environment;
import org.freebus.fts.persistence.SimpleConfig;
import org.junit.After;
import org.junit.Test;

public class TestSimpleConfig
{
   private String tempFileName;

   @After
   public final void tearDown()
   {
      if (tempFileName != null)
      {
         final File tempFile = new File(tempFileName);
         if (tempFile.exists()) tempFile.delete();
      }
   }

   @Test
   public final void testGetInstance()
   {
      SimpleConfig.disposeInstance();
      assertNotNull(SimpleConfig.getInstance());
      assertNotNull(SimpleConfig.getInstance());
   }

   @Test
   public final void testSimpleConfig()
   {
      assertNotNull(new SimpleConfig());
   }

   @Test
   public final void testContainsKey()
   {
      final SimpleConfig cfg = new SimpleConfig();
      assertFalse(cfg.containsKey(""));
      assertFalse(cfg.containsKey("nix"));
   }

   @Test
   public final void testPutGet()
   {
      final SimpleConfig cfg = new SimpleConfig();
      assertEquals(null, cfg.get("key-1"));
      cfg.put("key-1", "val-1");
      assertEquals("val-1", cfg.get("key-1"));
   }

   @Test
   public final void testGetStringValue()
   {
      final SimpleConfig cfg = new SimpleConfig();
      cfg.put("key-1", "val-1");
      assertEquals("val-1", cfg.getStringValue("key-1"));
      assertEquals("", cfg.getStringValue("does-not-exist"));
   }

   @Test
   public final void testGetIntValue()
   {
      final SimpleConfig cfg = new SimpleConfig();
      cfg.put("key-1", "123");
      assertEquals(123, cfg.getIntValue("key-1"));
      assertEquals(0, cfg.getIntValue("does-not-exist"));
   }

   @Test
   public final void testClear()
   {
      final SimpleConfig cfg = new SimpleConfig();
      cfg.put("key-1", "val-1");
      cfg.clear();
      assertEquals(null, cfg.get("key-1"));
   }

   @Test
   public final void testLoadSave() throws IOException
   {
      final SimpleConfig cfg = new SimpleConfig();
      tempFileName = Environment.getTempDir() + "/test-simple-config.tmp";
      cfg.put("key-1", "val-1");
      cfg.put("key-2", "2");
      cfg.save(tempFileName);

      final SimpleConfig cfg2 = new SimpleConfig();
      cfg2.load(tempFileName);
      assertEquals(cfg.get("key-1"), cfg2.get("key-1"));
      assertEquals(cfg.get("key-2"), cfg2.get("key-2"));

      cfg2.put("key-1", "another-value");

      cfg2.load(tempFileName);
      assertEquals(cfg.get("key-1"), cfg2.get("key-1"));
      assertEquals(cfg.get("key-2"), cfg2.get("key-2")); 
   }
}
