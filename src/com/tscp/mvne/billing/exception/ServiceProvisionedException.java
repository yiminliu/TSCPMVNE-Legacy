package com.tscp.mvne.billing.exception;

public class ServiceProvisionedException extends BillingException {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public ServiceProvisionedException() {
    super();
  }

  public ServiceProvisionedException(String message) {
    super(message);
  }

  public ServiceProvisionedException(String methodname, String message) {
    super(methodname, message);
  }

  public int getAccountno() {
    return super.getAccountno();
  }

  public void setAccountno(int accountno) {
    super.setAccountno(accountno);
  }

  public String getExternalid() {
    return super.getExternalid();
  }

  public void setExternalid(String externalid) {
    super.setExternalid(externalid);
  }
}
