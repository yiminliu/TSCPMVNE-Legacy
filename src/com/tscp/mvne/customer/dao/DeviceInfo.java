package com.tscp.mvne.customer.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.DeviceException;
import com.tscp.mvne.hibernate.HibernateUtil;

@SuppressWarnings("unchecked")
public class DeviceInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  private int deviceId;
  private int custId;
  private int accountNo;
  private String deviceLabel;
  private String deviceValue;
  private int deviceStatusId;
  private String deviceStatus;
  private Date modDate;
  private Date effectiveDate;
  private Date expirationDate;
  private DeviceAssociation deviceAssociation;

  public DeviceInfo() {
    setDeviceStatus(DeviceStatus.DESC_UNKNOWN);
    setDeviceStatusId(DeviceStatus.ID_UNKNOWN);
    // setModDate(new Date());
    // setEffectiveDate(new Date());
    // setExpirationDate(new Date());
  }

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  public int getCustId() {
    return custId;
  }

  public void setCustId(int custId) {
    this.custId = custId;
  }

  public int getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(int accountNo) {
    this.accountNo = accountNo;
  }

  public String getDeviceLabel() {
    return deviceLabel;
  }

  public void setDeviceLabel(String deviceLabel) {
    this.deviceLabel = deviceLabel;
  }

  public String getDeviceValue() {
    return deviceValue;
  }

  public void setDeviceValue(String deviceValue) {
    this.deviceValue = deviceValue;
  }

  public int getDeviceStatusId() {
    return deviceStatusId;
  }

  public void setDeviceStatusId(int deviceStatusId) {
    this.deviceStatusId = deviceStatusId;
  }

  public String getDeviceStatus() {
    return deviceStatus;
  }

  public void setDeviceStatus(String deviceStatus) {
    this.deviceStatus = deviceStatus;
  }

  public Date getModDate() {
    return modDate;
  }

  public void setModDate(Date modDate) {
    this.modDate = modDate;
  }

  public Date getEffectiveDate() {
    return effectiveDate;
  }

  public void setEffectiveDate(Date effectiveDate) {
    this.effectiveDate = effectiveDate;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  public DeviceAssociation getDeviceAssociation() {
    return deviceAssociation;
  }

  public void setDeviceAssociation(DeviceAssociation deviceAssociation) {
    this.deviceAssociation = deviceAssociation;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("DeviceInfo Object ...");
    sb.append(" \n");
    sb.append("deviceId         :: " + getDeviceId());
    sb.append(" \n");
    sb.append("custId           :: " + getCustId());
    sb.append(" \n");
    sb.append("deviceLabel      :: " + getDeviceLabel());
    sb.append(" \n");
    sb.append("deviceValue      :: " + getDeviceValue());
    sb.append(" \n");
    sb.append("deviceStatus     :: " + getDeviceStatus());
    sb.append(" \n");
    sb.append("accountNo        :: " + getAccountNo());
    return sb.toString();
  }

  public void save() throws DeviceException {
    if (getCustId() <= 0) {
      throw new DeviceException("Customer Id has not been set");
    }
    if (getDeviceLabel() == null || getDeviceLabel().isEmpty()) {
      throw new DeviceException("Device Label cannot be empty");
    }
    String methodName = "";
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    Query q;
    if (getDeviceId() == 0) {
      // Insert
      methodName = "ins_device_info";
      q = session.getNamedQuery(methodName);
      q.setParameter("in_account_no", getAccountNo());
    } else {
      // update
      methodName = "upd_device_info";
      q = session.getNamedQuery(methodName);
      q.setParameter("in_device_id", getDeviceId());
      q.setParameter("in_device_status_id", getDeviceStatusId());
      q.setParameter("in_eff_date", getEffectiveDate() == null ? "" : getEffectiveDate());
      q.setParameter("in_exp_date", getExpirationDate() == null ? "" : getExpirationDate());
    }
    q.setParameter("in_cust_id", getCustId());
    q.setParameter("in_device_label", getDeviceLabel());
    q.setParameter("in_device_value", getDeviceValue());
    List<GeneralSPResponse> generalSPResponseList = q.list();

    if (generalSPResponseList != null) {
      for (GeneralSPResponse generalSPResponse : generalSPResponseList) {
        if (generalSPResponse.getStatus().equals("Y")) {
          setDeviceId(generalSPResponse.getMvnemsgcode());
        } else {
          session.getTransaction().rollback();
          throw new DeviceException(generalSPResponse.getMvnemsg());
        }
      }
    } else {
      session.getTransaction().rollback();
      throw new DeviceException("Error Saving Device information...");
    }

    session.getTransaction().commit();

  }

  public void delete() {
    if (getDeviceId() <= 0) {
      throw new DeviceException("Device ID cannot be empty");
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("del_device_info");
    q.setParameter("in_cust_id", getCustId());
    q.setParameter("in_device_id", getDeviceId());

    List<GeneralSPResponse> generalSPResponseList = q.list();

    if (generalSPResponseList != null) {
      for (GeneralSPResponse generalSPResponse : generalSPResponseList) {
        if (generalSPResponse.getStatus().equals("Y")) {
          setDeviceId(generalSPResponse.getMvnemsgcode());
        } else {
          session.getTransaction().rollback();
          throw new DeviceException(generalSPResponse.getMvnemsg());
        }
      }
    } else {
      session.getTransaction().rollback();
      throw new DeviceException("Error Saving Device information...");
    }

    session.getTransaction().commit();
  }
}
