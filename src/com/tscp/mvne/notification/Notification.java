package com.tscp.mvne.notification;

import java.util.Date;
import java.util.List;

import javax.persistence.Enumerated;

import com.tscp.mvne.notification.dao.NotificationParameter;

public interface Notification {

  public int getNotificationId();

  public void setNotificationId(int notificationId);

  @Enumerated
  public NotificationCategory getNotificationCategory();

  public void setNotificationCategory(NotificationCategory notificationCategory);

  @Enumerated
  public NotificationType getNotificationType();

  public void setNotificationType(NotificationType notificationType);

  public Date getCreateDate();

  public void setCreateDate(Date createDate);

  public int getAttemptNo();

  public void setAttemptNo(int attemptNo);

  public Date getSentDate();

  public void setSentDate(Date sentDate);

  public void loadNotification();

  public void saveNotification();

  public List<NotificationParameter> getNotificationParameters();

  public void setNotificationParameters(List<NotificationParameter> notificationParametersList);

}
