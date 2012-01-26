package com.tscp.mvne.customer;

import java.util.List;
import java.util.Vector;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.dao.UsageDetail;
import com.tscp.mvne.customer.dao.CustAcctMapDAO;
import com.tscp.mvne.customer.dao.CustAddress;
import com.tscp.mvne.customer.dao.CustInfo;
import com.tscp.mvne.customer.dao.CustTopUp;
import com.tscp.mvne.customer.dao.DeviceAssociation;
import com.tscp.mvne.customer.dao.DeviceInfo;
import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.hibernate.HibernateUtil;
import com.tscp.mvne.payment.PaymentException;
import com.tscp.mvne.payment.PaymentInformation;
import com.tscp.mvne.payment.PaymentType;
import com.tscp.mvne.payment.dao.CreditCard;
import com.tscp.mvne.payment.dao.CustPmtMap;
import com.tscp.mvne.payment.dao.PaymentInvoice;
import com.tscp.mvne.payment.dao.PaymentRecord;
import com.tscp.mvne.payment.dao.PaymentTransaction;
import com.tscp.mvne.payment.dao.PaymentUnitResponse;

@SuppressWarnings("unchecked")
public class Customer {

  int id;

  List<CustAcctMapDAO> custaccts;
  List<CustPmtMap> custpmttypes;

  List<PaymentInformation> paymentinformation;

  List<Account> accounts;

  List<DeviceInfo> deviceList;

  public Customer() {
    paymentinformation = new Vector<PaymentInformation>();
  }

  public void get() throws CustomerException {
    List<CustAcctMapDAO> custAcctList = getCustaccts();
    getCustpmttypes(0);

    // for( CustAcctMapDAO custAcct : custAcctList ) {
    //
    // }
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setCustaccts(List<CustAcctMapDAO> custaccts) {
    this.custaccts = custaccts;
  }

  public CustInfo getCustInfo() {
    CustInfo custInfo = null;
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();

    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_cust_info");
    q.setParameter("in_cust_id", getId());

    List<CustInfo> custInfoList = q.list();

    if (custInfoList != null && custInfoList.size() > 0) {
      custInfo = custInfoList.get(0);
    }

    session.getTransaction().commit();

    return custInfo;
  }

  public List<CustAcctMapDAO> getCustaccts() {
    if (custaccts == null) {
      Session session = HibernateUtil.getSessionFactory().getCurrentSession();
      session.beginTransaction();

      Query q = session.getNamedQuery("fetch_cust_acct_map");
      q.setParameter("in_cust_id", getId());

      custaccts = q.list();

      session.getTransaction().commit();
    }
    return custaccts;
  }

  public void setCustpmttypes(List<CustPmtMap> custpmttypes) {
    this.custpmttypes = custpmttypes;
  }

  public List<CustPmtMap> getCustpmttypes(int pmt_id) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_cust_pmt_map");
    q.setParameter("in_cust_id", id);
    q.setParameter("in_pmt_id", pmt_id);
    custpmttypes = q.list();

    session.getTransaction().commit();
    return custpmttypes;
  }

  public List<PaymentInformation> getPaymentinformation() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_cust_pmt_map");
    q.setParameter("in_cust_id", getId());
    q.setParameter("in_pmt_id", 0);
    custpmttypes = q.list();
    for (CustPmtMap custpmt : custpmttypes) {
      if (custpmt.getPaymentalias().equals(PaymentType.CreditCard.toString())) {
        q = session.getNamedQuery("fetch_pmt_cc_info");
        q.setParameter("in_pmt_id", custpmt.getPaymentid());
        List<CreditCard> creditcard = q.list();
        if (creditcard != null) {
          paymentinformation.add(creditcard.get(0));
        }
      }
    }

    session.getTransaction().rollback();

