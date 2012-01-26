package com.tscp.mvne.customer.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.customer.CustomerException;
import com.tscp.mvne.hibernate.HibernateUtil;

@SuppressWarnings("unchecked")
public class CustTopUp implements Serializable {
  private static final long serialVersionUID = 1L;
  int custid;
  String topupAmount;
  int accountNo;

  public CustTopUp() {
    custid = 0;
    topupAmount = "";
  }

  public int getCustid() {
    return custid;
  }

  public void setCustid(int custid) {
    this.custid = custid;
  }

  public String getTopupAmount() {
    return topupAmount;
  }

  public void setTopupAmount(String topupAmount) {
    this.topupAmount = topupAmount;
  }

  public int getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(int accountNo) {
    this.accountNo = accountNo;
  }

  public void save() throws CustomerException {
    if (getCustid() == 0) {
      throw new CustomerException("save", "Customer ID must be set before saving topup information...");
    }
    if (getTopupAmount() == null || getTopupAmount().indexOf(".") == 0) {
      throw new CustomerException("save", "Invalid top up amount. Top up amount must be in the format XXX.XX");
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    String query = "upd_cust_topup_amt";

    Query q = session.getNamedQuery(query);
    q.setParameter("in_cust_id", getCustid());
    q.setParameter("in_topup_amt", getTopupAmount());
    q.setParameter("in_account_no", getAccountNo());

    List<GeneralSPResponse> list = q.list();
    if (list == null) {
      System.out.println("No response returned from executing named query " + query);
    }

    session.getTransaction().commit();
  }

  @Override
  public String toString() {
    return "CustId :: " + getCustid() + " || TopUp Amount :: " + getTopupAmount() + " || AccountNo :: "
        + getAccountNo();
  }

}
