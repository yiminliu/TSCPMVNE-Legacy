package com.tscp.mvne.customer.dao;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "Cust_Acct_Map")
@NamedQuery(name = "Customer.getAccountsById", query = "from CustAcctMapDAO where custid = :custid")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class CustAcctMapDAO {

  int id;
  int cust_id;
  int account_no;

  public int getCust_id() {
    return cust_id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setCust_id(int custid) {
    this.cust_id = custid;
  }

  public int getAccount_no() {
    return account_no;
  }

  public void setAccount_no(int accountno) {
    this.account_no = accountno;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("CustAcctMapDAO Object....");
    sb.append(" \n");
    sb.append("Id               :: " + getId());
    sb.append(" \n");
    sb.append("CustId           :: " + getCust_id());
    sb.append(" \n");
    sb.append("AccountNo        :: " + getAccount_no());
    return super.toString();
  }
}
