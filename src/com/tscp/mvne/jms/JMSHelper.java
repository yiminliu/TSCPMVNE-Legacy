package com.tscp.mvne.jms;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSHelper {

  public static final String QUEUE_USERNAME = "guest";
  public static final String QUEUE_PASSWORD = "guest";

  public static final String QUEUECONFAC = "queue/TestJMS";
  public static final String TOPICCONFAC = "TopicConnectionFactory";
  private static Context jndiContext = null;

  /**
   * Returns a QueueConnectionFactory object.
   * 
   * @return a QueueConnectionFactory object
   * @throws javax.naming.NamingException
   *           (or other exception) if name cannot be found
   */
  public static QueueConnectionFactory getQueueConnectionFactory() throws Exception {
    return (QueueConnectionFactory) jndiLookup(QUEUECONFAC);
  }

  /**
   * Returns a TopicConnectionFactory object.
   * 
   * @return a TopicConnectionFactory object
   * @throws javax.naming.NamingException
   *           (or other exception) if name cannot be found
   */
  public static TopicConnectionFactory getTopicConnectionFactory() throws Exception {
    return (TopicConnectionFactory) jndiLookup(TOPICCONFAC);
  }

  /**
   * Returns a Queue object.
   * 
   * @param name
   *          String specifying queue name
   * @param session
   *          a QueueSession object
   * 
   * @return a Queue object
   * @throws javax.naming.NamingException
   *           (or other exception) if name cannot be found
   */
  public static Queue getQueue(String name, QueueSession session) throws Exception {
    return (Queue) jndiLookup(name);
  }

  /**
   * Returns a Topic object.
   * 
   * @param name
   *          String specifying topic name
   * @param session
   *          a TopicSession object
   * 
   * @return a Topic object
   * @throws javax.naming.NamingException
   *           (or other exception) if name cannot be found
   */
  public static Topic getTopic(String name, TopicSession session) throws Exception {
    return (Topic) jndiLookup(name);
  }

  /**
   * Creates a JNDI API InitialContext object if none exists yet. Then looks up
   * the string argument and returns the associated object.
   * 
   * @param name
   *          the name of the object to be looked up
   * 
   * @return the object bound to name
   * @throws javax.naming.NamingException
   *           (or other exception) if name cannot be found
   */
  public static Object jndiLookup(String name) throws NamingException {
    Object obj = null;

    if (jndiContext == null) {
      try {
        jndiContext = new InitialContext();
      } catch (NamingException e) {
        System.err.println("Could not create JNDI API " + "context: " + e.toString());
        throw e;
      }
    }
    try {
      obj = jndiContext.lookup(name);
    } catch (NamingException e) {
      System.err.println("JNDI API lookup failed: " + e.toString());
      throw e;
    }
    return obj;
  }

  /**
   * Monitor class for asynchronous examples. Producer signals end of message
   * stream; listener calls allDone() to notify consumer that the signal has
   * arrived, while consumer calls waitTillDone() to wait for this notification.
   */
  static public class DoneLatch {
    boolean done = false;

    /**
     * Waits until done is set to true.
     */
    public void waitTillDone() {
      synchronized (this) {
        while (!done) {
          try {
            this.wait();
          } catch (InterruptedException ie) {
          }
        }
      }
    }

    /**
     * Sets done to true.
     */
    public void allDone() {
      synchronized (this) {
        done = true;
        this.notify();
      }
    }
  }

}
