package org.freebus.fts.common.vdx;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A JDBC driver for connecting to a vd_ file.
 */
public class VdxDriver implements Driver
{
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean acceptsURL(String url) throws SQLException
   {
      return url != null && url.startsWith("file:");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Connection connect(String url, Properties info) throws SQLException
   {
      return new VdxConnection(url, info);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getMajorVersion()
   {
      return 1;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getMinorVersion()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean jdbcCompliant()
   {
      return false;
   }

}
