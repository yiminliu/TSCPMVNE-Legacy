package com.tscp.mvne.customer;

import java.io.Serializable;

import com.tscp.mvne.exception.MVNEException;

public class DeviceException extends MVNEException implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public DeviceException() {
    super();
  }

  public DeviceException(String message) {
    super(message);
  }

  public DeviceException(String methodname, String message) {
    super(methodname, message);
  }
}
