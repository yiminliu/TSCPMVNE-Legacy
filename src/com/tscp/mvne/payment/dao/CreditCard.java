package com.tscp.mvne.payment.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.hibernate.HibernateUtil;
import com.tscp.mvne.payment.PaymentException;
import com.tscp.mvne.payment.PaymentInformation;
import com.tscp.mvne.payment.PaymentType;

@SuppressWarnings("unchecked")
public class CreditCard extends PaymentInformation implements Serializable {

  public static final String CREDITCARD_AMEX = "3";
  public static final String CREDITCARD_VISA = "4";
  public static final String CREDITCARD_MASTERCARD = "5";
  public static final String CREDITCARD_DISCOVER = "6";

  private static final long serialVersionUID = 1L;

  private String isDefault;
  private PaymentType paymentType = PaymentType.CreditCard;

  private String nameOnCreditCard;
  private String creditCardNumber;
  private String expirationDate;
  private String verificationcode;

  public CreditCard() {
    paymentType = PaymentType.CreditCard;
  }

  public String getNameOnCreditCard() {
    return nameOnCreditCard;
  }

  public void setNameOnCreditCard(String nameOnCreditCard) {
    this.nameOnCreditCard = nameOnCreditCard;
  }

  public String getCreditCardNumber() {
    return creditCardNumber;
  }

  public void setCreditCardNumber(String creditCardNumber) {
    this.creditCardNumber = creditCardNumber;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getVerificationcode() {
    return verificationcode;
  }

  public void setVerificationcode(String verificationcode) {
    this.verificationcode = verificationcode;
  }

  // public void setDefault(String isDefault) {
  // this.isDefault = isDefault;
  // }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  @Override
  @Enumerated(EnumType.STRING)
  public PaymentType getPaymentType() {
    return paymentType;
  }

  @Override
  public void setIsDefault(String isDefault) {
    this.isDefault = isDefault;
  }

  @Override
  public String getIsDefault() {
    return isDefault;
  }

  @Override
  public PaymentUnitResponse submitPayment(PaymentTransaction transaction) throws PaymentException {
    PaymentUnitResponse retValue = null;
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    System.out.println("Payment Amount " + transaction.getPaymentAmount());
    Query q = session.getNamedQuery("sbt_pmt_cc_info");
    q.setParameter("in_cardno", getCreditCardNumber());
    q.setParameter("in_cardexpdt", getExpirationDate());
    q.setParameter("in_seccode", getVerificationcode());
    q.setParameter("in_pymntamt", transaction.getPaymentAmount());
    q.setParameter("in_zip", getZip());
    q.setParameter("in_cardholder", getNameOnCreditCard());
    q.setParameter("in_street", getAddress1());

    List<PaymentUnitResponse> responseList = q.list();
    for (PaymentUnitResponse response : responseList) {
      retValue = response;
    }

    session.getTransaction().commit();
    return retValue;
  }

  @Override
  public void savePaymentOption() throws PaymentException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();

    session.beginTransaction();
    String queryName = "";
    if (getPaymentid() == 0) {
      queryName = "ins_pmt_cc_info";
    } else {
      queryName = "upd_pmt_cc_info";
    }

    Query q = session.getNamedQuery(queryName);
    if (getPaymentid() != 0) {
      q.setParameter("in_pmt_id", getPaymentid());
    }
    q.setParameter("in_cust_name", getNameOnCreditCard());
    q.setParameter("in_addr1", getAddress1());
    q.setParameter("in_addr2", getAddress2());
    q.setParameter("in_city", getCity());
    q.setParameter("in_state", getState());
    q.setParameter("in_zip", getZip());
    q.setParameter("in_cardno", getCreditCardNumber());
    q.setParameter("in_exp_dt", getExpirationDate());
    q.setParameter("in_sec_code", getVerificationcode());
    List<GeneralSPResponse> list = q.list();

    if (list == null) {
      session.getTransaction().rollback();
      session.close();
      throw new PaymentException("savePaymentOption", "Error saving payment " + getAlias());
    } else {
      if (list.size() > 0) {
        if (!list.get(0).getStatus().equals("Y")) {
          session.getTransaction().rollback();
          throw new PaymentException("savePaymentOption", "Error saving payment " + getAlias() + ". Fail Reason is : "
              + list.get(0).getMvnemsg());
        } else {
          if (getPaymentid() == 0) {
            setPaymentid(list.get(0).getMvnemsgcode());
          }
          session.getTransaction().commit();
        }
      } else {
        session.getTransaction().rollback();
        throw new PaymentException("savePaymentOption", "Error saving payment. No cursor items returned...");
      }
    }
  }

