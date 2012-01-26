package com.tscp.mvne.refund;

import java.io.Serializable;
import java.util.Date;

public class KenanPayment implements Serializable {
  private static final long serialVersionUID = 336497760957045043L;
  // TODO associate this class with entries in pmt_trans
  private int accountNumber;
  private Date transactionDate;
  private Double amount;
  private String trackingId;
  private int trackingIdServer;

  public int getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(int accountNumber) {
    this.accountNumber = accountNumber;
  }

  public Date getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(Date transactionDate) {
    this.transactionDate = transactionDate;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public void setTrackingId(String trackingId) {
    this.trackingId = trackingId;
  }

  public int getTrackingIdServer() {
    return trackingIdServer;
  }

  public void setTrackingIdServer(int trackingIdServer) {
    this.trackingIdServer = trackingIdServer;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + accountNumber;
    result = prime * result + ((amount == null) ? 0 : amount.hashCode());
    result = prime * result + ((trackingId == null) ? 0 : trackingId.hashCode());
    result = prime * result + trackingIdServer;
    result = prime * result + ((transactionDate == null) ? 0 : transactionDate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    KenanPayment other = (KenanPayment) obj;
    if (accountNumber != other.accountNumber)
      return false;
    if (amount == null) {
      if (other.amount != null)
        return false;
    } else if (!amount.equals(other.amount))
      return false;
    if (trackingId == null) {
      if (other.trackingId != null)
        return false;
    } else if (!trackingId.equals(other.trackingId))
      return false;
    if (trackingIdServer != other.trackingIdServer)
      return false;
    if (transactionDate == null) {
      if (other.transactionDate != null)
        return false;
    } else if (!transactionDate.equals(other.transactionDate))
      return false;
    return true;
  }

}
