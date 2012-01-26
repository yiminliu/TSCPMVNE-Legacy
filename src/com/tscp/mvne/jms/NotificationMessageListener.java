package com.tscp.mvne.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.QueueSession;

import com.tscp.mvne.notification.NotificationSystemImpl;
import com.tscp.mvne.notification.dao.EmailNotification;

public class NotificationMessageListener implements MessageListener {
  private final QueueSession session;

  final JMSHelper.DoneLatch monitor = new JMSHelper.DoneLatch();

  /**
   * Constructor. Instantiates the message listener with the session of the
   * consuming class (the vendor).
   * 
   * 
   * @param session
   */
  public NotificationMessageListener(QueueSession session) {
    this.session = session;
  }

  /**
   * Send the notification from this point
   * 
   * @param Message
   */
  @Override
  public void onMessage(Message message) {
    System.out.println("Hello world...Inside onMessage of NotificationMessageListener!");
    if (!(message instanceof ObjectMessage)) {
      System.out.println("setting to all done...");
      monitor.allDone();
      try {
        session.commit();
      } catch (JMSException je) {
      }
      return;
    } else {
      System.out.println("We have an object message");
      try {
        ObjectMessage objectMessage = (ObjectMessage) message;
        if (objectMessage.getObject() instanceof EmailNotification) {
          System.out.println("Email Notification retrieved");

          EmailNotification emailNotification = (EmailNotification) objectMessage.getObject();
          System.out.println("Attempting to send email...Email Template :: "
              + emailNotification.getTemplate().toString());
          NotificationSystemImpl notificationSystemImpl = new NotificationSystemImpl();
          notificationSystemImpl.sendNotification(emailNotification);

          System.out.println("Email has been sent...");
        } else {
          if (objectMessage.getObject() != null) {
            System.out.println("Unknown Object within ObjectMessage...ObjectMessage.getObject type is "
                + objectMessage.getObject().getClass());
          }
        }
      } catch (JMSException je) {

      }
    }

    // try {
    // System.out.println("Committing session...");
    // session.commit();
    // } catch (JMSException je) {
    // je.printStackTrace();
    // }
  }

}
