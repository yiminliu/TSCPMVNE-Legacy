package com.tscp.mvne.unittest;

import com.tscp.mvne.logger.TscpmvneLogger;

public class Log4jTester {
  static final TscpmvneLogger logger = new TscpmvneLogger();

  /**
   * @param args
   */
  public static void main(String[] args) {
    Log4jTester console = new Log4jTester();
    console.execute();
  }

  public Log4jTester() {
    System.out.println("log4j1=" + getClass().getClassLoader().getResource("log4j.xml"));
    System.out.println("log4j2=" + Thread.currentThread().getContextClassLoader().getResource("log4j.xml"));
  }

  public void execute() {
    // if (logger.isTraceEnabled()) {
    // logger.trace("Test: TRACE level message.");
    // }
    // if (logger.isDebugEnabled()) {
    // logger.debug("Test: DEBUG level message.");
    // }
    // if (logger.isInfoEnabled()) {
    // logger.info("Test: INFO level message.");
    // }
    // if (logger.isWarnEnabled()) {
    // logger.warn("Test: WARN level message.");
    // }
    // if (logger.isErrorEnabled()) {
    // logger.error("Test: ERROR level message.");
    // }
  }

}
