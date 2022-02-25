package com.hward.log.parser.service;

import com.hward.log.parser.model.objects.Event;
import com.hward.log.parser.utilities.Util;
import com.hward.log.parser.utilities.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventProcessor implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(EventProcessor.class);

  private static EventProcessor eventProcessor;
  protected static final Queue<String> lines = new ConcurrentLinkedQueue<>();
  protected static final Map<String, Event> openEvents = new ConcurrentHashMap<>();

  private boolean running = true;
  private boolean complete = false;
  private final StringBuilder query = new StringBuilder();
  private int batchSize = 0;

  public static synchronized EventProcessor getProcessor() {
    if (eventProcessor == null) {
      eventProcessor = new EventProcessor();
    }
    return eventProcessor;
  }

  protected static boolean queueLimitReached() {
    return EventProcessor.lines.size() >= Variables.MAX_PROCESSOR_QUEUE_SIZE;
  }

  public static synchronized boolean processorComplete() {
    return getProcessor().complete;
  }

  private EventProcessor() { }

  @Override
  public void run() {
    while (!lines.isEmpty() || this.running) {
      final String line = lines.poll();

      if (line == null) {
        continue;
      }

      final Event completeEvent = processLine(line);

      if (completeEvent != null) {
        query.append(completeEvent.generateSql());

        if (++this.batchSize >= Variables.MAX_BATCH_SIZE) {
          commitBatch();
          this.batchSize = 0;
        }
      }
    }

    if (this.batchSize != 0) {
      commitBatch();
    }

    complete();
  }

  public void stop() {
    this.running = false;
  }

  private Event processLine(String line) {
    final Event event = Util.gson.fromJson(line, Event.class);
    final Event currentEvent = openEvents.get(event.getId());

    if (currentEvent == null) {
      openEvents.put(event.getId(), event);
      return null;
    }

    currentEvent.completeEvent(event);
    openEvents.remove(currentEvent.getId());

    return currentEvent;
  }

  private void commitBatch() {
    final String queryString = this.query.toString();
    while (BatchDistributor.queueLimitReached());
    BatchDistributor.sqlStatements.add(queryString);
    logger.info("{} Complete Events Pushed to Batch Distributor Queue.", this.batchSize);

    this.query.setLength(0);
  }

  protected void complete() {
    this.complete = true;
  }

}
