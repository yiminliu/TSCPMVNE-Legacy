package com.tscp.mvne.customer.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.CustomerException;
import com.tscp.mvne.hibernate.HibernateUtil;

@SuppressWarnings("unchecked")
public class CustAddress implements Serializable {
  private static final long serialVersionUID = 1L;

  private int addressId;
  private int custId;

  private String addressLabel;
  private String address1;
  private String address2;
  private String address3;
  private String city;
  private String state;
  private String zip;

  private String isDefault;

  public int getAddressId() {
    return addressId;
  }

  public void setAddressId(int addressId) {
    this.addressId = addressId;
  }

  public int getCustId() {
    return custId;
  }

  public void setCustId(int custId) {
    this.custId = custId;
  }

  public String getAddressLabel() {
    return addressLabel;
  }

  public void setAddressLabel(String addressLabel) {
    this.addressLabel = addressLabel;
  }

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public String getAddress3() {
    return address3;
  }

  public void setAddress3(String address3) {
    this.address3 = address3;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(String isDefault) {
    this.isDefault = isDefault;
  }

  public void save() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    String procName = "";
    Query q = null;
    if (getAddressId() == 0) {
      procName = "ins_cust_address";
      q = session.getNamedQuery(procName);
    } else {
      procName = "upd_cust_address";
      q = session.getNamedQuery(procName);
      q.setParameter("in_address_id", getAddressId());
    }

    q.setParameter("in_cust_id", getCustId());
    q.setParameter("in_address1", getAddress1());
    q.setParameter("in_address2", getAddress2());
    q.setParameter("in_address3", getAddress3());
    q.setParameter("in_city", getCity());
    q.setParameter("in_state", getState());
    q.setParameter("in_zip", getZip());
    q.setParameter("in_address_label", getAddressLabel());
    q.setParameter("in_is_default", getIsDefault());

    List<GeneralSPResponse> generalSPResponseList = q.list();
    if (generalSPResponseList != null && generalSPResponseList.size() > 0) {
      for (GeneralSPResponse generalSPResponse : generalSPResponseList) {
        if (generalSPResponse.getStatus().equals("Y")) {
          setAddressId(generalSPResponse.getMvnemsgcode());
          session.getTransaction().commit();
        }
      }
    } else {
      session.getTransaction().rollback();
    }
  }

  public void delete() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("del_cust_address");
    q.setParameter("in_cust_id", getCustId());
    q.setParameter("in_address_id", getAddressId());

    List<GeneralSPResponse> generalSPResponseList = q.list();
    if (generalSPResponseList != null && generalSPResponseList.size() > 0) {
      for (GeneralSPResponse generalSPResponse : generalSPResponseList) {
        if (!generalSPResponse.getStatus().equals("Y")) {
          session.getTransaction().rollback();
          throw new CustomerException("Error deleting address..." + generalSPResponse.getMvnemsg());
        }
      }
      session.getTransaction().commit();
    } else {
      session.getTransaction().rollback();
    }

  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CustAddress Object ...");
    sb.append(" \n");
    sb.append("addressId        :: " + getAddressId());
    sb.append(" \n");
    sb.append("custId           :: " + getCustId());
    sb.append(" \n");
    sb.append("AddressLabel     :: " + getAddressLabel());
    sb.append(" \n");
    sb.append("Address1         :: " + getAddress1());
    sb.append(" \n");
    sb.append("Address2         :: " + getAddress2());
    sb.append(" \n");
    sb.append("Address3         :: " + getAddress3());
    sb.append(" \n");
    sb.append("City             :: " + getCity());
    sb.append(" \n");
    sb.append("State            :: " + getState());
    sb.append(" \n");
    sb.append("Zip              :: " + getZip());
    return sb.toString();
  }

}
