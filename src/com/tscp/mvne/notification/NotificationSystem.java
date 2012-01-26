package com.tscp.mvne.notification;

import java.util.List;

public interface NotificationSystem {

  public void init();

  public List<Notification> getFailedNotifications();

  public void sendNotification(Notification notification);

}
