package com.tscp.mvne.billing;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

// TODO jpong: Move address to a separate embedded class. This will affect TruConnect webfont as well.
public class Account implements Serializable {
  private static final long serialVersionUID = 1L;
  private int accountCategory;
  private int accountNumber;
  private String firstName;
  private String middleName;
  private String lastName;
  private String contact_address1;
  private String contact_address2;
  private String contact_city;
  private String contact_state;
  private String contact_zip;
  private String contact_number;
  private String contact_email;

  private Collection<ServiceInstance> serviceInstanceList;
  private Collection<Package> packageList;

  private Date activeDate;
  private Date inactiveDate;

  private String balance;

  public Account() {
    // do nothing
  }

  public int getAccount_category() {
    return accountCategory;
  }

  public void setAccount_category(int account_category) {
    this.accountCategory = account_category;
  }

  public int getAccountno() {
    return accountNumber;
  }

  public void setAccountno(int accountno) {
    this.accountNumber = accountno;
  }

  public String getFirstname() {
    return firstName;
  }

  public void setFirstname(String firstname) {
    this.firstName = firstname;
  }

  public String getMiddlename() {
    return middleName;
  }

  public void setMiddlename(String middlename) {
    this.middleName = middlename;
  }

  public String getLastname() {
    return lastName;
  }

  public void setLastname(String lastname) {
    this.lastName = lastname;
  }

  public String getContact_address1() {
    return contact_address1;
  }

  public void setContact_address1(String contact_address1) {
    this.contact_address1 = contact_address1;
  }

  public String getContact_address2() {
    return contact_address2;
  }

  public void setContact_address2(String contact_address2) {
    this.contact_address2 = contact_address2;
  }

  public String getContact_city() {
    return contact_city;
  }

  public void setContact_city(String contact_city) {
    this.contact_city = contact_city;
  }

  public String getContact_state() {
    return contact_state;
  }

  public void setContact_state(String contact_state) {
    this.contact_state = contact_state;
  }

  public String getContact_zip() {
    return contact_zip;
  }

  public void setContact_zip(String contact_zip) {
    this.contact_zip = contact_zip;
  }

  public String getContact_number() {
    return contact_number;
  }

  public void setContact_number(String contact_number) {
    this.contact_number = contact_number;
  }

  public String getContact_email() {
    return contact_email;
  }

  public void setContact_email(String contact_email) {
    this.contact_email = contact_email;
  }

  public Collection<ServiceInstance> getServiceinstancelist() {
    return serviceInstanceList;
  }

  public void setServiceinstancelist(Collection<ServiceInstance> serviceinstancelist) {
    this.serviceInstanceList = serviceinstancelist;
  }

  public Collection<Package> getPackageList() {
    return packageList;
  }

  public void setPackageList(Collection<Package> packageList) {
    this.packageList = packageList;
  }

  public Date getActive_date() {
    return activeDate;
  }

  public void setActive_date(Date active_date) {
    this.activeDate = active_date;
  }

  public Date getInactive_date() {
    return inactiveDate;
  }

  public void setInactive_date(Date inactive_date) {
    this.inactiveDate = inactive_date;
  }

  public String getBalance() {
    return balance;
  }

  public void setBalance(String balance) {
    this.balance = balance;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("accountNumber=").append(accountNumber).append(", ");
    sb.append("name=").append(firstName).append(" ").append(lastName).append(", ");
    sb.append("balance=").append(balance);
    return sb.toString();
  }

  public String toFormattedString() {
    StringBuffer sb = new StringBuffer();
    sb.append("--Account--").append(" \n");
    sb.append("  Account Number   = " + getAccountno()).append(" \n");
    sb.append("  FirstName        = " + getFirstname()).append(" \n");
    sb.append("  LastName         = " + getLastname()).append(" \n");
    sb.append("  Account Category = " + getAccount_category()).append(" \n");
    sb.append("  Balance          = " + getBalance()).append(" \n");
    sb.append("  Contact Address1 = " + getContact_address1()).append(" \n");
    sb.append("  Contact Address2 = " + getContact_address2()).append(" \n");
    sb.append("  Contact City     = " + getContact_city()).append(" \n");
    sb.append("  Contact State    = " + getContact_state()).append(" \n");
    sb.append("  Contact Zip      = " + getContact_zip()).append(" \n");
    sb.append("  Contact Email    = " + getContact_email()).append(" \n");
    sb.append("  Contact Number   = " + getContact_number()).append(" \n");
    sb.append("  Active Date      = " + getActive_date()).append(" \n");
    sb.append("  Inactive Date    = " + getInactive_date());
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Account) {
      Account tempAccount = (Account) obj;
      if (tempAccount.getAccount_category() == getAccount_category() && tempAccount.getAccountno() == getAccountno()
          && tempAccount.getActive_date().equals(getActive_date())
          && tempAccount.getContact_address1().equals(getContact_address1())
          && tempAccount.getContact_address2().equals(getContact_address2())
          && tempAccount.getContact_city().equals(getContact_city())
          && tempAccount.getContact_email().equals(getContact_email())
          && tempAccount.getContact_number().equals(getContact_number())
          && tempAccount.getContact_state().equals(getContact_state())
          && tempAccount.getContact_zip().equals(getContact_zip()) && tempAccount.getFirstname().equals(getFirstname())
          && tempAccount.getInactive_date().equals(getInactive_date())
          && tempAccount.getLastname().equals(getLastname()) && tempAccount.getMiddlename().equals(getMiddlename())
          && tempAccount.getBalance() == getBalance()) {
        return true;
      }
    }
    return super.equals(obj);
  }
}
