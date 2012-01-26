package com.tscp.mvne.logger;

import org.apache.log4j.Logger;

public class TscpmvneLogger {
  private static final Logger logger = Logger.getLogger("tscpmvneLogger");

  public TscpmvneLogger() {
    logger.info("TruConnect Logging services have been initialized");
  }

  public void debug(String message) {
    logger.debug(message);
  }

  public void trace(String message) {
    logger.trace(message);
  }

  public void info(String message) {
    logger.info(message);
  }

  public void warn(String message) {
    logger.warn(message);
  }

  public void warn(String message, Throwable throwable) {
    logger.warn(message, throwable);
  }

  public void error(String message) {
    logger.error(message);
  }

  public void fatal(String message) {
    logger.fatal(message);
  }

}
