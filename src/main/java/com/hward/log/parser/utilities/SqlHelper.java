package com.hward.log.parser.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class SqlHelper {

  private SqlHelper() {
    throw new IllegalStateException("Utility Class");
  }

  public static String getDbUrl() {
    final ClassLoader classLoader = SqlHelper.class.getClassLoader();
    final String dbPath = Objects.requireNonNull(classLoader.getResource("db")).getPath() + "/db";
    return "jdbc:hsqldb:file:" + dbPath + ";";
  }

  public static void createEventDetailsTable() throws SQLException {
    try (Connection conn = DriverManager.getConnection(getDbUrl()); Statement statement = conn.createStatement()) {
      final String sql = "CREATE MEMORY TABLE IF NOT EXISTS PUBLIC.EVENT_DETAILS(ID VARCHAR(255) NOT NULL PRIMARY KEY," +
          "ALERT VARCHAR(5),DURATION INTEGER NOT NULL,HOST VARCHAR(255),TYPE VARCHAR(255)," +
          "CHECK((PUBLIC.EVENT_DETAILS.ALERT) IN (('TRUE'),('FALSE'))));";
      statement.execute(sql);
    }
  }

}
