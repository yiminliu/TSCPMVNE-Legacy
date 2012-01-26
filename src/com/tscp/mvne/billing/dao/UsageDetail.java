package com.tscp.mvne.billing.dao;

import java.io.Serializable;
import java.util.Date;

import com.tscp.mvne.billing.Usage;

public class UsageDetail extends Usage implements Serializable {
  private static final long serialVersionUID = 6998103452809873332L;
  private Date dateAndTime;
  private String usageType;
  private String rate;
  private String usageAmount;
  private String dollarAmount;
  private String discount;
  private Date startTime;
  private Date endTime;
  private String notes;
  private double balance;

  public UsageDetail() {
    // do nothing
  }

  public Date getDateAndTime() {
    return dateAndTime;
  }

  public void setDateAndTime(Date dateAndTime) {
    this.dateAndTime = dateAndTime;
  }

  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setUsageAmount(String usageAmount) {
    this.usageAmount = usageAmount;
  }

  public String getUsageAmount() {
    return usageAmount;
  }

  public String getDollarAmount() {
    return dollarAmount;
  }

  public void setDollarAmount(String dollarAmount) {
    this.dollarAmount = dollarAmount;
  }

  public void setUsageType(String usageType) {
    this.usageType = usageType;
  }

  public String getUsageType() {
    return usageType;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public String getDiscount() {
    return discount;
  }

  public void setDiscount(String discount) {
    this.discount = discount;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("dateAndTime=" + getDateAndTime()).append(", ");
    sb.append("usageType=" + getUsageType()).append(", ");
    sb.append("rate=" + getRate()).append(", ");
    sb.append("usageAmount=" + getUsageAmount()).append(", ");
    sb.append("startTime=" + getStartTime()).append(", ");
    sb.append("endTime=" + getEndTime()).append(", ");
    sb.append("notes=" + getNotes()).append(", ");
    sb.append("balance=" + getBalance());
    return sb.toString();
  }

  public String toFormattedString() {
    StringBuffer sb = new StringBuffer();
    sb.append("--UsageDetail--").append(" \n");
    sb.append("dateAndTime = " + getDateAndTime()).append(" \n");
    sb.append("usageType   = " + getUsageType()).append(" \n");
    sb.append("rate        = " + getRate()).append(" \n");
    sb.append("usageAmount = " + getUsageAmount()).append(" \n");
    sb.append("startTime   = " + getStartTime()).append(" \n");
    sb.append("endTime     = " + getEndTime()).append(" \n");
    sb.append("notes       = " + getNotes()).append(" \n");
    sb.append("balance     = " + getBalance());
    return sb.toString();
  }

}
