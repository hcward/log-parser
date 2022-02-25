package com.hward.log.parser.service;

import com.hward.log.parser.utilities.Variables;
import com.hward.log.parser.utils.LogFileGenerator;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class EventProcessorTest {

  @Test
  public void testEventProcessor() throws IOException, InterruptedException {
    final Thread thread = new Thread(EventProcessor.getProcessor());
    thread.start();

    final int eventAmount = 27500;

    final Path logFilePath = LogFileGenerator.generate(eventAmount);
    try (Stream<String> stream = Files.lines(logFilePath)) {
      stream.forEach(EventProcessor.lines::add);
    }

    EventProcessor.getProcessor().stop();
    thread.join();

    int expectedSize = eventAmount % Variables.MAX_BATCH_SIZE == 0 ? eventAmount / Variables.MAX_BATCH_SIZE
        : (eventAmount / Variables.MAX_BATCH_SIZE) + 1;

    assertEquals(expectedSize, BatchDistributor.sqlStatements.size());

    LogFileGenerator.delete();
  }

}