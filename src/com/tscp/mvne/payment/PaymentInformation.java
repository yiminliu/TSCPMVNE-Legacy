package com.tscp.mvne.payment;

import com.tscp.mvne.payment.dao.PaymentTransaction;
import com.tscp.mvne.payment.dao.PaymentUnitResponse;

public abstract class PaymentInformation {

  private int paymentid;
  private String alias;

  private String address1;
  private String address2;
  private String city;
  private String state;
  private String zip;

  public PaymentInformation() {
  };

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
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

  public int getPaymentid() {
    return paymentid;
  }

  public void setPaymentid(int paymentid) {
    this.paymentid = paymentid;
  }

  public abstract void setIsDefault(String setDefault);

  public abstract String getIsDefault();

  public abstract PaymentType getPaymentType();

  public abstract PaymentUnitResponse submitPayment(PaymentTransaction transaction) throws PaymentException;

  public abstract void savePaymentOption() throws PaymentException;

  public abstract void deletePaymentOption() throws PaymentException;

  public abstract void load() throws PaymentException;

  public abstract boolean validate() throws PaymentException;
}
