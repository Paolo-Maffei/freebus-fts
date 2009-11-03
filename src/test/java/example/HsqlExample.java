package example;

import java.sql.*;
import java.util.Properties;

public class HsqlExample
{
   static final String driver = "org.hsqldb.jdbcDriver";
   static final String protocol = "jdbc:hsqldb:file:";
   Connection conn = null;

   public void connect(String dbName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
   {
      Class.forName(driver);

      final Properties props = new Properties();
      conn = DriverManager.getConnection(protocol + dbName, props);
   }
   
   public void disconnect() throws SQLException
   {
      conn.rollback();
      conn.close();
   }

   public boolean execute(String sql) throws SQLException
   {
      Statement stmt = conn.createStatement();
      return stmt.execute(sql);
   }

   public static void main(String[] args) throws Exception
   {
//      System.setProperty("derby.system.home", ".");

      HsqlExample tst = new HsqlExample();
      tst.connect("HsqlExample.db");

      tst.execute("create table test(id int, name varchar(20))");
      tst.conn.commit();

      tst.disconnect();
   }
}
