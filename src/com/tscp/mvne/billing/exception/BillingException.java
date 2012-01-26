package com.tscp.mvne.billing.exception;

import com.tscp.mvne.exception.MVNEException;

public class BillingException extends MVNEException {
  private static final long serialVersionUID = 1L;

  private int accountno;
  private String externalid;

  public BillingException() {
    super();
  }

  public BillingException(String message) {
    super(message);
  }

  public BillingException(String methodname, String message) {
    super(methodname, message);
  }

  public int getAccountno() {
    return accountno;
  }

  public void setAccountno(int accountno) {
    this.accountno = accountno;
  }

  public String getExternalid() {
    return externalid;
  }

  public void setExternalid(String externalid) {
    this.externalid = externalid;
  }

}