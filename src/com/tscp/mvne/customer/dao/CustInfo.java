package com.tscp.mvne.customer.dao;

import java.io.Serializable;

public class CustInfo implements Serializable {
  private static final long serialVersionUID = 4306347069162398894L;

  private int custId;

  private String firstName;
  private String middleName;
  private String lastName;

  public CustInfo() {
    custId = 0;
    firstName = "";
    middleName = "";
    lastName = "";
  }

  public int getCustId() {
    return custId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setCustId(int custId) {
    this.custId = custId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public int hashCode() {
    return getCustId();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CustInfo) {
      CustInfo custInfo = (CustInfo) obj;
      return (custInfo.getCustId() == getCustId() && custInfo.getFirstName().equals(getFirstName())
          && custInfo.getMiddleName().equals(getMiddleName()) && custInfo.getLastName().equals(getLastName()));
    } else {
      return super.equals(obj);
    }
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CustInfo Object ...");
    sb.append(" \n");
    sb.append("CustId           :: " + getCustId());
    sb.append(" \n");
    sb.append("FirstName        :: " + getFirstName());
    sb.append(" \n");
    sb.append("MiddleName       :: " + getMiddleName());
    sb.append(" \n");
    sb.append("LastName         :: " + getLastName());
    return sb.toString();
  }
}
