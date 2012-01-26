package com.tscp.mvne.payment.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.hibernate.HibernateUtil;
import com.tscp.mvne.payment.PaymentException;

@SuppressWarnings("unchecked")
public class CustPmtMap {

  private int paymentid;
  private int custid;

  private String paymenttype;
  private String paymentalias;

  private String isDefault;

  public CustPmtMap() {

  }

  public int getPaymentid() {
    return paymentid;
  }

  public void setPaymentid(int paymentid) {
    this.paymentid = paymentid;
  }

  public int getCustid() {
    return custid;
  }

  public void setCustid(int custid) {
    this.custid = custid;
  }

  public String getPaymenttype() {
    return paymenttype;
  }

  public void setPaymenttype(String paymenttype) {
    this.paymenttype = paymenttype;
  }

  public String getPaymentalias() {
    return paymentalias;
  }

  public void setPaymentalias(String paymentalias) {
    this.paymentalias = paymentalias;
  }

  public String getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(String isDefault) {
    this.isDefault = isDefault;
  }

  public void save() throws PaymentException {
    if (getPaymentalias() == null || getPaymentalias().trim().length() == 0) {
      throw new PaymentException("Please specify a payment alias");
    }
    if (!getIsDefault().equals("N") && !getIsDefault().equals("Y")) {
      throw new PaymentException("Invalid default value...valid values are N or Y");
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("ins_cust_pmt_map");
    q.setParameter("in_cust_id", getCustid());
    q.setParameter("in_pmt_id", getPaymentid());
    q.setParameter("in_pmt_type", getPaymenttype());
    q.setParameter("in_pmt_alias", getPaymentalias());
    q.setParameter("in_is_default", getIsDefault());
    List<GeneralSPResponse> list = q.list();

    if (list == null) {
      session.getTransaction().rollback();
      session.close();
      throw new PaymentException("savePaymentOption", "Error saving payment " + getPaymentalias());
    } else {
      if (list.size() > 0) {
        if (!list.get(0).getStatus().equals("Y")) {
          session.getTransaction().rollback();
          throw new PaymentException("savePaymentOption", "Error saving payment " + getPaymentalias()
              + ". Fail Reason is : " + list.get(0).getMvnemsg());
        } else {
          session.getTransaction().commit();
        }
      } else {
        session.getTransaction().rollback();
        // session.close();
        throw new PaymentException("savePaymentOption", "Error saving payment. No cursor items returned...");
      }
    }

  }

  public void update() throws PaymentException {
    if (getPaymentalias() == null || getPaymentalias().trim().length() == 0) {
      throw new PaymentException("Please specify a payment alias");
    }
    if (!getIsDefault().equals("N") && !getIsDefault().equals("Y")) {
      throw new PaymentException("Invalid default value...valid values are N or Y");
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("upd_cust_pmt_map");
    q.setParameter("in_cust_id", getCustid());
    q.setParameter("in_pmt_id", getPaymentid());
    q.setParameter("in_pmt_alias", getPaymentalias());
    q.setParameter("in_is_default", getIsDefault());
    List<GeneralSPResponse> list = q.list();

    if (list == null) {
      session.getTransaction().rollback();
      session.close();
      throw new PaymentException("savePaymentOption", "Error saving payment " + getPaymentalias());
    } else {
      if (list.size() > 0) {
        if (!list.get(0).getStatus().equals("Y")) {
          session.getTransaction().rollback();
          throw new PaymentException("savePaymentOption", "Error saving payment " + getPaymentalias()
              + ". Fail Reason is : " + list.get(0).getMvnemsg());
        } else {
          session.getTransaction().commit();
        }
      } else {
        session.getTransaction().rollback();
        // session.close();
        throw new PaymentException("savePaymentOption", "Error saving payment. No cursor items returned...");
      }
    }

  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CustPmtMap Object....");
    sb.append(" \n");
    sb.append("CustId           :: " + getCustid());
    sb.append(" \n");
    sb.append("PaymentId        :: " + getPaymentid());
    sb.append(" \n");
    sb.append("Payment Type     :: " + getPaymenttype());
    sb.append(" \n");
    sb.append("Payment Alias    :: " + getPaymentalias());
    sb.append(" \n");
    sb.append("IsDefault        :: " + getIsDefault());
    return sb.toString();
  }

}
