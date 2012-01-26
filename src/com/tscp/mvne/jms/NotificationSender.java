package com.tscp.mvne.jms;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import com.tscp.mvne.notification.dao.EmailNotification;

public class NotificationSender {

  final EmailNotification emailNotification;

  public NotificationSender(EmailNotification emailNotification) {
    this.emailNotification = emailNotification;
  }

  /**
   * Send the notifications to the queue
   */
  public void send() {

    QueueConnectionFactory queueConnectionFactory = null;
    QueueConnection queueConnection = null;
    QueueSession queueSession = null;
    Queue notificationQueue = null;
    QueueSender queueSender = null;
    ObjectMessage objectMessage = null;

    try {
      queueConnectionFactory = JMSHelper.getQueueConnectionFactory();
      queueConnection = queueConnectionFactory
          .createQueueConnection(JMSHelper.QUEUE_USERNAME, JMSHelper.QUEUE_PASSWORD);
      // queueConnection = queueConnectionFactory.createQueueConnection();
      queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
      notificationQueue = JMSHelper.getQueue("jms/Destination", queueSession);

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error establishing connection " + e.getMessage());

      if (queueConnection != null) {
        try {
          queueConnection.close();
        } catch (JMSException e1) {
          e1.printStackTrace();
        }
      }

    }

    try {
      queueSender = queueSession.createSender(notificationQueue);
      objectMessage = queueSession.createObjectMessage(emailNotification);

      queueSender.send(objectMessage);

      System.out.println("Message sent...");

      /*
       * Send a non-text control message indicating end of messages.
       */
      queueSender.send(queueSession.createMessage());
    } catch (JMSException jms_ex) {
      System.err.println("NotificationSender: Exception " + "occurred: " + jms_ex.toString());
      jms_ex.printStackTrace();
    } finally {
      if (queueConnection != null) {
        try {
          queueConnection.close();
        } catch (JMSException e) {
        }
      }
    }
    // super.run();
  }

  public static void main(String[] args) {
    NotificationSender sender = new NotificationSender(null);
    sender.send();
    System.out.println("Done testing...");

  }
}
