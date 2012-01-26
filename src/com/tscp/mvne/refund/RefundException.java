package com.tscp.mvne.refund;

import com.tscp.mvne.exception.MVNEException;

public class RefundException extends MVNEException {
  private static final long serialVersionUID = 4018439343134758689L;

  public RefundException() {
    super();
  }

  public RefundException(String message) {
    super(message);
  }

  public RefundException(String methodname, String message) {
    super(methodname, message);
  }
}
