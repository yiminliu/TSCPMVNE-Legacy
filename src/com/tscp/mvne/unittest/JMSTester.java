package com.tscp.mvne.unittest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.tscp.mvne.notification.EmailTemplate;
import com.tscp.mvne.notification.dao.EmailNotification;

public class JMSTester {

  public <U> void foo(U u1, U u2, Integer i) {
    // do something
  }

  public void useFoo() {
    Object obj = null;
    String s = null;
    int i = 0;
    foo(obj, s, i);
  }

  private void sendMessage() {
    try {
      // Hashtable env = new Hashtable();
      // env.put(Context.APPLET, this);

      // System.getProperties().put(Context.APPLET, this);

      InitialContext ctx = new InitialContext();
      // DataSource tscpMvne = (DataSource)ctx.lookup("jdbc/TSCIVRT");
      // ConnectionFactory cf1 = (ConnectionFactory)
      // ctx.lookup("jms/QueueConnectionFactory");
      ConnectionFactory cf1 = (ConnectionFactory) ctx.lookup("queue/TestJMS");
      // ConnectionFactory cf1 = (ConnectionFactory)
      // ctx.lookup("jms/TestQueue");
      // QueueConnectionFactory cf1 = new
      // com.sun.messaging.QueueConnectionFactory();

      Connection conn = cf1.createConnection("guest", "guest");

      Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

      // ctx.lookup("jdbc/TSCPMVNE");
      // Destination dest = (Queue) ctx.lookup("queue/TestJMS");
      // Destination dest = (Queue) ctx.lookup("jms/TestQueue");
      Destination dest = (Queue) ctx.lookup("jms/Destination");

      MessageProducer producer = session.createProducer(dest);

      EmailNotification notification = new EmailNotification();
      notification.setTemplate(EmailTemplate.topup2);
      ObjectMessage message = session.createObjectMessage(notification);

      TextMessage textMsg = session.createTextMessage();
      textMsg.setText("Hello There!");

      producer.send(message);

      System.out.println("closing connections...");
      session.close();
      conn.close();
    } catch (JMSException jms_ex) {
      jms_ex.printStackTrace();
      System.exit(-2);
    } catch (NamingException naming_ex) {
      naming_ex.printStackTrace();
      // throw new ExceptionInInitializerError(naming_ex);
      System.exit(-1);
    }
  }

  public void test() {
    System.out.println("Test");
  }

  public static void main(String[] args) {
    System.out.println("Testing JMS");
    JMSTester sjt = new JMSTester();

    // sjt.sendMessage();
    sjt.test();

    System.out.println("Exiting");
    // System.exit(0);
  }
}