    return paymentinformation;
  }

  public void addCustAccts(Account account) throws CustomerException {
    if (getId() <= 0) {
      throw new CustomerException("addCustAccts", "Please specify a customer to add an account mapping.");
    }
    if (account == null || account.getAccountno() <= 0) {
      throw new CustomerException("addCustAccts", "Please specify an account to add to customer " + getId());
    }

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();

    CustAcctMapDAO custacctmap = new CustAcctMapDAO();
    custacctmap.setCust_id(getId());
    custacctmap.setAccount_no(account.getAccountno());
    Query q = session.getNamedQuery("ins_cust_acct_map");
    q.setParameter("cust_id", custacctmap.getCust_id());
    q.setParameter("account_no", custacctmap.getAccount_no());
    List<GeneralSPResponse> spresponse = q.list();

    if (spresponse != null) {
      for (GeneralSPResponse response : spresponse) {
        System.out.println("STATUS :: " + response.getStatus() + " :: MVNEMSGCODE :: " + response.getMvnemsgcode()
            + " :: MVNEMSG :: " + response.getMvnemsg());
        if (!response.getStatus().equals("Y")) {
          throw new CustomerException("addCustAccts", "Error adding Customer Acct Map:: " + response.getMvnemsgcode()
              + "::" + response.getMvnemsg());
        }
      }
    } else {
      throw new CustomerException("addCustAccts", "No response returned from the db when calling ins_cust_acct_map");
    }

    tx.commit();
  }

  public void deleteCustAccts(Account account) throws CustomerException {
    if (getId() <= 0) {
      throw new CustomerException("addCustAccts", "Please specify a customer to add an account mapping.");
    }
    if (account == null || account.getAccountno() <= 0) {
      throw new CustomerException("addCustAccts", "Please specify an account to add to customer " + getId());
    }

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    CustAcctMapDAO custacctmap = new CustAcctMapDAO();
    custacctmap.setCust_id(getId());
    custacctmap.setAccount_no(account.getAccountno());
    Query q = session.getNamedQuery("del_cust_acct_map");
    q.setParameter("cust_id", custacctmap.getCust_id());
    q.setParameter("account_no", custacctmap.getAccount_no());
    List<GeneralSPResponse> spresponse = q.list();

    if (spresponse != null) {
      for (GeneralSPResponse response : spresponse) {
        System.out.println("STATUS :: " + response.getStatus() + " :: MVNEMSGCODE :: " + response.getMvnemsgcode()
            + " :: MVNEMSG :: " + response.getMvnemsg());
        if (!response.getStatus().equals("Y")) {
          throw new CustomerException("addCustAccts", "Error deleting Customer Acct Map:: " + response.getMvnemsgcode()
              + "::" + response.getMvnemsg());
        }
      }
    } else {
      throw new CustomerException("addCustAccts", "No response returned from the db when calling ins_cust_acct_map");
    }

    session.getTransaction().commit();
  }

  public CreditCard insertCreditCardPaymentInformation(CreditCard creditcard) throws PaymentException {
    if (id <= 0) {
      throw new CustomerException("insertCreditCardPaymentInformation", "Please specify a Customer Id");
    }
    if (creditcard == null) {
      throw new PaymentException("insertCreditCardPaymentInformation", "Please specify payment information to save.");
    }
    if (creditcard.validate()) {
      creditcard.savePaymentOption();
    }
    if (creditcard.getPaymentid() <= 0) {
      throw new PaymentException("insertCreditCardPaymentInformation", "Error saving payment information.");
    } else {
      if (creditcard.getAlias() == null || creditcard.getAlias().trim().length() == 0) {
        int myFirstCardNumber = Integer.parseInt(creditcard.getCreditCardNumber().substring(0, 1));
        String myAlias = " "
            + creditcard.getCreditCardNumber().substring(creditcard.getCreditCardNumber().length() - 4,
                creditcard.getCreditCardNumber().length());
        switch (myFirstCardNumber) {
        case 3:
          myAlias = "AMEX" + myAlias;
          // throw new
          // PaymentException("insertCreditCardPaymentInformation","American Express cards are not accepted at this time. Please try another card");
          break;
        case 4:
          myAlias = "VISA" + myAlias;
          break;
        case 5:
          myAlias = "MasterCard" + myAlias;
          break;
        case 6:
          myAlias = "Discover" + myAlias;
          // throw new
          // PaymentException("insertCreditCardPaymentInformation","Discover cards are not accepted at this time. Please try another card");
          break;
        }
        creditcard.setAlias(myAlias);
      }
      saveCustPmtMap(creditcard);
    }
    return creditcard;
  }

  public CreditCard updateCreditCardPaymentInformation(CreditCard creditcard) throws PaymentException {
    if (id <= 0) {
      throw new CustomerException("updateCreditCardPaymentInformation", "Please specify a Customer Id");
    }
    if (creditcard == null || creditcard.getPaymentid() <= 0) {
      throw new PaymentException("updateCreditCardPaymentInformation", "Please specify payment information to update.");
    }
    /*
     * we dont need to validate CC since it's an update. we're assuming the
     * client will handle this and null fields will not be updated anyways
     */
    if (true /* creditcard.validate() */) {
      if (creditcard.getAlias() == null || creditcard.getAlias().trim().length() == 0) {
        int myFirstCardNumber = Integer.parseInt(creditcard.getCreditCardNumber().substring(0, 1));
        String myAlias = " "
            + creditcard.getCreditCardNumber().substring(creditcard.getCreditCardNumber().length() - 4,
                creditcard.getCreditCardNumber().length());
        switch (myFirstCardNumber) {
        case 3:
          myAlias = "AMEX" + myAlias;
          // throw new
          // PaymentException("updateCreditCardPaymentInformation","American Express cards are not accepted at this time. Please try another card");
          break;
        case 4:
          myAlias = "VISA" + myAlias;
          break;
        case 5:
          myAlias = "MasterCard" + myAlias;
          break;
        case 6:
          myAlias = "Discover" + myAlias;
          // throw new
          // PaymentException("updateCreditCardPaymentInformation","Discover cards are not accepted at this time. Please try another card");
          break;
        }
        creditcard.setAlias(myAlias);
      }
      creditcard.savePaymentOption();
    }
    // else {
    // throw new
    // PaymentException("updateCreditCardPaymentInformation","CreditCard information does not validate however no validation exception was thrown.");
    // }
    return creditcard;
  }

  public List<PaymentRecord> getPaymentHistory() throws CustomerException {
    if (id <= 0) {
      throw new CustomerException("Invalid Customer Id " + id);
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_cust_pmt_trans");
    q.setParameter("in_cust_id", id);
    List<PaymentRecord> paymentRecordList = q.list();

    session.getTransaction().commit();
    return paymentRecordList;

  }

  public PaymentInvoice getPaymentInvoice(int transId) throws CustomerException {
    if (id <= 0) {
      throw new CustomerException("Invalid Customer...Id cannot be <= 0");
    }
    if (transId == 0) {
      throw new PaymentException("Please specify a transaction to look up an invoice against");
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    PaymentInvoice paymentInvoice = new PaymentInvoice();

    Query q = session.getNamedQuery("fetch_pmt_invoice");
    q.setParameter("in_cust_id", getId());
    q.setParameter("in_trans_id", transId);
    List<PaymentInvoice> paymentInvoiceList = q.list();
    if (paymentInvoiceList != null && paymentInvoiceList.size() > 0) {
      for (PaymentInvoice tempPaymentInvoice : paymentInvoiceList) {
        paymentInvoice = tempPaymentInvoice;
      }
    }
    session.getTransaction().commit();
    // session.close();
    return paymentInvoice;
  }

  public void deletePayment(int paymentId) throws CustomerException {
    if (getId() == 0) {
      throw new CustomerException("deletePayment", "Invalid Customer Object. ID must be set.");
    }
    List<CustPmtMap> custPmtMapList = getCustpmttypes(0);
    boolean isValidTransaction = false;
    for (CustPmtMap cpm : custPmtMapList) {
      if (cpm.getPaymentid() == paymentId) {
        isValidTransaction = true;
        if (cpm.getPaymenttype().equals(PaymentType.CreditCard.toString())) {
          CreditCard creditcard = new CreditCard();
          creditcard.setPaymentid(paymentId);
          creditcard.deletePaymentOption();
        }
        break;
      }
    }
    if (!isValidTransaction) {
      throw new CustomerException("deletePayment", "Invalid Request. Payment ID " + paymentId
          + " does not belong to cust id " + getId());
    }
  }

  public CustTopUp getTopupAmount(Account account) throws CustomerException {
    if (getId() == 0) {
      throw new CustomerException("getTopupAmount", "Customer.id must be set...");
    }
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    String query = "fetch_cust_topup_amt";

    Query q = session.getNamedQuery(query);
    q.setParameter("in_cust_id", getId());
    q.setParameter("in_account_no", account.getAccountno());
    CustTopUp topupAmount = new CustTopUp();
    List<CustTopUp> topupAmountList = q.list();
    for (CustTopUp custTopUp : topupAmountList) {
      topupAmount = custTopUp;
    }
    session.getTransaction().commit();
    return topupAmount;
  }

  public CustTopUp setTopupAmount(Account account, String topupAmount) throws CustomerException {
    if (getId() == 0) {
      throw new CustomerException("setTopupAmount", "Customer.id must be set");
    }
    CustTopUp custTopUp = new CustTopUp();
    custTopUp.setCustid(getId());
    custTopUp.setTopupAmount(topupAmount);
    custTopUp.setAccountNo(account.getAccountno());
    custTopUp.save();
    return getTopupAmount(account);
  }

  public PaymentUnitResponse submitPayment(PaymentTransaction transaction, int paymentId) throws PaymentException {
    PaymentUnitResponse retValue = null;
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    System.out.println("Payment Amount " + transaction.getPaymentAmount());
    Query q = session.getNamedQuery("sbt_pmt_info");
    q.setParameter("in_cust_id", getId());
    q.setParameter("in_pmt_id", paymentId);
    q.setParameter("in_pymntamt", transaction.getPaymentAmount());

    List<PaymentUnitResponse> responseList = q.list();
    for (PaymentUnitResponse response : responseList) {
      retValue = response;
    }

    session.getTransaction().commit();
    return retValue;
  }

  public CustAcctMapDAO getCustAcctMapDAOfromAccount(int accountno) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_cust_from_acct");
    q.setParameter("in_account_no", accountno);
    List<CustAcctMapDAO> custAcctMapList = q.list();
    CustAcctMapDAO retValue = new CustAcctMapDAO();
    for (CustAcctMapDAO custAcctMap : custAcctMapList) {
      retValue = custAcctMap;
    }

    session.getTransaction().commit();
    return retValue;
  }

  private void saveCustPmtMap(PaymentInformation paymentinformation) throws PaymentException {

    CustPmtMap custpmtmap = new CustPmtMap();
    custpmtmap.setCustid(id);
    custpmtmap.setPaymentid(paymentinformation.getPaymentid());
    custpmtmap.setPaymenttype(paymentinformation.getPaymentType().toString());
    custpmtmap.setPaymentalias(paymentinformation.getAlias());
    custpmtmap.setIsDefault(paymentinformation.getIsDefault());

    custpmtmap.save();
  }

  public List<DeviceInfo> getDeviceList() {
    return deviceList;
  }

  public void setDeviceList(List<DeviceInfo> deviceList) {
    this.deviceList = deviceList;
  }

  public List<DeviceInfo> retrieveDeviceList() {
    return retrieveDeviceList(0, 0);
  }

  public List<DeviceInfo> retrieveDeviceList(int accountNo) {
    return retrieveDeviceList(0, accountNo);
  }

  public List<DeviceInfo> retrieveDeviceList(int deviceId, int accountNo) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    Query q = session.getNamedQuery("fetch_device_info");
    q.setParameter("in_cust_id", getId());
    q.setParameter("in_device_id", deviceId);
    q.setParameter("in_account_no", accountNo);
    List<DeviceInfo> deviceInfoList = q.list();
    setDeviceList(deviceInfoList);
    session.getTransaction().rollback();
    return getDeviceList();
  }

  public List<DeviceAssociation> retrieveDeviceAssociationList(int inDeviceId) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_device_assoc_map");
    q.setParameter("in_cust_id", getId());
    q.setParameter("in_device_id", inDeviceId);
    List<DeviceAssociation> deviceAssociationList = q.list();

    session.getTransaction().rollback();
    return deviceAssociationList;
  }

  public List<UsageDetail> getChargeHistory(int accountNo, String mdn) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("sp_fetch_charge_history");
    q.setParameter("in_account_no", accountNo);
    q.setParameter("in_external_id", mdn);

    List<UsageDetail> usageDetailList = q.list();

    session.getTransaction().rollback();
    return usageDetailList;
  }

  public List<CustAddress> getCustAddressList(int addressId) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();

    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_cust_address");
    q.setParameter("in_cust_id", getId());
    q.setParameter("in_address_id", addressId);

    List<CustAddress> custAddressList = q.list();

    session.getTransaction().rollback();
    return custAddressList;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Customer Object....");
    sb.append("\n");
    sb.append("id               :: " + getId());
    // if( getCustaccts() != null ) {
    // for( CustAcctMapDAO custAcct : getCustaccts() ) {
    // sb.append("****************************");
    // sb.append(custAcct.toString());
    // }
    // }
    return sb.toString();
  }

}
