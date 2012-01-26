package com.tscp.mvne.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
  private static final SessionFactory sessionFactory = buildSessionFactory();

  private static SessionFactory buildSessionFactory() {
    try {
      return new Configuration().configure("hibernate.tscpmvne.cfg.xml").buildSessionFactory();
    } catch (Throwable ex) {
      System.err.println("Initial SessionFactory creation failed..." + ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * Used to close an open session and catch any exceptions during the process.
   * 
   * @param session
   * @throws RuntimeException
   */
  public static void closeSession(Session session) throws RuntimeException {
    if (session.isOpen()) {
      try {
        session.close();
      } catch (HibernateException e) {
        throw new RuntimeException("Error closing session. " + e.getMessage());
      }
    }
  }

  /**
   * Used to commit transactions and catch any exceptions during the process.
   * 
   * @param transaction
   * @throws RuntimeException
   */
  public static void commitTransaction(Transaction transaction) throws RuntimeException {
    if (transaction != null && transaction.isActive()) {
      try {
        transaction.commit();
      } catch (HibernateException e) {
        throw new RuntimeException("Error commiting back transaction. " + e.getMessage());
      }
    }
  }

  /**
   * Used to rollback a transaction and catch any exceptions during the process.
   * 
   * @param transaction
   * @throws RuntimeException
   */
  public static void rollbackTransaction(Transaction transaction) throws RuntimeException {
    if (transaction != null && transaction.isActive()) {
      try {
        transaction.rollback();
      } catch (HibernateException e) {
        throw new RuntimeException("Error rolling back transaction. " + e.getMessage());
      }
    }
  }

}
