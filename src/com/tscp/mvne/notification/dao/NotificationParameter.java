package com.tscp.mvne.notification.dao;

import java.io.Serializable;

public class NotificationParameter implements Serializable {
  private static final long serialVersionUID = 1L;

  int notificationId;
  int seq;
  String key;
  String value;

  public NotificationParameter() {
  }

  public NotificationParameter(String key, String value) {
    setKey(key);
    setValue(value);
  }

  public int getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(int notificationId) {
    this.notificationId = notificationId;
  }

  public int getSeq() {
    return seq;
  }

  public void setSeq(int seq) {
    this.seq = seq;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("NotificationParameter object....");
    sb.append("\n");
    sb.append("NotificationId  :: " + getNotificationId());
    sb.append("\n");
    sb.append("Key             :: " + getKey());
    sb.append("\n");
    sb.append("Value           :: " + getValue());
    sb.append("\n");
    return sb.toString();
  }

}
