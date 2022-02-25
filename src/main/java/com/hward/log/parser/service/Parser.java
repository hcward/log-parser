package com.hward.log.parser.service;

import com.hward.log.parser.utilities.SqlHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Parser {

  private static final Logger logger = LoggerFactory.getLogger(Parser.class);

  private final File logFile;

  public Parser(String logFilepath) {
    this.logFile = new File(logFilepath);
  }

  public void parseFile() throws IOException, SQLException {
    SqlHelper.createEventDetailsTable();

    if (!logFile.exists()) {
      throw new FileNotFoundException("File Does Not Exist: " + logFile.toPath());
    }

    logger.info("Initiating Parsing of File {} with Size {} bytes.", logFile.getName(), FileUtils.sizeOf(logFile));
    try (Stream<String> stream = Files.lines(logFile.toPath())) {
      stream.forEach(line -> {
        while (EventProcessor.queueLimitReached());
        EventProcessor.lines.add(line);
      });
    }
    logger.info("All Lines of File {} Distributed.", logFile.getName());
  }

}
