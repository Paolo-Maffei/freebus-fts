package test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.freebus.fts.persistence.SimpleConfig;
import org.junit.Test;

public class TestSimpleConfig
{
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
      assertEquals("", cfg.getStringValue("not-exists"));
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
      cfg.load("no-such-file");
   }
}
