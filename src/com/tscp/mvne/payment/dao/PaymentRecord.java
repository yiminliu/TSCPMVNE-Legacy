package com.tscp.mvne.payment.dao;

import java.io.Serializable;
import java.util.Date;

public class PaymentRecord implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private int transId = 0;
  private int billingTrackingId = 0;
  private int paymentId = 0;

  private String alias;
  private String paymentSource;
  private String paymentMethod;
  private String paymentAmount;
  private String paymentStatus;

  private String paymentUnitConfirmation;
  private String paymentUnitMessage;

  private Date paymentDate;
  private Date postDate;

  private int accountNo;

  private String account;
  private String paymentType;

  public PaymentRecord() {

  }

  public int getTransId() {
    return transId;
  }

  public void setTransId(int transId) {
    this.transId = transId;
  }

  public int getBillingTrackingId() {
    return billingTrackingId;
  }

  public void setBillingTrackingId(int billingTrackingId) {
    this.billingTrackingId = billingTrackingId;
  }

  public int getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(int paymentId) {
    this.paymentId = paymentId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
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

  public String getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(String paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public String getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  public String getPaymentUnitConfirmation() {
    return paymentUnitConfirmation;
  }

  public void setPaymentUnitConfirmation(String paymentUnitConfirmation) {
    this.paymentUnitConfirmation = paymentUnitConfirmation;
  }

  public String getPaymentUnitMessage() {
    return paymentUnitMessage;
  }

  public void setPaymentUnitMessage(String paymentUnitMessage) {
    this.paymentUnitMessage = paymentUnitMessage;
  }

  public Date getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(Date paymentDate) {
    this.paymentDate = paymentDate;
  }

  public Date getPostDate() {
    return postDate;
  }

  public void setPostDate(Date postDate) {
    this.postDate = postDate;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public int getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(int accountNo) {
    this.accountNo = accountNo;
  }

}
