package com.tscp.mvne.unittest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.Component;
import com.tscp.mvne.billing.ServiceInstance;
import com.tscp.mvne.billing.api.BillName;
import com.tscp.mvne.billing.api.CustAddress;
import com.tscp.mvne.billing.dao.UsageDetail;
import com.tscp.mvne.customer.Customer;
import com.tscp.mvne.customer.dao.CustAcctMapDAO;
import com.tscp.mvne.customer.dao.DeviceAssociation;
import com.tscp.mvne.customer.dao.DeviceInfo;
import com.tscp.mvne.customer.dao.DeviceStatus;
import com.tscp.mvne.customer.dao.GeneralSPResponse;
import com.tscp.mvne.hibernate.HibernateUtil;
import com.tscp.mvne.network.NetworkImpl;
import com.tscp.mvne.network.NetworkInfo;
import com.tscp.mvne.payment.PaymentInformation;
import com.tscp.mvne.payment.PaymentType;
import com.tscp.mvne.payment.dao.CreditCard;
import com.tscp.mvne.payment.dao.CustPmtMap;
import com.tscp.mvne.payment.dao.PaymentInvoice;
import com.tscp.mvne.payment.dao.PaymentTransaction;
import com.tscp.mvne.payment.dao.PaymentUnitResponse;
import com.tscp.mvne.unittest.dao.ReservedMDN;

public class DAOTester {

  public static void main(String[] args) {
    if (args == null || args.length == 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
      DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.JAPAN));
      // Currency currency = Currency.getInstance("USD");
      // // Curre
      // currency.
      String myTestString = "CID540T2010722134600auto";
      System.out.println("contains test " + myTestString.contains("AUTO"));
      String topUpAmount = "10.00";
      String balance = "2.72";
      String testBalance = null;
      System.out.println(Double.parseDouble(testBalance == null ? "0.00" : "9.99"));
      int topUpQuantity = 0;
      while (Double.parseDouble(balance) < 2.0) {
        ++topUpQuantity;
        balance = Double.toString(Double.parseDouble(balance) + Double.parseDouble(topUpAmount));
      }
      System.out.println("TopUp Quantity " + topUpQuantity);
      System.out.println("Charge amount will be :" + topUpQuantity * Double.parseDouble(topUpAmount));
      System.out.println("New Balance is :" + NumberFormat.getCurrencyInstance().format(Double.parseDouble(balance)));
      Double.parseDouble(topUpAmount);
      if (Double.parseDouble(topUpAmount) % 10 != 0) {
        System.out.println("invalid amount");
      }
      assert 2 == 1 : "error";
      System.out.println(df.format(Double.parseDouble("-123.0")));
      System.out.println(df.format(Double.parseDouble("10.99900")));
      System.out.println(NumberFormat.getCurrencyInstance().format(Double.parseDouble("10.999")));
      int quantity = 2;
      System.out.println("Quantity modified :: "
          + NumberFormat.getCurrencyInstance().format(Double.parseDouble("10.999") * quantity));
      System.out.println(sdf.format(new Date()));

      // System.exit(0);
      DAOTester tester = new DAOTester();
      // tester.disconnectBatchMDN();
      tester.savePmtInvoice();
      // tester.getUsageDetail(693882, null);
      // tester.addCustAddress();
      // tester.updateCustAddress();
      // tester.getCustTopUpAmount();
      // List<com.tscp.mvne.customer.dao.CustAddress> custAddressList =
      // tester.getCustAddressList(1,0);
      // if( custAddressList != null && custAddressList.size() > 0 ) {
      // for( com.tscp.mvne.customer.dao.CustAddress custAddress :
      // custAddressList ) {
      // System.out.println(custAddress.toString());
      // }
      // }
      // tester.getBillName();
      // tester.getComponentList();
      // tester.getCustAddress();
      // tester.getPackages();
      // tester.getServices();
      // tester.getTransaction();
      // tester.updateTransaction();
      // tester.submitCCPayment();
      // tester.addCustAcctEntry(1, 100);
      // tester.saveDeviceInfo();
      // tester.saveDeviceAssociation();
      // tester.getDeviceInfoList(504, 0);
      // List<CustAcctMapDAO> custAcctMap = tester.listEvents();
      // if( custAcctMap != null && custAcctMap.size() > 0 ) {
      // for( CustAcctMapDAO cust : custAcctMap ) {
      // System.out.println("Cust : "+cust.getCust_id()+" :: Acct : "+cust.getAccount_no());
      // }
      // }
      // tester.saveCreditCard();
      // tester.saveCustPmtMap();

      // Vector< ? extends PaymentInformation> testVector = new
      // Vector<PaymentInformation>();
      // testVector.add(new CreditCard());

