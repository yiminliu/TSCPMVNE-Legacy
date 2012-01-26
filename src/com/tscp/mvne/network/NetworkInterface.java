package com.tscp.mvne.network;

import java.util.List;

public interface NetworkInterface {

  public static final String DEVICE_STATUS_ACTIVE = "A";
  public static final String DEVICE_STATUS_CANCELLED = "C";
  public static final String DEVICE_STATUS_SUSPENDED = "S";

  public static final int ESN_DEC_LENGTH = 11;
  public static final int ESN_HEX_LENGTH = 8;

  public static final int MEID_DEC_LENGTH = 18;
  public static final int MEID_HEX_LENGTH = 14;

  public NetworkInfo getNetworkInfo(String esn, String mdn) throws NetworkException;

  public NetworkInfo reserveMDN(String csa, String priceplan, List<String> soclist) throws NetworkException;

  public void activateMDN(NetworkInfo networkinfo) throws NetworkException;

  public void suspendService(NetworkInfo networkinfo) throws NetworkException;

  public void restoreService(NetworkInfo networkinfo) throws NetworkException;

  public void disconnectService(NetworkInfo networkinfo) throws NetworkException;

  public void swapESN(NetworkInfo oldnetworkinfo, NetworkInfo newnetworkinfo) throws NetworkException;

}
