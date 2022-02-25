package com.hward.log.parser.utilities;

import com.google.gson.Gson;

public class Util {

  private Util() {
    throw new IllegalStateException("Utility Class");
  }

  public static final Gson gson = new Gson();

}