      // List<CustPmtMap> custpmtmap = tester.getCustPmtMap(1, 0);
      // if( custpmtmap != null ) {
      // for( CustPmtMap pmtmap : custpmtmap ) {
      // System.out.println("Cust ID      :: "+pmtmap.getCustid());
      // System.out.println("Payment ID   :: "+pmtmap.getPaymentid());
      // System.out.println("Payment Type :: "+pmtmap.getPaymenttype());
      // System.out.println("Payment Alias:: "+pmtmap.getPaymentalias());
      // }
      // }
      // List<PaymentInformation> pmtinfo = tester.getPaymentInformation(0);
      // if( pmtinfo != null ) {
      // if( pmtinfo.get(0).getPaymentType().equals(PaymentType.CreditCard) ) {
      // CreditCard cc = (CreditCard)pmtinfo.get(0);
      //
      // System.out.println(pmtinfo.get(0).getPaymentid()+" :: "+pmtinfo.get(0).getAlias()+" :: "+cc.getAlias()+" :: "+pmtinfo.get(0).getPaymentid()+" :: "+cc.getCreditCardNumber()+" :: "+cc.getExpirationDate());
      // }
      // }
    }
  }

  private void disconnectBatchMDN() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("sp_fetch_reserved_mdns");
    List<ReservedMDN> reservedMDNList = q.list();

    session.getTransaction().commit();
    if (reservedMDNList != null && reservedMDNList.size() > 0) {
      for (ReservedMDN reservedMDN : reservedMDNList) {
        NetworkImpl networkImpl = new NetworkImpl();
        try {
          NetworkInfo networkInfo = new NetworkInfo();
          networkInfo.setMdn(reservedMDN.getMDN());
          networkImpl.disconnectService(networkInfo);
          reservedMDN.setReleaseDate(new Date());
        } catch (Exception ex) {
          reservedMDN.setNotes(ex.getMessage());
        }
        reservedMDN.update();
      }
    } else {
      System.out.println("Reserved MDN List is empty...");
    }
  }

  private void savePmtInvoice() {
    PaymentInvoice paymentInvoice = new PaymentInvoice();
    paymentInvoice.setTransId(522);
    paymentInvoice.setNotificationId(143);
    paymentInvoice.save();
  }

  private void getCustTopUpAmount() {
    Customer customer = new Customer();
    customer.setId(240);
    Account account = new Account();
    account.setAccountno(693882);
    System.out.println("Top Up fetch");
    System.out.println(customer.getTopupAmount(account).toString());
  }

  private void updateCustAddress() {
    com.tscp.mvne.customer.dao.CustAddress custAddress = new com.tscp.mvne.customer.dao.CustAddress();
    custAddress.setAddress1("11025 E Valley Blvd");
    // custAddress.setAddress2("SUITE 3100");
    custAddress.setAddressLabel("El Monte Office");
    custAddress.setCity("El Monte");
    custAddress.setState("CA");
    custAddress.setZip("91731");
    custAddress.setCustId(1);
    custAddress.setAddressId(3);
    custAddress.save();
  }

  private void addCustAddress() {
    com.tscp.mvne.customer.dao.CustAddress custAddress = new com.tscp.mvne.customer.dao.CustAddress();
    custAddress.setAddress1("355 S GRAND AVE");
    custAddress.setAddress2("SUITE 3100");
    custAddress.setAddressLabel("Downtown LA Office");
    custAddress.setCity("Los Angeles");
    custAddress.setState("CA");
    custAddress.setZip("90071");
    custAddress.setCustId(1);
    custAddress.save();

  }

  private List<com.tscp.mvne.customer.dao.CustAddress> getCustAddressList(int custId, int addressId) {
    Customer customer = new Customer();
    customer.setId(custId);
    return customer.getCustAddressList(addressId);
  }

  private List<UsageDetail> getUsageDetail(int accountNo, String mdn) {
    Customer customer = new Customer();
    return customer.getChargeHistory(accountNo, mdn);
  }

  private void saveDeviceInfo() {
    // Session session = HibernateUtil.getSessionFactory().getCurrentSession();

    // session.beginTransaction();
    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setCustId(504);
    deviceInfo.setDeviceLabel("Vincent's Test Device");
    deviceInfo.setDeviceValue("09608582996");
    deviceInfo.save();
    // Query q = session.getNamedQuery("ins_device_info");
    // q.setParameter("in_cust_id", 504);
    // q.setParameter("in_device_label", "Vincent's Test Device");
    // q.setParameter("in_device_value", "09608582996");
    //
    // session.getTransaction().commit();
  }

  private void saveDeviceAssociation() {
    DeviceAssociation deviceAssoc = new DeviceAssociation();
    deviceAssoc.setDeviceId(1);
    deviceAssoc.setSubscrNo(745274);
    deviceAssoc.save();
  }

  private List<DeviceInfo> getDeviceInfoList(int custId, int deviceId) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_device_info");
    q.setParameter("in_cust_id", custId);
    q.setParameter("in_device_id", deviceId);

    List<DeviceInfo> deviceList = q.list();

    session.getTransaction().commit();

    for (DeviceInfo deviceInfo : deviceList) {
      if (deviceInfo.getDeviceId() == 1) {
        deviceInfo.setDeviceLabel("Vincent's MiFi Device");
        // deviceInfo.setDeviceStatus(DeviceStatus.DESC_ACTIVE);
        deviceInfo.setDeviceStatusId(DeviceStatus.ID_RELEASED_REMOVED);
        deviceInfo.setExpirationDate(new Date());
        deviceInfo.save();
        // Customer customer = new Customer();
        // customer.setId(504);
      }
    }
    return deviceList;
  }

  private List<CustAcctMapDAO> listEvents() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    // List<CustAcctMapDAO> result =
    // session.createQuery("from CustAcctMapDAO").list();
    Query q = session.getNamedQuery("fetch_cust_acct_map");
    q.setParameter("in_cust_id", "1");
    List<CustAcctMapDAO> result = q.list();

    session.getTransaction().commit();
    return result;
  }

  private void addCustAcctEntry(int custid, int accountno) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    CustAcctMapDAO custacctmap = new CustAcctMapDAO();
    custacctmap.setCust_id(custid);
    custacctmap.setAccount_no(accountno);
    Query q = session.getNamedQuery("ins_cust_acct_map");
    q.setParameter("cust_id", custacctmap.getCust_id());
    q.setParameter("account_no", custacctmap.getAccount_no());
    List<GeneralSPResponse> spresponse = q.list();

    if (spresponse != null) {
      for (GeneralSPResponse response : spresponse) {
        System.out.println("STATUS :: " + response.getStatus() + " :: MVNEMSGCODE :: " + response.getMvnemsgcode()
            + " :: MVNEMSG :: " + response.getMvnemsg());
      }
    }

    session.getTransaction().commit();
  }

  private void saveCreditCard() {
    CreditCard creditcard = new CreditCard();

    creditcard.setAddress1("5540 Middlebury Ct");
    creditcard.setAddress2(null);
    creditcard.setAlias("BofA Visa Card");
    creditcard.setCity("Rancho Cucamonga");
    creditcard.setCreditCardNumber("4000123400009017");
    creditcard.setExpirationDate("0413");
    creditcard.setNameOnCreditCard("Dan Ta");
    creditcard.setPaymentType(PaymentType.CreditCard);
    creditcard.setState("CA");
    creditcard.setVerificationcode("973");
    creditcard.setZip("91739");

    creditcard.savePaymentOption();

    System.out.println("PMT ID :: " + creditcard.getPaymentid());

  }

  private void saveCustPmtMap() {
    CustPmtMap custpmtmap = new CustPmtMap();
    custpmtmap.setCustid(1);
    custpmtmap.setPaymentid(1);
    custpmtmap.setPaymenttype(PaymentType.CreditCard.toString());
    custpmtmap.setPaymentalias("BofA Visa Card");

    custpmtmap.save();

  }

  private List<CustPmtMap> getCustPmtMap(int cust_id, int pmt_id) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_cust_pmt_map");
    q.setParameter("in_cust_id", cust_id);
    q.setParameter("in_pmt_id", pmt_id);
    List<CustPmtMap> list = q.list();

    session.getTransaction().commit();
    return list;
  }

  private List<PaymentInformation> getPaymentInformation(int pmt_id) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_pmt_cc_info");
    q.setParameter("in_pmt_id", 1);

    List<PaymentInformation> pmtinfo = q.list();

    session.getTransaction().commit();

    return pmtinfo;
  }

  private void getBillName() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_cust_name");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", "684806");

    List<BillName> billNameList = q.list();
    for (BillName billName : billNameList) {
      System.out.println("BillName :: " + billName.getFirstName() + " " + billName.getLastName());
    }
    session.getTransaction().rollback();
  }

  private void getComponentList() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_active_components");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", "684806");
    q.setParameter("in_external_id", "2138675309-Test");

    List<Component> componentList = q.list();
    for (Component component : componentList) {
      System.out.println("Component_id     :: " + component.getComponent_id());
      System.out.println("Component_Name   :: " + component.getComponent_name());
      System.out.println("Comp_Instance_id :: " + component.getComponent_instance_id());
      System.out.println("Active_Date :: " + component.getActive_date());
    }

    session.getTransaction().rollback();

  }

  private void getCustAddress() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_billing_address");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", "684806");

    List<CustAddress> custAddressList = q.list();
    if (custAddressList != null) {
      for (CustAddress custAddress : custAddressList) {
        StringBuffer sb = new StringBuffer();
        if (custAddress.getAddress1() != null) {
          sb.append(custAddress.getAddress1() + " ");
        }
        if (custAddress.getAddress2() != null) {
          sb.append(custAddress.getAddress2() + " ");
        }
        if (custAddress.getAddress3() != null) {
          sb.append(custAddress.getAddress3() + " ");
        }
        if (custAddress.getCity() != null) {
          sb.append(custAddress.getCity() + " ");
        }
        if (custAddress.getState() != null) {
          sb.append(custAddress.getState() + " ");
        }
        if (custAddress.getZip() != null) {
          sb.append(custAddress.getZip() + " ");
        }
        System.out.println("Billing Address :: " + sb.toString());
      }
    }

    session.getTransaction().rollback();
  }

  private void getPackages() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_active_packages");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", 684806);
    List<com.tscp.mvne.billing.Package> packageList = q.list();
    for (com.tscp.mvne.billing.Package acctPackage : packageList) {
      System.out.println("PackageId   :: " + acctPackage.getPackageid());
      System.out.println("PackageName :: " + acctPackage.getPackage_name());
      System.out.println("PackageInstanceId :: " + acctPackage.getPackage_instance_id());
    }

    session.getTransaction().rollback();
  }

  private void getServices() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_active_services");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", 681685);
    List<ServiceInstance> serviceInstanceList = q.list();
    for (ServiceInstance si : serviceInstanceList) {
      System.out.println("ExternalId  :: " + si.getExternalid());
      System.out.println("SubscrNo    :: " + si.getSubscrno());
    }

    session.getTransaction().rollback();
  }

  private PaymentTransaction getTransaction() {
    PaymentTransaction trans = null;
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("fetch_pmt_trans");
    q.setParameter("in_cust_id", 1);
    q.setParameter("in_trans_id", "");
    List<PaymentTransaction> transList = q.list();
    for (PaymentTransaction transaction : transList) {
      System.out.println("tid           ::" + transaction.getTransId());
      System.out.println("Attempt No    ::" + transaction.getAttemptNo());
      System.out.println("PayAmount     ::" + transaction.getPaymentAmount());
      System.out.println("PayUnitConf   ::" + transaction.getPaymentUnitConfirmation());
      System.out.println("PayUnitMsg    ::" + transaction.getPaymentUnitMessage());
      System.out.println("PayUnitDate   ::" + transaction.getPaymentUnitDate());
      System.out.println("BillDate      ::" + transaction.getBillingUnitDate());
      trans = transaction;
    }

    session.getTransaction().commit();

    return trans;
  }

  private void updateTransaction() {
    PaymentTransaction transaction = getTransaction();
    if (transaction != null) {
      if (transaction.getPaymentUnitDate() == null) {
        transaction.setPaymentUnitConfirmation("CC0T1364");
        transaction.setPaymentUnitMessage("Successful Charge AuthCode::000100");
        // SimpleDateFormat sdf = new SimpleDateFormat();
        // sdf.format(new Date());
        transaction.setPaymentUnitDate(new java.util.Date());
      } else if (transaction.getBillingUnitDate() == null) {
        transaction.setBillingUnitDate(new java.util.Date());
      }
      transaction.savePaymentTransaction();
    }
  }

  private void submitCCPayment() {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("sbt_pmt_cc_info");
    q.setParameter("in_cardno", "6011000998980019");
    q.setParameter("in_cardexpdt", "1211");
    q.setParameter("in_seccode", "996");
    q.setParameter("in_pymntamt", "1.20");
    q.setParameter("in_zip", "85284");
    q.setParameter("in_cardholder", "Joe Blow");
    q.setParameter("in_street", "742 Evergreen Terrace");

    List<PaymentUnitResponse> responseList = q.list();
    for (PaymentUnitResponse response : responseList) {
      System.out.println("ConfCode    :" + response.getConfcode());
      System.out.println("ConfDesc    :" + response.getConfdescr());
      System.out.println("TransId     :" + response.getTransid());
      System.out.println("AuthCode    :" + response.getAuthcode());
      System.out.println("CvvCode     :" + response.getCvvcode());
      System.out.println("ToString    :" + response.toString());
    }

    session.getTransaction().commit();
  }
}