  @Override
  public void load() throws PaymentException {
    if (getPaymentid() <= 0) {
      throw new PaymentException("load", "Error loading payment information.  Please specify a PaymentID");
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_pmt_cc_info");
    q.setParameter("in_pmt_id", getPaymentid());

    List<CreditCard> creditCard = q.list();
    if (creditCard == null) {
      throw new PaymentException("load", "Credit Card information not found for Payment ID " + getPaymentid());
    } else {
      if (creditCard.size() > 1) {
        throw new PaymentException("load", "Too many rows returned when fetching CC info for payment id "
            + getPaymentid());
      }
      clone(creditCard.get(0));
    }
    session.getTransaction().rollback();
  }

  public void clone(CreditCard creditCard) {
    setPaymentid(creditCard.getPaymentid());
    setAddress1(creditCard.getAddress1());
    setAddress2(creditCard.getAddress2());
    setAlias(creditCard.getAlias());
    setCity(creditCard.getCity());
    setCreditCardNumber(creditCard.getCreditCardNumber());
    setExpirationDate(creditCard.getExpirationDate());
    setNameOnCreditCard(creditCard.getNameOnCreditCard());
    setPaymentType(PaymentType.CreditCard);
    setState(creditCard.getState());
    setVerificationcode(creditCard.getVerificationcode());
    setZip(creditCard.getZip());
    setIsDefault(creditCard.getIsDefault());
  }

  @Override
  public boolean validate() throws PaymentException {
    if (nameOnCreditCard == null || nameOnCreditCard.trim().length() == 0) {
      throw new PaymentException("validate", "Name cannot be null on credit card payment information");
    }
    if (creditCardNumber == null || creditCardNumber.trim().length() == 0) {
      throw new PaymentException("validate", "Credit Card Number invalid");
    } else if (creditCardNumber.trim().length() > 16 || creditCardNumber.trim().length() < 15) {
      throw new PaymentException("validate", "Credit Card Number invalid");
    }
    if (expirationDate == null || expirationDate.trim().length() == 0) {
      throw new PaymentException("validate", "Expiration date must be provided");
    }
    if (verificationcode == null || verificationcode.trim().length() == 0) {
      throw new PaymentException("validate", "Verification code must be populated.");
    }
    if (getZip() == null || getZip().trim().length() <= 0) {
      throw new PaymentException("validate", "Billing Zip code must be populated");
    }
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CreditCard) {
      CreditCard tempCreditCard = (CreditCard) obj;
      if (tempCreditCard.getCreditCardNumber().equals(getCreditCardNumber())
          && tempCreditCard.getExpirationDate().equals(getExpirationDate())
          && tempCreditCard.getAddress1().equals(getAddress1()) && tempCreditCard.getAddress2().equals(getAddress2())
          && tempCreditCard.getAlias().equals(getAlias()) && tempCreditCard.getCity().equals(getCity())
          && tempCreditCard.getNameOnCreditCard().equals(getNameOnCreditCard())
          && tempCreditCard.getPaymentid() == getPaymentid()
          && tempCreditCard.getPaymentType().equals(getPaymentType()) && tempCreditCard.getState().equals(getState())
          && tempCreditCard.getVerificationcode().equals(getVerificationcode())
          && tempCreditCard.getZip().equals(getZip())) {
        return true;
      }
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CreditCard Object...");
    sb.append(" \n");
    sb.append("Alias            :: " + getAlias());
    sb.append(" \n");
    sb.append("PaymentId        :: " + getPaymentid());
    sb.append(" \n");
    sb.append("PaymentType      :: " + getPaymentType());
    sb.append(" \n");
    sb.append("Address1         :: " + getAddress1());
    sb.append(" \n");
    sb.append("Address2         :: " + getAddress2());
    sb.append(" \n");
    sb.append("City             :: " + getCity());
    sb.append(" \n");
    sb.append("State            :: " + getState());
    sb.append(" \n");
    sb.append("Zip              :: " + getZip());
    sb.append(" \n");
    sb.append("IsDefault        :: " + getIsDefault());
    sb.append(" \n");
    sb.append("NameOnCreditCard :: " + getNameOnCreditCard());
    sb.append(" \n");
    if (getCreditCardNumber() != null && getCreditCardNumber().length() >= 4) {
      sb.append("CreditCardNumber :: "
          + getCreditCardNumber().substring(getCreditCardNumber().length() - 4, getCreditCardNumber().length()));
      sb.append(" \n");
    }
    sb.append("ExpirationDate   :: " + getExpirationDate());
    sb.append(" \n");
    sb.append("VerificationCode :: " + getVerificationcode());
    return sb.toString();
  }

  @Override
  public void deletePaymentOption() throws PaymentException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("del_pmt_cc_info");
    q.setParameter("in_payment_id", getPaymentid());
    List<GeneralSPResponse> list = q.list();

    if (list == null) {
      session.getTransaction().rollback();
      throw new PaymentException("deletePaymentOption", "Error deleting payment " + getAlias() + "(" + getPaymentid()
          + ")");
    } else {
      if (list.size() > 0) {
        if (!list.get(0).getStatus().equals("Y")) {
          session.getTransaction().rollback();
          throw new PaymentException("deletePaymentOption", "Error deleting payment " + getAlias() + "("
              + getPaymentid() + ")" + ". Fail Reason is : " + list.get(0).getMvnemsg());
        } else {
          System.out.println("Status: " + list.get(0).getStatus() + " | MVNEMsgCode: " + list.get(0).getMvnemsgcode()
              + " | MVNEMsg: " + list.get(0).getMvnemsg());
        }
      } else {
        session.getTransaction().rollback();
        throw new PaymentException("deletePaymentOption", "Error deleting payment. No cursor items returned...");
      }
    }

    session.getTransaction().commit();
  }
}
