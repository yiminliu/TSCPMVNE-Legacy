package com.tscp.mvne.unittest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.tscp.mvne.TruConnect;
import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.ServiceInstance;
import com.tscp.mvne.billing.dao.UsageSummary;
import com.tscp.mvne.customer.Customer;
import com.tscp.mvne.customer.dao.CustAcctMapDAO;
import com.tscp.mvne.customer.dao.CustTopUp;
import com.tscp.mvne.customer.dao.DeviceInfo;
import com.tscp.mvne.payment.PaymentException;
import com.tscp.mvne.payment.PaymentType;
import com.tscp.mvne.payment.dao.CreditCard;
import com.tscp.mvne.payment.dao.CustPmtMap;
import com.tscp.mvne.payment.dao.PaymentRecord;

public class WebServiceTester {

  TruConnect tc;

  public WebServiceTester() {
    tc = new TruConnect();
  }

  public static void main(String[] args) {
    System.out.println("Hello world.");
    WebServiceTester wst = new WebServiceTester();
    // wst.addCustomerPayment();
    // wst.updateCustPmtMap();
    // wst.test1();
    // wst.getCust();
    // wst.getAccountInfo(693882);
    // wst.getChargeHistory(240, 693882);
    // wst.updateTopUp();
    wst.testReinstallProcess(32, 688669, 3, "09144678731");
    // wst.testReinstallProcess(240, 693891, 8, "");
    // wst.makePayment();
    // wst.getPaymentList();
    // wst.getUsage();
    // wst.getTopUpAmount(1);
    // wst.deleteCustomerPayment();
  }

  //
  // private void disconnectCustomer() {
  // ServiceInstance serviceInstance = new ServiceInstance();
  // serviceInstance.setExternalid(externalid);
  // tc.disconnectService(serviceInstance);
  // }

  public void getChargeHistory(int custId, int accountNo) {
    Customer customer = new Customer();
    customer.setId(custId);
    tc.getCustomerChargeHistory(customer, accountNo, null);
  }

  public void updateCustPmtMap() {
    CustPmtMap cpm = new CustPmtMap();
    cpm.setCustid(2);
    cpm.setPaymentid(167);
    cpm.setPaymentalias("My Card Name");
    cpm.setIsDefault("Y");
    List<CustPmtMap> paymentTypes = tc.updateCustPaymentMap(cpm);
    for (CustPmtMap pmtMap : paymentTypes) {
      System.out.println("CustId       :: " + pmtMap.getCustid());
      System.out.println("IsDefault    :: " + pmtMap.getIsDefault());
      System.out.println("PaymentAlias :: " + pmtMap.getPaymentalias());
      System.out.println("PaymentId    :: " + pmtMap.getPaymentid());
      System.out.println("PaymentType  :: " + pmtMap.getPaymenttype());
    }
  }

  public void getTopUpAmount(int custid) {
    Customer customer = new Customer();
    customer.setId(custid);
    Account account = new Account();
    account.setAccountno(0);
    CustTopUp ctu = customer.getTopupAmount(account);
    if (ctu != null) {
      System.out.println("Customer TopUp Amount " + ctu.toString());
    }
  }

  private void addCustomerPayment() {
    Customer customer = new Customer();
    customer.setId(2);
    CreditCard creditcard = new CreditCard();
    creditcard.setAddress1("1234 Main St");
    creditcard.setAddress2(null);
    creditcard.setAlias("Test Discover Card");
    creditcard.setCity("Springfield");
    creditcard.setCreditCardNumber("6006000998980019");
    creditcard.setIsDefault("Y");
    creditcard.setExpirationDate("1211");
    creditcard.setIsDefault("Y");
    creditcard.setNameOnCreditCard("Mickey Mouse");
    creditcard.setPaymentType(PaymentType.CreditCard);
    creditcard.setState("CA");
    creditcard.setVerificationcode("996");
    creditcard.setZip("90044");
    creditcard = tc.addCreditCard(customer, creditcard);
    System.out.println("PaymentId :: " + creditcard.getPaymentid());
  }

  private void updateTopUp() {
    Customer customer = new Customer();
    customer.setId(1);

    Account account = new Account();
    account.setAccountno(1);

    tc.setCustTopUpAmount(customer, "30.00", account);
  }

