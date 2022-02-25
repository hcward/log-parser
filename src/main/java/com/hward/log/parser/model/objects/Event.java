package com.hward.log.parser.model.objects;

import com.hward.log.parser.model.enums.Alert;
import com.hward.log.parser.model.enums.State;

public class Event {

  private final String id;
  private final State state;
  private long timestamp;
  private final String type;
  private final String host;

  private int duration;

  public Event(String id, State state, long timestamp, String type, String host) {
    this.id = id;
    this.state = state;
    this.timestamp = timestamp;
    this.type = type;
    this.host = host;
  }

  public void completeEvent(Event event) {
    if (event.getState().equals(State.FINISHED)) {
      setDuration(event.getTimestamp() - this.getTimestamp());
      setTimestamp(event.getTimestamp());
    } else {
      setDuration(this.getTimestamp() - event.getTimestamp());
    }
  }

  public String generateSql() {
    return String.format("INSERT INTO EVENT_DETAILS VALUES ('%s','%s',%d,'%s','%s');",
        getId(), getAlert().name(), getDuration(), getHost(), getType());
  }

  public String getId() {
    return id;
  }

  public State getState() {
    return state;
  }

  public long getTimestamp() {
    return timestamp;
  }

  private void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getType() {
    return type;
  }

  public String getHost() {
    return host;
  }

  public int getDuration() {
    return duration;
  }

  private void setDuration(Long duration) {
    this.duration = duration.intValue();
  }

  private Alert getAlert() {
    return getDuration() > 4 ? Alert.TRUE : Alert.FALSE;
  }

}
