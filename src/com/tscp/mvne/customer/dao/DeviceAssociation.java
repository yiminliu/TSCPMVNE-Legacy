package com.tscp.mvne.customer.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.DeviceException;
import com.tscp.mvne.hibernate.HibernateUtil;

@SuppressWarnings("unchecked")
public class DeviceAssociation implements Serializable {
  private static final long serialVersionUID = 1L;

  private int deviceId;
  private int accountNo;
  private String externalId;
  private int subscrNo;
  private Date activeDate;
  private Date inactiveDate;
  private Date modDate;

  public DeviceAssociation() {
    activeDate = new Date();
    inactiveDate = new Date();
    modDate = new Date();
  }

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  public int getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(int accountNo) {
    this.accountNo = accountNo;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public int getSubscrNo() {
    return subscrNo;
  }

  public void setSubscrNo(int subscrNo) {
    this.subscrNo = subscrNo;
  }

  public Date getActiveDate() {
    return activeDate;
  }

  public void setActiveDate(Date activeDate) {
    this.activeDate = activeDate;
  }

  public Date getInactiveDate() {
    return inactiveDate;
  }

  public void setInactiveDate(Date inactiveDate) {
    this.inactiveDate = inactiveDate;
  }

  public Date getModDate() {
    return modDate;
  }

  public void setModDate(Date modDate) {
    this.modDate = modDate;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("UsageDetail Object ...");
    sb.append(" \n");
    sb.append("deviceId         :: " + getDeviceId());
    sb.append(" \n");
    sb.append("accountNo        :: " + getAccountNo());
    sb.append(" \n");
    sb.append("externalId       :: " + getExternalId());
    sb.append(" \n");
    sb.append("subscrNo         :: " + getSubscrNo());
    sb.append(" \n");
    sb.append("activeDate       :: " + getActiveDate());
    sb.append(" \n");
    sb.append("inactiveDate     :: " + getInactiveDate());
    return sb.toString();
  }

  public void save() throws DeviceException {
    if (getDeviceId() <= 0) {
      throw new DeviceException("Device ID Value not set");
    }
    if (getSubscrNo() <= 0) {
      throw new DeviceException("Subscriber Number has not been set");
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("ins_device_assoc_map");
    q.setParameter("in_device_id", getDeviceId());
    q.setParameter("in_subscr_no", getSubscrNo());

    List<GeneralSPResponse> generalSPResponseList = q.list();

    if (generalSPResponseList != null) {
      for (GeneralSPResponse generalSPResponse : generalSPResponseList) {
        if (!generalSPResponse.getStatus().equals("Y")) {
          session.getTransaction().rollback();
          throw new DeviceException(generalSPResponse.getMvnemsg());
        }
      }
    } else {
      session.getTransaction().rollback();
      throw new DeviceException("Error inserting Device Association Map....Nothing was returned.");
    }

    session.getTransaction().commit();

  }

}
