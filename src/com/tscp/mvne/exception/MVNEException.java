package com.tscp.mvne.exception;

import javax.xml.ws.WebServiceException;

public class MVNEException extends WebServiceException {
  private static final long serialVersionUID = -1984305003999836500L;
  /**
   * Value that can be referenced in the database with the exception transaction
   * and parameters
   */
  private int transactionid;
  private String methodname;

  public MVNEException() {
    super();
  }

  public MVNEException(String message, Throwable t) {
    super(message, t);
  }

  public MVNEException(String message) {
    super(message);
  }

  public MVNEException(String methodname, String message) {
    this(message);
    setMethodname(methodname);
  }

  public int getTransactionid() {
    return transactionid;
  }

  public void setTransactionid(int transactionid) {
    this.transactionid = transactionid;
  }

  public String getMethodname() {
    return methodname;
  }

  public void setMethodname(String methodname) {
    this.methodname = methodname;
  }

}
