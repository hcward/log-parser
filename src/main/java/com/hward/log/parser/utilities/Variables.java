package com.hward.log.parser.utilities;

public class Variables {

  private Variables() {
    throw new IllegalStateException("Utility Class");
  }

  public static final int MAX_BATCH_SIZE = 75000;
  public static final int MAX_PROCESSOR_QUEUE_SIZE = 100000;
  public static final int MAX_BATCH_DISTRIBUTOR_QUEUE_SIZE = 5;

}