  private void updateCustomerPayment() {
    Customer customer = new Customer();
    customer.setId(2);
    CreditCard creditcard = new CreditCard();
    creditcard.setPaymentid(55);
    creditcard.setAddress1("1234 Main St");
    creditcard.setAddress2(null);
    creditcard.setAlias(null);
    creditcard.setCity("Springfield");
    creditcard.setCreditCardNumber("377224496141004");
    creditcard.setIsDefault("Y");
    creditcard.setExpirationDate("1211");
    creditcard.setIsDefault("Y");
    creditcard.setNameOnCreditCard("Mickey Mouse");
    creditcard.setPaymentType(PaymentType.CreditCard);
    creditcard.setState("CA");
    creditcard.setVerificationcode("1166");
    creditcard.setZip("90044");
    // creditcard = tc.addCreditCard(customer, creditcard);

    List<CustPmtMap> paymentMapList = tc.updateCreditCardPaymentMethod(customer, creditcard);
    for (CustPmtMap pmtMap : paymentMapList) {
      System.out.println("CustId       :: " + pmtMap.getCustid());
      System.out.println("IsDefault    :: " + pmtMap.getIsDefault());
      System.out.println("PaymentAlias :: " + pmtMap.getPaymentalias());
      System.out.println("PaymentId    :: " + pmtMap.getPaymentid());
      System.out.println("PaymentType  :: " + pmtMap.getPaymenttype());
    }
  }

  private void deleteCustomerPayment() {
    Customer customer = new Customer();
    customer.setId(2);
    List<CustPmtMap> paymentMapList = tc.deleteCreditCardPaymentMethod(customer, 55);
    for (CustPmtMap pmtMap : paymentMapList) {
      System.out.println("CustId       :: " + pmtMap.getCustid());
      System.out.println("IsDefault    :: " + pmtMap.getIsDefault());
      System.out.println("PaymentAlias :: " + pmtMap.getPaymentalias());
      System.out.println("PaymentId    :: " + pmtMap.getPaymentid());
      System.out.println("PaymentType  :: " + pmtMap.getPaymenttype());
    }
  }

  private void getPaymentList() {
    Customer customer = new Customer();
    customer.setId(1);
    List<PaymentRecord> paymentRecordList = tc.getPaymentHistory(customer);
    for (PaymentRecord paymentRecord : paymentRecordList) {
      System.out.println("TransId           :: " + paymentRecord.getTransId());
      System.out.println("PaymentId         :: " + paymentRecord.getPaymentId());
      System.out.println("Alias             :: " + paymentRecord.getAlias());
      System.out.println("PaymentAmount     :: " + paymentRecord.getPaymentAmount());
      System.out.println("PaymentUnitConfirm:: " + paymentRecord.getPaymentUnitConfirmation());
      System.out.println("PaymentUnitMessage:: " + paymentRecord.getPaymentUnitMessage());
      System.out.println("PaymentDate       :: " + paymentRecord.getPaymentDate());
      System.out.println("BillingTrackingId :: " + paymentRecord.getBillingTrackingId());
      System.out.println("PostDate          :: " + paymentRecord.getPostDate());
      System.out.println("PaymentSource     :: " + paymentRecord.getPaymentSource());
      System.out.println("PaymentStatus     :: " + paymentRecord.getPaymentStatus());
    }
  }

  public void testReinstallProcess(int custId, int accountNo, int deviceId, String esn) {
    Customer customer = new Customer();
    customer.setId(custId);

    DeviceInfo deviceInfo = new DeviceInfo();
    deviceInfo.setDeviceId(deviceId);
    deviceInfo.setAccountNo(accountNo);
    deviceInfo.setDeviceValue(esn);

    tc.reinstallCustomerDevice(customer, deviceInfo);
  }

  private void getUsage() {
    Customer customer = new Customer();
    customer.setId(2);

    ServiceInstance serviceInstance = new ServiceInstance();
    serviceInstance.setExternalid("2123884164");

    UsageSummary usage = tc.getUsageSummary(customer, serviceInstance);
    if (usage != null) {
      System.out.println("Usage :: " + usage.toString());
    }
  }

  private void test1() {
    TruConnect tc = new TruConnect();
    tc.activateService(null, null);
  }

  private void makePayment() {
    TruConnect tc = new TruConnect();
    Account account = new Account();
    account.setAccountno(693881);

    int pmtId = 6718;

    // CreditCard creditcard = new CreditCard();
    // creditcard = tc.getCreditCardDetail(22);
    String paymentamount = "20.00";
    // System.exit(0);
    Customer customer = new Customer();
    customer.setId(238);
    try {
      // tc.makeCreditCardPayment("3sacas023", account, creditcard,
      // paymentamount);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
      String sessionid = "CID" + customer.getId() + "T" + sdf.format(new Date()) + "TEST";
      tc.submitPaymentByPaymentId(sessionid, customer, pmtId, account, paymentamount);
    } catch (PaymentException pmt_ex) {
      pmt_ex.printStackTrace();
    }
  }

  private void getCust() {
    CustAcctMapDAO c = tc.getCustFromAccount(681685);
    System.out.println("Cust :: " + c.toString());
  }

  private Account getAccountInfo(int accountNo) {
    return tc.getAccountInfo(accountNo);
  }

}
