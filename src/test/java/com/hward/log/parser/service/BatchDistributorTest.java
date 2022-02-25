package com.hward.log.parser.service;

import com.hward.log.parser.utilities.SqlHelper;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

public class BatchDistributorTest {

  @Before
  public void setup() throws SQLException {
    SqlHelper.createEventDetailsTable();
    try (Connection conn = DriverManager.getConnection(SqlHelper.getDbUrl()); Statement statement = conn.createStatement()) {
      statement.execute("DELETE FROM EVENT_DETAILS;");
    }
  }

  @Test
  public void testBatchDistributor() throws SQLException, InterruptedException {
    final BatchDistributor batchDistributor = new BatchDistributor();

    final Thread thread = new Thread(batchDistributor);
    thread.start();

    EventProcessor.getProcessor().complete();

    final String sqlStatement = "INSERT INTO EVENT_DETAILS VALUES ('1234','FALSE',3,'12345','APPLICATION_LOG');";

    BatchDistributor.sqlStatements.add(sqlStatement);

    batchDistributor.stop();

    thread.join();

    final ResultSet resultSet;
    try (Connection conn = DriverManager.getConnection(SqlHelper.getDbUrl()); Statement statement = conn.createStatement()) {
      resultSet = statement.executeQuery("SELECT * FROM EVENT_DETAILS WHERE ID = '1234';");
    }

    resultSet.next();
    assertEquals("1234", resultSet.getString(1));
  }

}