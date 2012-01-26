package com.tscp.mvne.notification;

import com.tscp.mvne.exception.MVNEException;

public class NotificationException extends MVNEException {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  int transactionid;

  public NotificationException() {
    super();
  }

  public NotificationException(String message) {
    super(message);
  }

  public NotificationException(String methodname, String message) {
    super(methodname, message);
  }

  public void setTransactionid(int transactionid) {
    this.transactionid = transactionid;
  }

  public int getTransactionid() {
    return transactionid;
  }

}
