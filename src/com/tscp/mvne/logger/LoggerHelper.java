package com.tscp.mvne.logger;

import java.util.List;

public class LoggerHelper {
  private static final TscpmvneLogger logger = new TscpmvneLogger();
  private String methodName;

  public LoggerHelper(String methodName) {
    this.methodName = methodName;
  }

  public LoggerHelper(String methodName, Object... parameters) {
    this.methodName = methodName;
    logMethod(parameters);
  }

  public void logMethodExit() {
    logger.info("Exiting " + methodName);
  }

  public void logMethodReturn(Object object) {
    logger.info("Returning " + object.toString());
    logger.info("Exiting " + methodName);
  }

  public void logMethod(List<Object> parameters) {
    logger.info("Begin " + methodName);
    int counter = 0;
    String indent = "****";
    for (Object object : parameters) {
      logger.info("ARG" + counter);
      logger.info(indent + (object == null ? "null" : object.toString()));
      ++counter;
    }
  }

  public void logMethod(Object... parameters) {
    logger.info("Begin " + methodName);
    int counter = 0;
    String indent = "****";
    for (Object object : parameters) {
      logger.info("ARG" + counter);
      logger.info(indent + (object == null ? "null" : object.toString()));
      ++counter;
    }
  }

}
