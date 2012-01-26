package com.tscp.mvne.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import com.tscp.mvne.notification.dao.EmailNotification;

public class NotificationHandler extends Thread {

  public NotificationHandler() {
    // setDaemon(true);
  }

  /**
   * Send the notification to the jms Queue import
   * com.sun.messaging.jms.Session;
   */
  @Override
  public void run() {
    QueueConnectionFactory queueConnectionFactory = null;
    QueueConnection queueConnection = null;
    QueueSession queueSession = null;
    Queue emailNotificationQueue = null;
    QueueReceiver emailNotificationQueueReceiver = null;
    NotificationMessageListener notificationMessageListener = null;
    Message inMessage = null;
    ObjectMessage objectMessage = null;
    EmailNotification emailNotification;
    while (true) {
      System.out.println("Starting JMS Listening service...");
      try {
        queueConnectionFactory = JMSHelper.getQueueConnectionFactory();
        queueConnection = queueConnectionFactory.createQueueConnection(JMSHelper.QUEUE_USERNAME,
            JMSHelper.QUEUE_PASSWORD);
        queueSession = queueConnection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
        emailNotificationQueue = JMSHelper.getQueue("jms/Destination", queueSession);
        // emailNotificationQueue
      } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Connection problem: " + e.toString());
        if (queueConnection != null) {
          try {
            queueConnection.close();
          } catch (JMSException ee) {
          }
        }
        System.exit(1);
      }

      // final int MAX_RUNS = 20;
      // int counter = 0;
      // while ( counter < MAX_RUNS ) {
      try {
        emailNotificationQueueReceiver = queueSession.createReceiver(emailNotificationQueue);

        notificationMessageListener = new NotificationMessageListener(queueSession);
        emailNotificationQueueReceiver.setMessageListener(notificationMessageListener);

        System.out.println("Starting queue connection...");
        queueConnection.start();

        /*
         * Process orders in vendor order queue. Use one transaction to receive
         * order from order queue and send messages to suppliers' order queues
         * to order components to fulfill the order placed with the vendor.
         */
        while (true) {
          // try {
          // Receive an order from a retailer.
          System.out.println("Receive an order from a retailer.");
          // inMessage =
          // emailNotificationQueueReceiver.receive();

          // if( !(inMessage instanceof ObjectMessage) ) {
          // //End of message stream...
          // queueSession.commit();
          // break;
          // } else {
          // objectMessage = (ObjectMessage)inMessage;
          // System.out.println("Received ObjectMessage...");
          // System.out.println("ObjectMessage serializable object is of type "+objectMessage.getObject().getClass().toString());
          // }
          //
          // // Commit session.
          // queueSession.commit();
          // } catch(JMSException e) {
          // System.err.println("Vendor: " +
          // "JMSException occurred: " +
          // e.toString());
          // e.printStackTrace();
          // queueSession.rollback();
          // System.err.println("  Vendor: rolled " +
          // "back transaction 1");
          // }

          // Wait till listener gets back with consignment.
          notificationMessageListener.monitor.waitTillDone();
          if (notificationMessageListener.monitor.done) {
            System.out.println("Notification has been received...");
            break;
          }
        }

        // System.out.println("Ending from try section...");
      } catch (JMSException jms_ex) {
        System.err.println("Vendor: Exception " + "occurred: " + jms_ex.toString());
        jms_ex.printStackTrace();
      } finally {
        if (queueConnection != null) {
          try {
            System.out.println("Closing Queue Connection...");
            queueConnection.close();
            System.out.println("Queue Connection closed...");
          } catch (JMSException e) {
          }
        }
      }
    }
  }

  // super.run();

  public static void main(String[] args) {
    System.out.println("Testing Notification Handler...");
    NotificationHandler nh = new NotificationHandler();
    nh.start();
    try {
      nh.join();
    } catch (InterruptedException int_ex) {

    }

    System.out.println("Done testing notification handler");
    System.exit(0);
  }
}
