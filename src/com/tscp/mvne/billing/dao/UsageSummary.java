package com.tscp.mvne.billing.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.billing.Usage;
import com.tscp.mvne.billing.api.BillingUsage;
import com.tscp.mvne.hibernate.HibernateUtil;

/**
 * @author Dan
 * 
 */
public class UsageSummary extends Usage implements Serializable {
  private static final long serialVersionUID = 1L;

  private String externalid;

  private String dollarusage;
  private String rate;

  private String mbs;

  public UsageSummary() {
    externalid = "";
    dollarusage = "0";
    rate = "0";
    mbs = "0";
  }

  public String getExternalid() {
    return externalid;
  }

  public void setExternalid(String externalid) {
    this.externalid = externalid;
  }

  public String getDollarusage() {
    return dollarusage;
  }

  public void setDollarusage(String dollarusage) {
    this.dollarusage = dollarusage;
  }

  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  public String getMbs() {
    return mbs;
  }

  public void setMbs(String mbs) {
    this.mbs = mbs;
  }

  public String getUsageAmount() {
    return getMbs() + "MBs";
  }

  public String getUsageType() {
    return "Data";
  }

  public void clone(UsageSummary dataUsage) {
    setExternalid(dataUsage.getExternalid());
    setDollarusage(dataUsage.getDollarusage());
    setRate(dataUsage.getRate());
    setMbs(dataUsage.getMbs());
  }

  public void load() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_unbilled_data_mbs");
    q.setParameter("in_username", "username");
    q.setParameter("in_external_id", getExternalid());

    @SuppressWarnings("unchecked")
    List<UsageSummary> dataList = q.list();

    if (dataList != null && dataList.size() >= 1) {
      clone(dataList.get(0));
    }

    session.getTransaction().rollback();

  }

  public void load(BillingUsage billingUsage) {
    setDollarusage(billingUsage.getDollarUsage());
    setExternalid(billingUsage.getExternalId());
    setMbs(billingUsage.getUsage());
    setRate(billingUsage.getRate());
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    // sb.append("ExternalID :: "+getExternalid()+
    // " DollarUsage :: "+getDollarusage()+
    // " Rate :: "+getRate()+
    // " Mbs :: "+getMbs()+
    // " UsageAmount :: "+getUsageAmount() +
    // " UsageType :: "+getUsageType()
    // );
    sb.append("DataUsage Object ...");
    sb.append(" \n");
    sb.append("ExternalID       :: " + getExternalid());
    sb.append(" \n");
    sb.append("DollarUsage      :: " + getDollarusage());
    sb.append(" \n");
    sb.append("Rate             :: " + getRate());
    sb.append(" \n");
    sb.append("Mbs              :: " + getMbs());
    sb.append(" \n");
    sb.append("UsageAmount      :: " + getUsageAmount());
    sb.append(" \n");
    sb.append("UsageType        :: " + getUsageType());
    return sb.toString();
  }

}
