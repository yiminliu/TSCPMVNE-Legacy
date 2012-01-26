package com.tscp.mvne.network;

import java.io.Serializable;

public class NetworkInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  private String esnmeiddec;
  private String esnmeidhex;
  private String mdn;
  private String msid;
  private String effectivedate;
  private String effectivetime;
  private String expirationdate;
  private String expirationtime;
  private String status;

  public NetworkInfo() {
    // do nothing
  }

  public String getEsnmeiddec() {
    return esnmeiddec;
  }

  public void setEsnmeiddec(String esnmeiddec) {
    this.esnmeiddec = esnmeiddec;
  }

  public String getEsnmeidhex() {
    return esnmeidhex;
  }

  public void setEsnmeidhex(String esnmeidhex) {
    this.esnmeidhex = esnmeidhex;
  }

  public String getMdn() {
    return mdn;
  }

  public void setMdn(String mdn) {
    this.mdn = mdn;
  }

  public String getMsid() {
    return msid;
  }

  public void setMsid(String msid) {
    this.msid = msid;
  }

  public String getEffectivedate() {
    return effectivedate;
  }

  public void setEffectivedate(String effectivedate) {
    this.effectivedate = effectivedate;
  }

  public String getEffectivetime() {
    return effectivetime;
  }

  public void setEffectivetime(String effectivetime) {
    this.effectivetime = effectivetime;
  }

  public String getExpirationdate() {
    return expirationdate;
  }

  public void setExpirationdate(String expirationdate) {
    this.expirationdate = expirationdate;
  }

  public String getExpirationtime() {
    return expirationtime;
  }

  public void setExpirationtime(String expirationtime) {
    this.expirationtime = expirationtime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NetworkInfo) {
      NetworkInfo tempObj = (NetworkInfo) obj;
      if (tempObj.getMdn().equals(getMdn()) && tempObj.getMsid().equals(getMsid())
          && tempObj.getEsnmeiddec().equals(getEsnmeiddec()) && tempObj.getEffectivedate().equals(getEffectivedate())
          && tempObj.getEffectivetime().equals(getEffectivetime())
          && tempObj.getExpirationdate().equals(getExpirationdate())
          && tempObj.getExpirationtime().equals(getExpirationtime()) && tempObj.getEsnmeidhex().equals(getEsnmeidhex())
          && tempObj.getStatus().equals(getStatus())) {
        return true;
      }
    }
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  public String toFormattedString() {
    StringBuffer sb = new StringBuffer();
    sb.append("--NetworkInfo--").append(" \n");
    sb.append("  Status          = " + getStatus()).append(" \n");
    sb.append("  Mdn             = " + getMdn()).append(" \n");
    sb.append("  Msid            = " + getMsid()).append(" \n");
    sb.append("  EsnMeid Dec     = " + getEsnmeiddec()).append(" \n");
    sb.append("  EsnMeid Hex     = " + getEsnmeidhex()).append(" \n");
    sb.append("  Effective Date  = " + getEffectivedate()).append(" \n");
    sb.append("  Effective Time  = " + getEffectivetime()).append(" \n");
    sb.append("  Expiration Date = " + getExpirationdate()).append(" \n");
    sb.append("  Expiration Time = " + getExpirationtime());
    return sb.toString();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("status=" + getStatus()).append(", ");
    sb.append("mdn=" + getMdn()).append(", ");
    sb.append("EsnMeidDec=" + getEsnmeiddec()).append(", ");
    sb.append("EsnMeidHex=" + getEsnmeidhex());
    return sb.toString();
  }

}
