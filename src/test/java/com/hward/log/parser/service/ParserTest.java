package com.hward.log.parser.service;

import com.hward.log.parser.utils.LogFileGenerator;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class ParserTest {

  @Test
  public void testParser() throws IOException, SQLException {
    final int eventAmount = 100;
    final Path logFilePath = LogFileGenerator.generate(eventAmount);

    final Parser parser = new Parser(logFilePath.toString());
    parser.parseFile();

    assertEquals(eventAmount * 2, EventProcessor.lines.size());

    LogFileGenerator.delete();
  }

}