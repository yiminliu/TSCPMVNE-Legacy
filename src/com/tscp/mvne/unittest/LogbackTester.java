package com.tscp.mvne.unittest;

import com.tscp.mvne.logger.TscpmvneLogger;

public class LogbackTester {
  static final TscpmvneLogger logger = new TscpmvneLogger();

  /**
   * @param args
   */
  public static void main(String[] args) {
    LogbackTester console = new LogbackTester();
    console.execute();
  }

  public LogbackTester() {
    System.out.println("logback2=" + getClass().getClassLoader().getResource("logback.xml"));
    System.out.println("logback2=" + Thread.currentThread().getContextClassLoader().getResource("logback.xml"));
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
