package com.tscp.mvne.payment.dao;

import java.io.Serializable;

public class PaymentUnitResponse implements Serializable {

  public static final String SUCCESSFUL_TRANSACTION = "0";

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private String confcode;
  private String confdescr;

  private String transid;

  private String authcode;
  private String cvvcode;

  public PaymentUnitResponse() {

  }

  public String getConfcode() {
    return confcode;
  }

  public void setConfcode(String confcode) {
    this.confcode = confcode;
  }

  public String getConfdescr() {
    return confdescr;
  }

  public void setConfdescr(String confdescr) {
    this.confdescr = confdescr;
  }

  public String getTransid() {
    return transid;
  }

  public void setTransid(String transid) {
    this.transid = transid;
  }

  public String getAuthcode() {
    return authcode;
  }

  public void setAuthcode(String authcode) {
    this.authcode = authcode;
  }

  public String getCvvcode() {
    return cvvcode;
  }

  public void setCvvcode(String cvvcode) {
    this.cvvcode = cvvcode;
  }

  public String getConfirmationString() {
    return "CC" + getConfcode() + "TID" + getTransid();
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("PaymentResponse Object ...");
    sb.append(" \n");
    sb.append("ConfCode         :: " + getConfcode());
    sb.append(" \n");
    sb.append("Confdescr        :: " + getConfdescr());
    sb.append(" \n");
    sb.append("TransId          :: " + getTransid());
    sb.append(" \n");
    sb.append("AuthCode         :: " + getAuthcode());
    sb.append(" \n");
    sb.append("CvvCode          :: " + getCvvcode());
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PaymentUnitResponse) {
      PaymentUnitResponse pur = (PaymentUnitResponse) obj;
      if (pur.getAuthcode().equals(getAuthcode()) && pur.getConfcode().equals(getConfcode())
          && pur.getConfdescr().equals(getConfdescr()) && pur.getCvvcode().equals(getCvvcode())
          && pur.getTransid().equals(getTransid())) {
        return true;
      }
    }
    return super.equals(obj);
  }
}
