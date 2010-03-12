package org.freebus.fts.common;

import static org.junit.Assert.*;

import org.junit.Test;


public class TestRot13
{
   @Test
   public final void testRotate()
   {
      assertEquals("", Rot13.rotate(""));
      assertEquals("nZ50$", Rot13.rotate("aM05$"));
      assertEquals("aBc123sTv890", Rot13.rotate(Rot13.rotate("aBc123sTv890")));
   }
}
