package com.hward.log.parser;

import com.hward.log.parser.service.BatchDistributor;
import com.hward.log.parser.service.EventProcessor;
import com.hward.log.parser.service.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws InterruptedException, IOException, SQLException {
    final String logFilePath = args[0];

    final Parser parser = new Parser(logFilePath);

    final EventProcessor eventProcessor = EventProcessor.getProcessor();
    new Thread(eventProcessor).start();

    final BatchDistributor batchDistributor = new BatchDistributor();

    final Thread batchDistributorThread = new Thread(batchDistributor);
    batchDistributorThread.start();

    final long startTime = System.currentTimeMillis();
    parser.parseFile();

    eventProcessor.stop();
    batchDistributor.stop();

    batchDistributorThread.join();
    logger.info("Parsing Completed in {} Milliseconds.", System.currentTimeMillis() - startTime);
  }

}
