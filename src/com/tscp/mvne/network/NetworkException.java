package com.tscp.mvne.network;

import com.tscp.mvne.exception.MVNEException;

public class NetworkException extends MVNEException {
  private static final long serialVersionUID = -7412354082633059506L;
  NetworkInfo networkinfo;

  public NetworkException() {
    super();
  }

  public NetworkException(String message) {
    super(message);
  }

  public NetworkException(String message, Throwable t) {
    super(message, t);
  }

  public NetworkException(String methodname, String message) {
    super(methodname, message);
  }

  public void setNetworkinfo(NetworkInfo networkinfo) {
    this.networkinfo = networkinfo;
  }

  public NetworkInfo getNetworkinfo() {
    return networkinfo;
  }
}
