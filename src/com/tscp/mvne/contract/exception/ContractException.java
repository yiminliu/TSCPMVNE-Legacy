package com.tscp.mvne.contract.exception;

import com.tscp.mvne.exception.MVNEException;

public class ContractException extends MVNEException {
  private static final long serialVersionUID = 1464376120169337740L;

  public ContractException() {
    super();
  }

  public ContractException(String message) {
    super(message);
  }

  public ContractException(String methodname, String message) {
    super(methodname, message);
  }
}
