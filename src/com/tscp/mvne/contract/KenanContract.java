package com.tscp.mvne.contract;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.ServiceInstance;

public class KenanContract {
  private Account account;
  private ServiceInstance serviceInstance;
  private int contractType;
  private int contractId;
  private int duration;
  private String description;

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public ServiceInstance getServiceInstance() {
    return serviceInstance;
  }

  public void setServiceInstance(ServiceInstance serviceInstance) {
    this.serviceInstance = serviceInstance;
  }

  public int getContractType() {
    return contractType;
  }

  public void setContractType(int contractType) {
    this.contractType = contractType;
  }

  public int getContractId() {
    return contractId;
  }

  public void setContractId(int contractId) {
    this.contractId = contractId;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  // TODO validation should be moved out of the object and into a validator
  // class
  public boolean validate() {
    return validateAccount() && validateServiceInstance() && validateContractType() && validateDuration();
  }

  private boolean validateAccount() {
    if (account == null || account.getAccountno() == 0) {
      return false;
    } else {
      return true;
    }
  }

  private boolean validateServiceInstance() {
    if (serviceInstance == null || serviceInstance.getExternalid() == null || serviceInstance.getExternalid().isEmpty()) {
      return false;
    } else {
      return true;
    }
  }

  private boolean validateContractType() {
    return contractType != 0;
  }

  private boolean validateDuration() {
    return duration >= 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("contractId=").append(contractId).append(", ");
    sb.append("contractType=").append(contractType).append(", ");
    sb.append("duration=").append(duration).append(", ");
    sb.append("description=").append(description);
    return sb.toString();
  }

  public String toFormattedString() {
    StringBuilder sb = new StringBuilder();
    sb.append("--Contract--").append("\n");
    sb.append("  contractId=").append(contractId).append("\n");
    sb.append("  contractType=").append(contractType).append("\n");
    sb.append("  duration=").append(duration).append("\n");
    sb.append("  description=").append(description);
    return sb.toString();
  }

}