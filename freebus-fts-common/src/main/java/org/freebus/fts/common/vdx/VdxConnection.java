package org.freebus.fts.common.vdx;

import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * A JDBC connection for a vd_ file.
 */
public class VdxConnection implements Connection
{
   private VdxFileReader reader;

   /**
    * Create a connection for a vd_ file.
    */
   VdxConnection(String url, Properties info) throws SQLException
  {
      try
      {
         reader = new VdxFileReader((new URL(url)).getPath());
      }
      catch (Exception e)
      {
         throw new SQLException("cannot open file: " + url, e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void clearWarnings() throws SQLException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close() throws SQLException
   {
      if (reader != null)
      {
         reader.close();
         reader = null;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void commit() throws SQLException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Array createArrayOf(String typeName, Object[] elements) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Blob createBlob() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Clob createClob() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NClob createNClob() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public SQLXML createSQLXML() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public Statement createStatement() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
         throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public Struct createStruct(String typeName, Object[] attributes) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public boolean getAutoCommit() throws SQLException
   {
      return true;
   }

   @Override
   public String getCatalog() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public Properties getClientInfo() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public String getClientInfo(String name) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public int getHoldability() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public DatabaseMetaData getMetaData() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public int getTransactionIsolation() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public Map<String, Class<?>> getTypeMap() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public SQLWarning getWarnings() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public boolean isClosed() throws SQLException
   {
      return reader == null;
   }

   @Override
   public boolean isReadOnly() throws SQLException
   {
      return true;
   }

   @Override
   public boolean isValid(int timeout) throws SQLException
   {
      return !isClosed();
   }

   @Override
   public String nativeSQL(String sql) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public CallableStatement prepareCall(String sql) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
         int resultSetHoldability) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public PreparedStatement prepareStatement(String sql) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
         throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
         int resultSetHoldability) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public void releaseSavepoint(Savepoint savepoint) throws SQLException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void rollback() throws SQLException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void rollback(Savepoint savepoint) throws SQLException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAutoCommit(boolean autoCommit) throws SQLException
   {
   }

   @Override
   public void setCatalog(String catalog) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public void setClientInfo(Properties properties) throws SQLClientInfoException
   {
      throw new SQLClientInfoException();
   }

   @Override
   public void setClientInfo(String name, String value) throws SQLClientInfoException
   {
      throw new SQLClientInfoException();
   }

   @Override
   public void setHoldability(int holdability) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public void setReadOnly(boolean readOnly) throws SQLException
   {
   }

   @Override
   public Savepoint setSavepoint() throws SQLException
   {
      throw new SQLException("not implemented");
   }

   @Override
   public Savepoint setSavepoint(String name) throws SQLException
   {
      throw new SQLException("not implemented");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setTransactionIsolation(int level) throws SQLException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setTypeMap(Map<String, Class<?>> map) throws SQLException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isWrapperFor(Class<?> iface) throws SQLException
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <T> T unwrap(Class<T> iface) throws SQLException
   {
      return null;
   }

}
