package com.tscp.mvne.billing;

import java.util.Date;

public class ServiceInstance {
  private int subscrno;
  private int externalidtype;
  private String externalid;
  private Date activedate;
  private Date inactivedate;

  public ServiceInstance() {
    // do nothing
  }

  public int getSubscrno() {
    return subscrno;
  }

  public void setSubscrno(int subscrno) {
    this.subscrno = subscrno;
  }

  public int getExternalidtype() {
    return externalidtype;
  }

  public void setExternalidtype(int externalidtype) {
    this.externalidtype = externalidtype;
  }

  public String getExternalid() {
    return externalid;
  }

  public void setExternalid(String externalid) {
    this.externalid = externalid;
  }

  public Date getActivedate() {
    return activedate;
  }

  public void setActivedate(Date activedate) {
    this.activedate = activedate;
  }

  public Date getInactivedate() {
    return inactivedate;
  }

  public void setInactivedate(Date inactivedate) {
    this.inactivedate = inactivedate;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("ServiceInstance Object ...");
    sb.append(" \n");
    sb.append("SubscrNo         :: " + getSubscrno());
    sb.append(" \n");
    sb.append("ExternalIdType   :: " + getExternalidtype());
    sb.append(" \n");
    sb.append("ExternalId       :: " + getExternalid());
    sb.append(" \n");
    sb.append("ActiveDate       :: " + getActivedate());
    sb.append(" \n");
    sb.append("InactiveDate     :: " + getInactivedate());
    return sb.toString();
  }

  public String toFormattedStrng() {
    // TODO
    return toString();
  }
}
