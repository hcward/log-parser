package com.hward.log.parser.utils;

import com.hward.log.parser.model.enums.State;
import com.hward.log.parser.model.objects.Event;
import com.hward.log.parser.utilities.Util;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Random;

public class LogFileGenerator {

  public static Path generate(int events) throws IOException {
    final File file = new File("logfile.txt");
    final StringBuilder contents = new StringBuilder();

    if (file.exists()) {
      FileUtils.write(file, "", Charset.defaultCharset());
    }

    final Random random = new Random();
    for (int i = 0; i < events; i++) {
      final String id = String.valueOf(i);
      final Event startEvent = new Event(id, State.STARTED,
          System.currentTimeMillis() - random.nextInt(6), "APPLICATION_LOG", "12345");
      final Event finishEvent = new Event(id, State.FINISHED,
          System.currentTimeMillis(), "APPLICATION_LOG", "12345");

      final String jsonEvents;
      if (random.nextInt(2) == 0) {
        jsonEvents = Util.gson.toJson(startEvent) + "\n" + Util.gson.toJson(finishEvent) + "\n";
      } else {
        jsonEvents = Util.gson.toJson(finishEvent) + "\n" + Util.gson.toJson(startEvent) + "\n";
      }
      contents.append(jsonEvents);

      if (contents.length() >= 100000) {
        FileUtils.write(file, contents.toString(), Charset.defaultCharset(), true);
        contents.setLength(0);
      }
    }

    FileUtils.write(file, contents.toString(), Charset.defaultCharset(), true);
    return file.toPath();
  }

  public static void delete() {
    final File file = new File("logfile.txt");
    if (file.exists()) {
      file.delete();
    }
  }

}
