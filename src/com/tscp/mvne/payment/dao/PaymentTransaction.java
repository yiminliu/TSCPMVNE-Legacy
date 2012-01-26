package com.tscp.mvne.payment.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.hibernate.HibernateUtil;
import com.tscp.mvne.payment.PaymentException;

@SuppressWarnings("unchecked")
public class PaymentTransaction {

  private int transId;
  private String sessionId;
  private int pmtId;
  private int attemptNo;

  private String paymentAmount;

  private Date paymentTransDate;

  private String paymentUnitConfirmation;
  private Date paymentUnitDate;
  private String paymentUnitMessage;

  private int billingTrackingId = 0;
  private Date billingUnitDate;

  private String paymentSource;
  private String paymentMethod;
  private int accountNo;

  public PaymentTransaction() {

  }

  public int getTransId() {
    return transId;
  }

  public void setTransId(int transId) {
    this.transId = transId;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public int getPmtId() {
    return pmtId;
  }

  public void setPmtId(int pmtId) {
    this.pmtId = pmtId;
  }

  public int getAttemptNo() {
    return attemptNo;
  }

  public void setAttemptNo(int attemptNo) {
    this.attemptNo = attemptNo;
  }

  public String getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(String paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public Date getPaymentTransDate() {
    return paymentTransDate;
  }

  public void setPaymentTransDate(Date paymentTransDate) {
    this.paymentTransDate = paymentTransDate;
  }

  public String getPaymentUnitConfirmation() {
    return paymentUnitConfirmation;
  }

  public void setPaymentUnitConfirmation(String paymentUnitConfirmation) {
    this.paymentUnitConfirmation = paymentUnitConfirmation;
  }

  public Date getPaymentUnitDate() {
    return paymentUnitDate;
  }

  public void setPaymentUnitDate(Date paymentUnitDate) {
    this.paymentUnitDate = paymentUnitDate;
  }

  public String getPaymentUnitMessage() {
    return paymentUnitMessage;
  }

  public void setPaymentUnitMessage(String paymentUnitMessage) {
    this.paymentUnitMessage = paymentUnitMessage;
  }

  public int getBillingTrackingId() {
    return billingTrackingId;
  }

  public void setBillingTrackingId(int billingTrackingId) {
    this.billingTrackingId = billingTrackingId;
  }

  public Date getBillingUnitDate() {
    return billingUnitDate;
  }

  public void setBillingUnitDate(Date billingUnitDate) {
    this.billingUnitDate = billingUnitDate;
  }

  public String getPaymentSource() {
    return paymentSource;
  }

  public void setPaymentSource(String paymentSource) {
    this.paymentSource = paymentSource;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public int getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(int accountNo) {
    this.accountNo = accountNo;
  }

  public void savePaymentTransaction() throws PaymentException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    if (getTransId() == 0) {
      Query q = session.getNamedQuery("ins_pmt_trans");
      q.setParameter("in_session_id", getSessionId());
      q.setParameter("in_pmt_id", getPmtId() == 0 ? "" : getPmtId());
      q.setParameter("in_pmt_amount", getPaymentAmount());
      q.setParameter("in_account_no", getAccountNo());
      List<GeneralSPResponse> responseList = q.list();
      if (responseList == null) {
        throw new PaymentException("savePaymentTransaction", "Error creating transaction for session id :: "
            + getSessionId() + " and payment id " + getPmtId() + ". No Response from DB");
      } else {
        for (GeneralSPResponse response : responseList) {
          if (!response.getStatus().equals("Y")) {
            session.getTransaction().rollback();
            throw new PaymentException("savePaymentTransaction", "Error creating transaction for session id :: "
                + getSessionId() + " and payment id " + getPmtId() + ". Error Message :: " + response.getMvnemsgcode()
                + "::" + response.getMvnemsg());
          } else {
            setTransId(response.getMvnemsgcode());
          }
        }
      }
    } else {
      Query q = session.getNamedQuery("upd_pmt_trans");
      q.setParameter("in_trans_id", getTransId());
      q.setParameter("in_pmt_unit_confirmation", getPaymentUnitConfirmation());
      q.setParameter("in_pmt_unit_date", getPaymentUnitDate());
      q.setParameter("in_pmt_unit_msg", getPaymentUnitMessage());
      q.setParameter("in_billing_tracking_id", getBillingTrackingId());
      q.setParameter("in_billing_date", getBillingUnitDate() == null ? "" : getBillingUnitDate());
      q.setParameter("in_pmt_source", getPaymentSource());
      q.setParameter("in_pmt_method", getPaymentMethod());
      List<GeneralSPResponse> responseList = q.list();
      if (responseList == null) {
        throw new PaymentException("savePaymentTransaction", "Error updating transaction " + getTransId()
            + ". No Response from DB");
      } else {
        for (GeneralSPResponse response : responseList) {
          if (!response.getStatus().equals("Y")) {
            session.getTransaction().rollback();
            throw new PaymentException("savePaymentTransaction", "Error updating transaction " + getTransId()
                + ". Error Message :: " + response.getMvnemsgcode() + "::" + response.getMvnemsg());
          }
        }
      }
    }
    session.getTransaction().commit();
  }

}
