package com.tscp.mvne.payment;

import com.tscp.mvne.exception.MVNEException;

public class PaymentException extends MVNEException {
  private static final long serialVersionUID = 8144586037731331146L;
  int transactionid;

  public PaymentException() {
    super();
  }

  public PaymentException(String message) {
    super(message);
  }

  public PaymentException(String methodname, String message) {
    super(methodname, message);
  }

  public void setTransactionid(int transactionid) {
    this.transactionid = transactionid;
  }

  public int getTransactionid() {
    return transactionid;
  }

}
