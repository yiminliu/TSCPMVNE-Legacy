package com.tscp.mvne.exception;

public class DaoException extends MVNEException {
  private static final long serialVersionUID = 1464376120169337740L;

  public DaoException() {
    super();
  }

  public DaoException(String message) {
    super(message);
  }

  public DaoException(String methodname, String message) {
    super(methodname, message);
  }
}