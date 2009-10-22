package example;

import java.sql.*;
import java.util.Properties;

public class DerbyExample
{
   static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
   static final String protocol = "jdbc:derby:";
   Connection conn = null;

   public void connect(String dbName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
   {
      Class.forName(driver).newInstance();

      final Properties props = new Properties();
      conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
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

      DerbyExample tst = new DerbyExample();
      tst.connect("DerbyExample.db");

      tst.execute("create table test(id int, name varchar(20))");
      tst.conn.commit();

      tst.disconnect();
   }
}
