package com.tscp.mvne.payment.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.hibernate.HibernateUtil;
import com.tscp.mvne.payment.PaymentException;

@SuppressWarnings("unchecked")
public class PaymentInvoice implements Serializable {
  private static final long serialVersionUID = -9095267344797834806L;
  int paymentInvoiceId;
  int transId;
  int notificationId;

  String paymentUnitConfirmation;
  String paymentMethod;
  String paymentSource;

  int invoiceNumber;

  String invoiceBody;

  public PaymentInvoice() {
  }

  public int getTransId() {
    return transId;
  }

  public void setTransId(int transId) {
    this.transId = transId;
  }

  public int getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(int notificationId) {
    this.notificationId = notificationId;
  }

  public String getPaymentUnitConfirmation() {
    return paymentUnitConfirmation;
  }

  public void setPaymentUnitConfirmation(String paymentUnitConfirmation) {
    this.paymentUnitConfirmation = paymentUnitConfirmation;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getPaymentSource() {
    return paymentSource;
  }

  public void setPaymentSource(String paymentSource) {
    this.paymentSource = paymentSource;
  }

  public int getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(int invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public String getInvoiceBody() {
    return invoiceBody;
  }

  public void setInvoiceBody(String invoiceBody) {
    this.invoiceBody = invoiceBody;
  }

  public int getPaymentInvoiceId() {
    return paymentInvoiceId;
  }

  public void setPaymentInvoiceId(int paymentInvoiceId) {
    this.paymentInvoiceId = paymentInvoiceId;
  }

  public void save() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();

    if (getPaymentInvoiceId() == 0) {
      session.beginTransaction();

      Query q = session.getNamedQuery("ins_pmt_invoice");
      List<GeneralSPResponse> generalSPResponseList = q.list();
      if (generalSPResponseList != null && generalSPResponseList.size() > 0) {
        for (GeneralSPResponse generalSPResponse : generalSPResponseList) {
          if (generalSPResponse.getStatus().equals("Y")) {
            session.getTransaction().commit();
            setPaymentInvoiceId(generalSPResponse.getMvnemsgcode());
          } else {
            session.getTransaction().rollback();
            throw new PaymentException("Error inserting payment invoice footprint..." + generalSPResponse.getMvnemsg());
          }
        }
      } else {
        session.getTransaction().rollback();
        throw new PaymentException(
            "Error inserting payment invoice footprint...nothing was returned from the ins_pmt_invoice method...");
      }

    }

    session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("upd_pmt_invoice");
    q.setParameter("in_pmtinvoice_id", getPaymentInvoiceId());
    q.setParameter("in_trans_id", getTransId());
    q.setParameter("in_notification_id", getNotificationId());

    List<GeneralSPResponse> generalSPResponseList = q.list();

    if (generalSPResponseList != null && generalSPResponseList.size() > 0) {
      for (GeneralSPResponse generalSPResponse : generalSPResponseList) {
        if (generalSPResponse.getStatus().equals("Y")) {
          session.getTransaction().commit();
        } else {
          session.getTransaction().rollback();
          throw new PaymentException("Error updating payment invoice " + getPaymentInvoiceId() + "..."
              + generalSPResponse.getMvnemsg());
        }
      }
    } else {
      session.getTransaction().rollback();
      throw new PaymentException("Error inserting payment invoice " + getPaymentInvoiceId()
          + "...nothing was returned from the upd_pmt_invoice method...");
    }
    // session.close();
  }
}
