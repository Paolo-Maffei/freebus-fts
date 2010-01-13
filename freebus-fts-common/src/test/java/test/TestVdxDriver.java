package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;

import org.freebus.fts.common.vdx.VdxDriver;

public class TestVdxDriver extends TestCase
{
   public final void testAcceptsURL() throws SQLException
   {
      final VdxDriver driver = new VdxDriver();

      assertFalse(driver.acceptsURL(null));
      assertTrue(driver.acceptsURL("file:src/test/resources/test-file.vd_"));
   }

   public final void testConnect() throws SQLException
   {
      final VdxDriver driver = new VdxDriver();
      final Properties info = new Properties();
      Connection con;

      try
      {
         con = driver.connect(null, info);
         assertTrue("exception not thrown", false);
      }
      catch (SQLException e)
      {
      }

      con = driver.connect("file:src/test/resources/test-file.vd_", info);
      assertNotNull(con);
   }
}
