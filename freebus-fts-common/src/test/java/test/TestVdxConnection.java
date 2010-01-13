package test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.freebus.fts.common.vdx.VdxDriver;

import junit.framework.TestCase;


public class TestVdxConnection extends TestCase
{
   private Connection con;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      final VdxDriver driver = new VdxDriver();
      final Properties info = new Properties();

      con = driver.connect("file:src/test/resources/test-file.vd_", info);
   }

   @Override
   protected void tearDown() throws Exception
   {
      if (con != null)
      {
         con.close();
         con = null;
      }
   }
  
   public final void testClearWarnings() throws SQLException
   {
      con.clearWarnings();
   }

   public final void testCloseIsClosed() throws SQLException
   {
      assertFalse(con.isClosed());
      con.close();
      assertTrue(con.isClosed());
   }

   public final void testCommit()
   {

   }

   public final void testCreateArrayOf()
   {

   }

   public final void testCreateBlob()
   {

   }

   public final void testCreateClob()
   {

   }

   public final void testCreateNClob()
   {

   }

   public final void testCreateSQLXML()
   {

   }

   public final void testCreateStatement()
   {

   }

   public final void testCreateStatementIntInt()
   {

   }

   public final void testCreateStatementIntIntInt()
   {

   }

   public final void testCreateStruct()
   {

   }

   public final void testGetAutoCommit()
   {

   }

   public final void testGetCatalog()
   {

   }

   public final void testGetClientInfo()
   {

   }

   public final void testGetClientInfoString()
   {

   }

   public final void testGetHoldability()
   {

   }

   public final void testGetMetaData()
   {

   }

   public final void testGetTransactionIsolation()
   {

   }

   public final void testGetTypeMap()
   {

   }

   public final void testGetWarnings()
   {

   }

   public final void testIsReadOnly()
   {

   }

   public final void testIsValid()
   {

   }

   public final void testNativeSQL()
   {

   }

   public final void testPrepareCallString()
   {

   }

   public final void testPrepareCallStringIntInt()
   {

   }

   public final void testPrepareCallStringIntIntInt()
   {

   }

   public final void testPrepareStatementString()
   {

   }

   public final void testPrepareStatementStringInt()
   {

   }

   public final void testPrepareStatementStringIntArray()
   {

   }

   public final void testPrepareStatementStringStringArray()
   {

   }

   public final void testPrepareStatementStringIntInt()
   {

   }

   public final void testPrepareStatementStringIntIntInt()
   {

   }

   public final void testReleaseSavepoint()
   {

   }

   public final void testRollback()
   {

   }

   public final void testRollbackSavepoint()
   {

   }

   public final void testSetAutoCommit()
   {

   }

   public final void testSetCatalog()
   {

   }

   public final void testSetClientInfoProperties()
   {

   }

   public final void testSetClientInfoStringString()
   {

   }

   public final void testSetHoldability()
   {

   }

   public final void testSetReadOnly()
   {

   }

   public final void testSetSavepoint()
   {

   }

   public final void testSetSavepointString()
   {

   }

   public final void testSetTransactionIsolation()
   {

   }

   public final void testSetTypeMap()
   {

   }

   public final void testIsWrapperFor()
   {

   }

   public final void testUnwrap()
   {

   }

}
