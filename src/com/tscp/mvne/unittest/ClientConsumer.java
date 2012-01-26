package com.tscp.mvne.unittest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.tscp.mvne.notification.dao.EmailNotification;

public class ClientConsumer {

  public ClientConsumer() {

  }

  public void init() {
    try {
      Context ctx = new InitialContext();

      ConnectionFactory cf1 = (ConnectionFactory) ctx.lookup("queue/TestJMS");

      Connection conn = cf1.createConnection();

      Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

      Destination dest = (Queue) ctx.lookup("jms/Destination");

      MessageConsumer consumer = session.createConsumer(dest);

      conn.start();
      long timeout = 3000;
      long sleep = 0;
      final long MAX_RUN_TIME = 30000;
      while (sleep <= MAX_RUN_TIME) {
        Message msg = consumer.receive(timeout);
        if (msg != null) {
          if (msg instanceof TextMessage) {
            System.out.println("Message Text :: " + ((TextMessage) msg).getText());
          } else if (msg instanceof ObjectMessage) {
            ObjectMessage objMsg = (ObjectMessage) msg;
            if (objMsg.getObject() instanceof EmailNotification) {
              EmailNotification notification = (EmailNotification) objMsg.getObject();
              System.out.println("EmailNotification received...");
              System.out.println("Template :: " + notification.getTemplate());
            }
          }
        }

        System.out.println("Pausing 5 secs...");
        sleep += 5000;
        Thread.sleep(sleep);
      }

      System.out.println("Closing session and connection...");
      session.close();
      conn.close();
    } catch (JMSException jms_ex) {
      jms_ex.printStackTrace();
    } catch (NamingException naming_ex) {
      naming_ex.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("Starting consumer client");
    ClientConsumer consumer = new ClientConsumer();
    consumer.init();
  }

}