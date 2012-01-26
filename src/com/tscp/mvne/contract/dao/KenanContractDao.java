package com.tscp.mvne.contract.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.ServiceInstance;
import com.tscp.mvne.contract.KenanContract;
import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.exception.DaoException;
import com.tscp.mvne.hibernate.HibernateUtil;

@SuppressWarnings("unchecked")
public class KenanContractDao {

  public static int insertContract(KenanContract contract) throws DaoException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction transaction = session.beginTransaction();

    Query query = session.getNamedQuery("ins_coupon_contract");
    query.setParameter("in_account_no", contract.getAccount().getAccountno());
    query.setParameter("in_mdn", contract.getServiceInstance().getExternalid());
    query.setParameter("in_contract_type", contract.getContractType());
    query.setParameter("in_duration", contract.getDuration());
    GeneralSPResponse response = getQueryResponse(query.list(), transaction, session);

    if (response != null && response.success()) {
      transaction.commit();
      return response.getMvnemsgcode();
    } else {
      throw new DaoException("insertContract", "Error inserting contract " + contract.getContractType()
          + " on account " + contract.getAccount().getAccountno() + ". Fail Reason is : " + response.getMvnemsg());
    }
  }

  public static void updateContract(KenanContract contract) throws DaoException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction transaction = session.beginTransaction();

    Query query = session.getNamedQuery("upd_coupon_contract");
    query.setParameter("in_account_no", contract.getAccount().getAccountno());
    query.setParameter("in_mdn", contract.getServiceInstance().getExternalid());
    query.setParameter("in_contract_type", contract.getContractType());
    query.setParameter("in_contract_id", contract.getContractId());
    query.setParameter("in_duration", contract.getDuration());
    GeneralSPResponse response = getQueryResponse(query.list(), transaction, session);

    if (response != null && response.success()) {
      transaction.commit();
    } else {
      throw new DaoException("updateContrat", "Error updating contract " + contract.getContractId()
          + ". Fail Reason is : " + response.getMvnemsg());
    }
  }

  public static List<KenanContract> getContracts(Account account, ServiceInstance serviceInstance) throws DaoException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction transaction = session.beginTransaction();
    try {
      Query query = session.getNamedQuery("get_customer_coupons");
      query.setParameter("in_account_no", account.getAccountno());
      query.setParameter("in_external_id", serviceInstance.getExternalid());
      List<KenanContract> contracts = query.list();
      transaction.commit();
      return contracts;
    } catch (RuntimeException e) {
      HibernateUtil.rollbackTransaction(transaction);
      throw new DaoException("Error fetching contracts (getContracts)");
    }
  }

  public static int applyCouponPayment(Account account, String amount, Date date) throws DaoException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction transaction = session.beginTransaction();

    Query query = session.getNamedQuery("ins_coupon_payment");
    query.setParameter("in_account_no", account.getAccountno());
    query.setParameter("in_coupon_amount", amount);
    query.setParameter("in_trans_date", date);
    GeneralSPResponse response = getQueryResponse(query.list(), transaction, session);

    if (response != null && response.success()) {
      transaction.commit();
      return response.getMvnemsgcode();
    } else {
      throw new DaoException("applyCouponPayment", "Error applying coupon payment on account " + account.getAccountno()
          + " for " + amount + ". Fail Reason is : " + response.getMvnemsg());
    }
  }

  private static GeneralSPResponse getQueryResponse(List cursor, Transaction transaction, Session session) {
    GeneralSPResponse response = null;
    if (cursor == null || cursor.size() < 1) {
      transaction.rollback();
      session.close();
      response = new GeneralSPResponse();
      response.setStatus("N");
      response.setMvnemsg("No items returned in cursor");
    } else {
      response = (GeneralSPResponse) cursor.get(0);
    }
    return response;
  }

}