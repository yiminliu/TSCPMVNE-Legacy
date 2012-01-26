package com.tscp.mvne.customer;

import com.tscp.mvne.exception.MVNEException;

public class CustomerException extends MVNEException {
  private static final long serialVersionUID = -8032831785338560217L;
  int transactionid;
  int custid;

  public CustomerException() {
    super();
  }

  public CustomerException(String message) {
    super(message);
  }

  public CustomerException(String methodname, String message) {
    super(methodname, message);
  }

  public void setTransactionid(int transactionid) {
    this.transactionid = transactionid;
  }

  public int getTransactionid() {
    return transactionid;
  }

  public void setCustid(int custid) {
    this.custid = custid;
  }

  public int getCustid() {
    return custid;
  }

}
