package com.hward.log.parser.service;

import com.hward.log.parser.utilities.SqlHelper;
import com.hward.log.parser.utilities.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BatchDistributor implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(BatchDistributor.class);

  protected static final Queue<String> sqlStatements = new ConcurrentLinkedQueue<>();

  protected static boolean queueLimitReached() {
    return BatchDistributor.sqlStatements.size() >= Variables.MAX_BATCH_DISTRIBUTOR_QUEUE_SIZE;
  }

  private boolean running = true;

  @Override
  public void run() {
    try (Connection conn = DriverManager.getConnection(SqlHelper.getDbUrl()); Statement statement = conn.createStatement()) {
      while (!BatchDistributor.sqlStatements.isEmpty() || running || !EventProcessor.processorComplete()) {
        final String sql = sqlStatements.poll();

        if (sql != null) {
          executeQuery(sql, statement);
        }

      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    this.running = false;
  }

  private void executeQuery(String sql, Statement statement) {
    try {
      statement.executeQuery(sql);
      logger.info("SQL Statement Executed Successfully.");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
