package com.tscp.mvne.unittest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.FileHandler;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.BillingSystem;
import com.tscp.mvne.billing.ServiceInstance;
import com.tscp.mvne.billing.api.ArrayOfMessageHolder;
import com.tscp.mvne.billing.api.ArrayOfPackage;
import com.tscp.mvne.billing.api.ArrayOfPaymentHolder;
import com.tscp.mvne.billing.api.ArrayOfPkgComponent;
import com.tscp.mvne.billing.api.ArrayOfServiceHolder;
import com.tscp.mvne.billing.api.ArrayOfValueHolder;
import com.tscp.mvne.billing.api.BillName;
import com.tscp.mvne.billing.api.BillingAccount;
import com.tscp.mvne.billing.api.BillingService;
import com.tscp.mvne.billing.api.BillingServiceInterface;
import com.tscp.mvne.billing.api.BillingServiceInterfaceSoap;
import com.tscp.mvne.billing.api.ContactInfo;
import com.tscp.mvne.billing.api.ContactInfoHolder;
import com.tscp.mvne.billing.api.CustAddrInfo;
import com.tscp.mvne.billing.api.CustAddress;
import com.tscp.mvne.billing.api.CustBalanceHolder;
import com.tscp.mvne.billing.api.MessageHolder;
import com.tscp.mvne.billing.api.PaymentHolder;
import com.tscp.mvne.billing.api.PkgComponent;
import com.tscp.mvne.billing.api.ServiceAddressHolder;
import com.tscp.mvne.billing.api.ServiceHolder;
import com.tscp.mvne.billing.api.UsageHolder;
import com.tscp.mvne.billing.api.ValueHolder;
import com.tscp.mvne.logger.TscpmvneLogger;
import com.tscp.mvne.payment.dao.PaymentTransaction;

public class BillingTester {

  BillingSystem billingImpl;
  BillingService billingservice;
  // Logger logger;
  TscpmvneLogger logger;

  BillingServiceInterfaceSoap port;

  public BillingTester() {
    logger = new TscpmvneLogger();
    // logger = LoggerFactory.getLogger("tscpmvneLogger");
    // logger.setUseParentHandlers(false);

    // Handler handler;
    try {
      java.util.logging.FileHandler handler = new FileHandler("test.txt");
      // logger.addHandler(handler);
    } catch (IOException io) {
      io.printStackTrace();
    }
    billingImpl = new BillingSystem();
    logger.info("test 1");
  }

  public Account getAccountByAccountNo(int accountNo) {
    if (billingImpl == null) {
      billingImpl = new BillingSystem();
    }

    Account account = billingImpl.getAccountByAccountNo(accountNo);

    System.out.println("Returned account object::");
    System.out.println(account.toString());

    return account;
  }

  public void addServiceInstance(Account account) {
    ServiceInstance serviceInstance = new ServiceInstance();
    billingImpl.addServiceInstance(account, serviceInstance);
  }

  public static void main(String[] args) {
    System.out.println("Testing Billing API");
    BillingTester bt = new BillingTester();
    String accountNo = "701520";
    // bt.updateCustAddress();
    // bt.getCustBalance();
    // bt.updateEmailAddress();
    // bt.updateContactInfo();
    // bt.test();
    // bt.createAccount();
    // bt.testAddService();
    // bt.addPackage();
    // bt.addComponent();
    // bt.test3();
    // bt.testGetActiveServices();
    bt.addPayment(accountNo);
    // bt.addTransaction();
    // bt.portAvailabilityCheck();
    // bt.getEmail();
    // bt.getContactInfo();
    bt.getPaymentList(accountNo);
    // bt.getCustBalance();
    // bt.getUsageList();
    // bt.updateThresholdValue("2132789548", null);
    // bt.getAccountByAccountNo(698177);
    System.out.println("Done Testing Billing API");
  }

  private void test4() {
    getBillingService();
    // port.addPayment(username, externalId, externalIdType, amount, transDate,
    // transType, submitBy);
  }

  private void test3() {
    BillingSystem billingImpl = new BillingSystem();

    Account account = billingImpl.getAccountByAccountNo(691421);
    account.toString();
    // ServiceInstance serviceinstance = new ServiceInstance();
    // serviceinstance.setExternalid("2138675309");
    // serviceinstance.setExternalidtype(3);
    // billingImpl.deleteServiceInstance(null, serviceinstance);
  }

  private void createAccount() {
    BillingSystem billingimpl = new BillingSystem();
    Account acct = new Account();
    acct.setContact_address1("5540 Middlebury Ct");
    acct.setContact_address2("");
    acct.setContact_city("Rancho Cucamonga");
    acct.setContact_state("CA");
    acct.setContact_zip("91739");
    acct.setFirstname("Dan");
    acct.setLastname("Ta");
    acct.setContact_email("dta@telscape.net");

    billingimpl.createAccount(acct);

    System.out.println("Account No :: " + acct.getAccountno());
  }

  private void test() {
    ClassLoader cl = BillingTester.class.getClassLoader();
    String inputPropertyFile = "config/truConnectDefaults.properties";

    String connectionPropertyFile = "config/connection.tscpmvne.properties";

    System.out.println("Loading Properties file...");
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    InputStream inConn = cl.getResourceAsStream(connectionPropertyFile);
    Properties props = new Properties();
    Properties connProps = new Properties();
    try {
      props.load(in);
      connProps.load(inConn);
      System.out.println("Account Categroy :: " + props.getProperty("account.account_category"));
      System.out.println("Currency Code :: " + props.getProperty("account.currency_code"));

      String wsdlLocation = connProps.getProperty("billing.location");
      String namespace = connProps.getProperty("billing.namespace");
      String servicename = connProps.getProperty("billing.servicename");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      BillingAccount billingAccount = new BillingAccount();

      billingAccount.setAccountCategory(Short.parseShort(props.getProperty("account.account_category")));
      billingAccount.setBillDispMethod(Short.parseShort(props.getProperty("account.bill_disp_meth")));
      billingAccount.setBillFormatOpt(Integer.parseInt(props.getProperty("account.bill_fmt_opt")));

      BillName billname = new BillName();
      billname.setFirstName("Daniel");
      billname.setMiddleName("Day");
      billname.setLastName("Lewis");
      // billingAccount.setBillName(billname);

      billingAccount.setBillPeriod(props.getProperty("account.bill_period"));
      billingAccount.setCCardIdServ(Short.parseShort(props.getProperty("account.default_ccard_id_serv")));
      billingAccount.setCollectionIndicator(Short.parseShort(props.getProperty("account.collection_indicator")));

      ContactInfo contactinfo = new ContactInfo();
      contactinfo.setContact1Name("Daniel Day Lewis");
      contactinfo.setContact1Phone("2133880022");
      // billingAccount.setContactInfo(contactinfo);

      billingAccount.setCreditThresh(props.getProperty("account.credit_thresh"));
      billingAccount.setCredStatus(Short.parseShort(props.getProperty("account.cred_status")));
      billingAccount.setCurrencyCode(Short.parseShort(props.getProperty("account.currency_code")));

      CustAddress custAddress = new CustAddress();
      custAddress.setAddress1("355 S Grand Ave");
      custAddress.setAddress2("");
      custAddress.setAddress3("");
      custAddress.setCity("Los Angeles");
      custAddress.setCountryCode(Short.parseShort(props.getProperty("account.cust_country_code")));
      custAddress.setCounty("Los Angeles");
      custAddress.setFranchiseTaxCode(Short.parseShort(props.getProperty("account.cust_franchise_tax_code")));
      custAddress.setState("CA");
      custAddress.setZip("90071");
      // billingAccount.setCustAddress(custAddress);

      // BillAddress billingAddress = new BillingAddress();
      // billingAccount.setBillAddress(custAddress);

      billingAccount.setCustEmail("dta@telscape.net");
      billingAccount.setCustFaxNo("");
      billingAccount.setCustPhone1("2133880022");
      billingAccount.setCustPhone2("");

      // billingAccount.setExrateClass(Short.parseShort(props.getProperty("account.exrate_class")));
      // billingAccount.setExternalAccountNoType(Short.parseShort(props.getProperty("account.account_type")));
      // billingAccount.setInsertGrpId(Short.parseShort(props.getProperty("account.insert_grp_id")));

      billingAccount.setLanguageCode(Short.parseShort(props.getProperty("account.language_code")));
      billingAccount.setMarketCode(Short.parseShort(props.getProperty("account.mkt_code")));
      billingAccount.setMsgGroupId(Short.parseShort(props.getProperty("account.msg_grp_id")));

      billingAccount.setOwningCostCtr(Short.parseShort(props.getProperty("account.owning_cost_ctr")));
      billingAccount.setPaymentMethod(Short.parseShort(props.getProperty("account.pay_method")));

      billingAccount.setRateClassDefault(Short.parseShort(props.getProperty("account.rate_class_default")));
      /**
       * Double check this field
       */
      billingAccount.setServiceCenterId(Short.parseShort(props.getProperty("account.svc_ctr_id")));
      billingAccount.setServiceCenterType(Short.parseShort(props.getProperty("account.svc_ctr_type")));

      billingAccount.setSicCode(Short.parseShort(props.getProperty("account.sic_code")));
      billingAccount.setTieCode(Short.parseShort(props.getProperty("account.tie_code")));

      billingAccount.setVipCode(Short.parseShort(props.getProperty("account.vip_code")));
      try {
        XMLGregorianCalendar value = DatatypeFactory.newInstance().newXMLGregorianCalendar(
            new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles")));
        // DataTypeFactory.newXML
        System.out.println(value);
        billingAccount.setSysDate(value);
        billingAccount.setAccountDateActive(value);

        // billingAccount.setBillFormatOpt(2);
        // billingAccount.setBillPeriod("P01");
        // short msgGroupId = 20;
        // billingAccount.setMsgGroupId(msgGroupId);
        // short mktcode = 26;
        // billingAccount.setMarketCode(mktcode);
        // short franchisetaxcode = 12;
        // billingAccount.setCustAddress(new CustAddress());
        // billingAccount.getCustAddress().setFranchiseTaxCode(franchisetaxcode);
        // billingAccount.setBillAddress(new CustAddress());
        // billingAccount.getBillAddress().setFranchiseTaxCode(franchisetaxcode);
        // short languagecode = 1;
        // billingAccount.setLanguageCode(languagecode);
        // short credstatus = 0;
        // billingAccount.setCredStatus(credstatus);
        // short siccode = 0;
        // billingAccount.setSicCode(siccode);
        // billingAccount.setBillName(new BillName());
        // billingAccount.getBillName().setFirstName("Larry");
        // billingAccount.getBillName().setMiddleName("");
        // billingAccount.getBillName().setLastName("David");
        // billingAccount.getBillAddress().setAddress1("355 S. Grand Ave");
        // billingAccount.getBillAddress().setAddress2("Suite 3100");
        // billingAccount.getBillAddress().setAddress3("");
        // billingAccount.getBillAddress().setCity("Los Angeles");
        // billingAccount.getBillAddress().setCounty("Los Angeles");
        // billingAccount.getBillAddress().setState("CA");
        // billingAccount.getBillAddress().setZip("90071");
        // billingAccount.getCustAddress().setAddress1("355 S. Grand Ave");
        // billingAccount.getCustAddress().setAddress2("Suite 3100");
        // billingAccount.getCustAddress().setAddress3("");
        // billingAccount.getCustAddress().setCity("Los Angeles");
        // billingAccount.getCustAddress().setCounty("Los Angeles");
        // billingAccount.getCustAddress().setState("CA");
        // billingAccount.getCustAddress().setZip("90071");
        // billingAccount.setCompanyName("");
        // billingAccount.setCustFaxNo("");
        // billingAccount.setCustPhone1("");
        // billingAccount.setCustPhone2("");
        // billingAccount.setCustEmail("");
        // billingAccount.setAccountDateActive(value);
        // billingAccount.setContactInfo(new ContactInfo());
        // billingAccount.getContactInfo().setContact1Name("");
        // billingAccount.getContactInfo().setContact2Name("");
        // billingAccount.getContactInfo().setContact1Phone("");
        // billingAccount.getContactInfo().setContact2Phone("");
        // billingAccount.setCreditThresh("");
        // billingAccount.setSysDate(value);
        // short countrycode = 840;
        // billingAccount.getBillAddress().setCountryCode(countrycode);
        // billingAccount.setBillNamePre("");
        // short svccenterid = 1001;
        // billingAccount.setServiceCenterId(svccenterid);
        // short insertgrpid = 4;
        // billingAccount.setInsertGrpId(insertgrpid);
        // short externalaccountnotype = 1;
        // billingAccount.setExternalAccountNoType(externalaccountnotype);
        // short paymethod = 1;
        // billingAccount.setPaymentMethod(paymethod);
        // short billdispmethod = 1;
        // billingAccount.setBillDispMethod(billdispmethod);
        // short rateclassdefault = 121;
        // billingAccount.setRateClassDefault(rateclassdefault);
        // short currencycode = 1;
        // billingAccount.setCurrencyCode(currencycode);
        // short exrateclasse = 1;
        // billingAccount.setExrateClass(exrateclasse);
        // short owningcostctr = 1;
        // billingAccount.setOwningCostCtr(owningcostctr);
        // short servicecentertype = 1;
        // billingAccount.setServiceCenterType(servicecentertype);
        // short collectionindicator = 1;
        // billingAccount.setCollectionIndicator(collectionindicator);
        // short accountcategory = 6;
        // billingAccount.setAccountCategory(accountcategory);
        // short vipcode = 101;
        // billingAccount.setVipCode(vipcode);
        // short custaddresscountrycode = 840;
        // billingAccount.getCustAddress().setCountryCode(custaddresscountrycode);
        // short tiecode = 0;
        // billingAccount.setTieCode(tiecode);
        // short ccardidserv = 0;
        // billingAccount.setCCardIdServ(ccardidserv);

      } catch (DatatypeConfigurationException dce) {
        dce.printStackTrace();
      }

      ValueHolder valueholder = port.addAccount("API", billingAccount);
      if (valueholder != null) {
        System.out.println("Value.value     :: " + valueholder.getValue());
        System.out.println("Value.value2    :: " + valueholder.getValue2());
        System.out.println("Value.message   :: " + valueholder.getStatusMessage());
        System.out.println("Value.statusmessage.message :: " + valueholder.getStatusMessage().getMessage());
        System.out.println("Value.statusmessage.status  :: " + valueholder.getStatusMessage().getStatus());
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
      System.exit(-1);
    }
  }

  private void getEmail() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      ValueHolder valueHolder = port.getEmail("dtat", "689555");
      if (valueHolder != null) {
        System.out.println("Status   :: " + valueHolder.getStatusMessage().getStatus());
        System.out.println("Message  :: " + valueHolder.getStatusMessage().getMessage());
        System.out.println("Value1   :: " + valueHolder.getValue());
        System.out.println("Value2   :: " + valueHolder.getValue2());
      }

    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }
  }

  private void testAddService() {

    ClassLoader cl = BillingTester.class.getClassLoader();
    String inputPropertyFile = "config/truConnectDefaults.properties";

    String connectionPropertyFile = "config/connection.tscpmvne.properties";

    System.out.println("Loading Properties file...");
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    InputStream inConn = cl.getResourceAsStream(connectionPropertyFile);
    Properties props = new Properties();
    Properties connProps = new Properties();
    try {
      props.load(in);
      connProps.load(inConn);
      System.out.println("Account Categroy :: " + props.getProperty("account.account_category"));
      System.out.println("Currency Code :: " + props.getProperty("account.currency_code"));

      String wsdlLocation = connProps.getProperty("billing.location");
      String namespace = connProps.getProperty("billing.namespace");
      String servicename = connProps.getProperty("billing.servicename");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();
      System.out.println("Account Categroy :: " + props.getProperty("account.account_category"));
      System.out.println("Currency Code :: " + props.getProperty("account.currency_code"));

      XMLGregorianCalendar sysdate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles")));
      XMLGregorianCalendar activeDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(2011, 6, 26));

      short currencycode = Short.parseShort(props.getProperty("service.currency_code"));
      short emfconfigid = Short.parseShort(props.getProperty("service.emf_config_id"));
      short exrateclass = Short.parseShort(props.getProperty("service.exrate_class"));
      short externalaccountnotype = Short.parseShort(props.getProperty("service.external_account_no_type"));
      short externalidtype = Short.parseShort(props.getProperty("service.external_id_type"));
      short rateclassdefault = Short.parseShort(props.getProperty("service.rate_class_default"));
      short saleschannelid = Short.parseShort(props.getProperty("service.sales_channel_id"));
      short countrycode = Short.parseShort(props.getProperty("service.country_code"));
      short servicefranchisetaxcode = Short.parseShort(props.getProperty("service.franchise_tax_code"));

      CustAddress custAddress = new CustAddress();
      custAddress.setAddress1("41 E 400 N");
      custAddress.setAddress2("8");
      custAddress.setAddress3("");
      custAddress.setCity("Provo");
      custAddress.setState("Ut");
      custAddress.setZip("84606");
      custAddress.setCountryCode(countrycode);
      custAddress.setFranchiseTaxCode(servicefranchisetaxcode);
      custAddress.setCounty("");
      BillName billName = new BillName();
      billName.setFirstName("Taylor");
      billName.setMiddleName("");
      billName.setLastName("Moss");

      BillingService billingService = new BillingService();
      billingService.setAccountDateActive(sysdate);
      billingService.setAccountNo("692025");
      billingService.setCurrencyCode(currencycode);
      billingService.setEMFConfigId(emfconfigid);
      billingService.setExrateClass(exrateclass);
      billingService.setExternalAccountNoType(externalaccountnotype);
      billingService.setExternalId("2138415310");
      billingService.setExternalIdType(externalidtype);
      billingService.setRateClassDefault(rateclassdefault);
      billingService.setSalesChannelId(saleschannelid);
      billingService.setServiceAddr(custAddress);
      billingService.setServiceName(billName);
      billingService.setServiceStartDate(activeDate);
      billingService.setSysDate(sysdate);

      MessageHolder holder = port.addService("username", billingService);
      if (holder != null) {
        System.out.println("Status :: " + holder.getStatus());
        System.out.println("Message :: " + holder.getMessage());
      }

    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (DatatypeConfigurationException dce) {
      dce.printStackTrace();
    }
  }

  private void testGetActiveServices() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      // bsi = new BillingServiceInterface();
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      String accountno = "681894";

      ArrayOfServiceHolder serviceHolderList = port.getActiveService("username", accountno);
      if (serviceHolderList != null) {
        for (ServiceHolder serviceHolder : serviceHolderList.getServiceHolder()) {
          System.out.println("AccountNo   :: " + serviceHolder.getService().getAccountNo());
          System.out.println("ActiveDate  :: " + serviceHolder.getService().getActiveDate());
          System.out.println("ExternalId  :: " + serviceHolder.getService().getExternalId());
          System.out.println("ExternalIdType:: " + serviceHolder.getService().getExternalIdType());
          System.out.println("InactiveDate:: " + serviceHolder.getService().getInactiveDate());
          System.out.println("SubscrNo    :: " + serviceHolder.getService().getSubscrNo());
        }
      }
      // if( valueHolderList != null ) {
      // for( ValueHolder valueHolder : valueHolderList.getValueHolder() ) {
      // System.out.println("Status   :: "+valueHolder.getStatusMessage().getStatus());
      // System.out.println("Message  :: "+valueHolder.getStatusMessage().getMessage());
      // System.out.println("Value1   :: "+valueHolder.getValue());
      // System.out.println("Value2   :: "+valueHolder.getValue2());
      // }
      // }
    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }

  }

  private void addPackage() {

    ClassLoader cl = BillingTester.class.getClassLoader();
    String inputPropertyFile = "com/tscp/mvne/billing/defaults.properties";
    System.out.println("Loading Properties file...");
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);
      BillingServiceInterface bsi = new BillingServiceInterface();
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      XMLGregorianCalendar sysdate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles")));
      XMLGregorianCalendar activeDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(2011, 7, 26));

      int packageid = Integer.parseInt(props.getProperty("package.package_id"));
      short externalidtype = Short.parseShort(props.getProperty("package.external_id_type"));

      ArrayOfPackage packages = new ArrayOfPackage();

      com.tscp.mvne.billing.api.Package kenanPackage = new com.tscp.mvne.billing.api.Package();
      kenanPackage.setPackageId(packageid);
      kenanPackage.setExternalIdType(externalidtype);
      kenanPackage.setActiveDate(activeDate);
      kenanPackage.setAccountNo("692025");

      packages.getPackage().add(kenanPackage);

      ArrayOfValueHolder holder = port.addPackage("username", packages);
      if (holder != null) {
        if (holder.getValueHolder().size() > 0) {
          for (ValueHolder valueHolder : holder.getValueHolder()) {
            System.out.println("StatusMessage.Status  :: " + valueHolder.getStatusMessage().getStatus());
            System.out.println("StatusMessage.Message :: " + valueHolder.getStatusMessage().getMessage());
            System.out.println("PackageInstanceId     :: " + valueHolder.getValue());
            System.out.println("PackageInstanceIdServ :: " + valueHolder.getValue2());
          }
        }
      }

    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (DatatypeConfigurationException dce) {
      dce.printStackTrace();
    }

  }

  private void addComponent() {

    ClassLoader cl = BillingTester.class.getClassLoader();
    String inputPropertyFile = "com/tscp/mvne/billing/defaults.properties";
    System.out.println("Loading Properties file...");
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);
      System.out.println("Account Categroy :: " + props.getProperty("account.account_category"));
      System.out.println("Currency Code :: " + props.getProperty("account.currency_code"));
      BillingServiceInterface bsi = new BillingServiceInterface();
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      XMLGregorianCalendar sysdate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles")));
      XMLGregorianCalendar activeDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(2011, 7, 26));

      int componentid = Integer.parseInt(props.getProperty("component.reinstall.component"));
      int packageid = Integer.parseInt(props.getProperty("component.package_id"));
      short externalidtype = Short.parseShort(props.getProperty("component.external_id_type"));
      short packageinstanceidserv = 4;

      ArrayOfPkgComponent components = new ArrayOfPkgComponent();

      PkgComponent pkgComponent = new PkgComponent();
      pkgComponent.setComponentId(componentid);
      pkgComponent.setPackageInstanceId(1299459);
      pkgComponent.setPackageInstanceIdServ(packageinstanceidserv);
      pkgComponent.setComponentActiveDate(activeDate);
      pkgComponent.setExternalId("2138415310");
      pkgComponent.setExternalIdType(externalidtype);
      pkgComponent.setPackageId(packageid);

      components.getPkgComponent().add(pkgComponent);

      ArrayOfMessageHolder messageHolder = port.addComponent("username", components);
      if (messageHolder != null && messageHolder.getMessageHolder() != null
          && messageHolder.getMessageHolder().size() > 0) {
        for (MessageHolder message : messageHolder.getMessageHolder()) {
          System.out.println("Status  :: " + message.getStatus());
          System.out.println("Message :: " + message.getMessage());
        }
      }

    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (DatatypeConfigurationException dce) {
      dce.printStackTrace();
    }
  }

  private void addPayment(String accountNo) {

    String inputPropertyFile = "com/tscp/mvne/billing/api/connection.properties";
    Properties props;

    // String namespace = "http://vm2k25-dev2/BillingServiceInterface/";
    String namespace = "http://www.telscape.com/BillingServiceInterface/";
    String servicename = "BillingServiceInterface";
    // String wsdlLocation =
    // "http://vm2k25-dev2/BillingServiceInterface/BillingServiceInterface.asmx?WSDL";
    String wsdlLocation = "http://uscaelm2k24/BillingServiceInterface/BillingServiceInterface.asmx?WSDL";
    try {
      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      String externalId = accountNo;
      int externalIdType = 1;
      String amount = "1000";
      XMLGregorianCalendar transDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(TimeZone.getTimeZone("America/Los Angeles")));
      int transType = 1;
      String submitBy = "dta";

      MessageHolder message = port
          .addPayment("api", externalId, externalIdType, amount, transDate, transType, submitBy);
      System.out.println("Payment Posting Response...");
      if (message != null) {
        System.out.println("Status  :: " + message.getStatus());
        System.out.println("Message :: " + message.getMessage());
      } else {
        System.out.println("Response object is null!");
      }
      // PaymentHolder paymentHolder = port.getLastPayment("dta", externalId);
      // if( paymentHolder != null ) {
      // if( paymentHolder.getStatusMessage() != null ) {
      // System.out.println("Status    :: "+paymentHolder.getStatusMessage().getStatus());
      // System.out.println("Message   :: "+paymentHolder.getStatusMessage().getMessage());
      // }
      // if( paymentHolder.getPayment() != null ) {
      // System.out.println("Payment Type :: "+paymentHolder.getPayment().getPaymentTypeDesc());
      // System.out.println("Tracking Id  :: "+paymentHolder.getPayment().getTrackingId());
      // System.out.println("Trans Amount :: "+paymentHolder.getPayment().getTransAmount());
      // System.out.println("Trans Type   :: "+paymentHolder.getPayment().getTransType());
      // System.out.println("Trans Date   :: "+paymentHolder.getPayment().getTransDate().toString());
      // }
      // } else {
      // System.out.println("Null payments found");
      // }
    } catch (MalformedURLException urlex) {
      urlex.printStackTrace();
    } catch (DatatypeConfigurationException dce) {
      dce.printStackTrace();
    }
  }

  private void addTransaction() {
    PaymentTransaction trans = new PaymentTransaction();

    trans.setSessionId("asdsad233214assa");
    // trans.setPaymentAmount(20.00);
    trans.setPmtId(0);

    trans.savePaymentTransaction();

    System.out.println("Trans ID :: " + trans.getTransId());
  }

  private void portAvailabilityCheck() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      ValueHolder value = port.getAccountNo("usernmae", "2123884164");
      if (value != null) {
        System.out.println("Status    :: " + value.getStatusMessage().getStatus());
        System.out.println("Message   :: " + value.getStatusMessage().getMessage());
        System.out.println("Value     :: " + value.getValue());
        System.out.println("Value2    :: " + value.getValue2());
      }
      // port.updateThreshold(username, externalId, externalIdType,
      // newThreshold)

      PaymentHolder paymentHolder = port.getLastPayment("username", "681688");
      if (paymentHolder != null) {
        System.out.println("TransType    :: " + paymentHolder.getPayment().getPaymentTypeDesc());
        System.out.println("TrackingId    :: " + paymentHolder.getPayment().getTrackingId());
        System.out.println("TransAmount  :: " + paymentHolder.getPayment().getTransAmount());
        System.out.println("TransDate    :: " + paymentHolder.getPayment().getTransDate());
        System.out.println("TransType    :: " + paymentHolder.getPayment().getTransType());
      }
    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }
  }

  private void getPaymentList(String accountNo) {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      ArrayOfPaymentHolder paymentHolderList = port.getCompletePaymentHistory("username", accountNo);
      System.out.println("Payment List for account " + accountNo + "....");
      for (PaymentHolder paymentHolder : paymentHolderList.getPaymentHolder()) {
        System.out.println("TransType    :: " + paymentHolder.getPayment().getPaymentTypeDesc());
        System.out.println("TrackingId   :: " + paymentHolder.getPayment().getTrackingId());
        System.out.println("TransAmount  :: " + paymentHolder.getPayment().getTransAmount());
        System.out.println("TransDate    :: " + paymentHolder.getPayment().getTransDate());
        System.out.println("TransType    :: " + paymentHolder.getPayment().getTransType());
      }

      // port.getEmail("dtat", "681894");

    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }
  }

  private void getContactInfo() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      ContactInfoHolder valueHolder = port.getContactInfo("dtat", "681894");
      if (valueHolder != null) {
        System.out.println("Status   :: " + valueHolder.getStatusMessage().getStatus());
        System.out.println("Message  :: " + valueHolder.getStatusMessage().getMessage());
        System.out.println("Value1   :: " + valueHolder.getContactInfo().getContact1Name());
        System.out.println("Value2   :: " + valueHolder.getContactInfo().getContact1Phone());
      }

    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }
  }

  private void updateThresholdValue(String tn, String threshold) {
    BillingSystem billingImpl = new BillingSystem();
    ServiceInstance serviceInstance = new ServiceInstance();
    serviceInstance.setExternalid(tn);
    serviceInstance.setExternalidtype(3);
    billingImpl.updateServiceInstanceStatus(serviceInstance, threshold);
  }

  public void getUsageList() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      // bsi = new BillingServiceInterface();
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();
      UsageHolder usageHolder = port.getUnbilledDataMBs("username", "2138043419");
      // ArrayOfPaymentHolder paymentHolderList =
      // port.getCompletePaymentHistory("username", "681688");
      if (usageHolder != null) {
        if (usageHolder.getStatusMessage() != null) {
          if (usageHolder.getStatusMessage().getStatus().equals("SUCCESS")) {
            System.out.println("DollarUsage    :: " + usageHolder.getUsage().getDollarUsage());
            System.out.println("ExternalId     :: " + usageHolder.getUsage().getExternalId());
            System.out.println("Rate           :: " + usageHolder.getUsage().getRate());
            System.out.println("Usage          :: " + usageHolder.getUsage().getUsage());
          } else if (usageHolder.getStatusMessage().getStatus().equals("")) {
            System.out.println("Empty Status...");
          } else {
            System.out.println("Status    :: " + usageHolder.getStatusMessage().getStatus());
            System.out.println("Message   :: " + usageHolder.getStatusMessage().getMessage());
          }
        }
      }
    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }

  }

  public void getCustBalance() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      // bsi = new BillingServiceInterface();
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();
      CustBalanceHolder valueHolder = port.getCurrentBalance("dta", "698177");
      if (valueHolder != null) {
        System.out.println("Status    :: " + valueHolder.getStatusMessage().getStatus());
        System.out.println("Message   :: " + valueHolder.getStatusMessage().getMessage());
        System.out.println("RealBalance :: " + valueHolder.getCustBalance().getRealBalance());
        System.out.println("OverageCharges:: " + valueHolder.getCustBalance().getTotalOverageCharges());
        System.out.println("KenanBalance :: " + valueHolder.getCustBalance().getKenanBalance());
        DecimalFormat df = new DecimalFormat("0.00");
        Currency currency = df.getCurrency();
        System.out.println("Currency Symbol " + currency.getSymbol());
        System.out.println("Currency.ToString :: " + currency.toString());
        System.out.println("Formatted Balance :: " + df.format((valueHolder.getCustBalance().getRealBalance() * -1)));

      }
    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }

  }

  public void updateCustAddress() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      // bsi = new BillingServiceInterface();
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();
      // ValueHolder valueHolder = port.getCurrentBalance("dta", "681893");

      String accountno = "681938";// "689555";
      CustAddress address = new CustAddress();
      // port.getBillingAddress("username", accountno);
      ServiceAddressHolder serviceAddressHolder = port.getServiceAddress("username", accountno, "2138229618");
      System.out.println("Address1 :: " + serviceAddressHolder.getServiceAddress().getAddress1());
      System.out.println("Address2 :: " + serviceAddressHolder.getServiceAddress().getAddress2());
      System.out.println("Address3 :: " + serviceAddressHolder.getServiceAddress().getAddress3());
      System.out.println("City  :: " + serviceAddressHolder.getServiceAddress().getCity());
      System.out.println("State :: " + serviceAddressHolder.getServiceAddress().getState());
      System.out.println("Zip   :: " + serviceAddressHolder.getServiceAddress().getZip());

      // address = serviceAddressHolder.getServiceAddress();

      address.setAddress1("355 S Grand Ave");
      address.setAddress2("ste 3100");
      address.setCity("Los Angeles");
      address.setState("CA");
      address.setZip("90071");

      CustAddrInfo custAddrInfo = new CustAddrInfo();
      custAddrInfo.setAccountNo(accountno);
      custAddrInfo.setExternalAccountNoType((short) 1);
      custAddrInfo.setExternalIdType((short) 3);
      custAddrInfo.setServiceAddr(address);
      custAddrInfo.setExternalId("2138226001");
      custAddrInfo.setServiceAddr(address);
      // custAddrInfo.setServiceAddr()
      MessageHolder messageHolder = port.updateServiceAddress("username", custAddrInfo);
      printOutMessageHolder(messageHolder);
      messageHolder = port.updateBillingAddress("username", accountno, address);
      printOutMessageHolder(messageHolder);
      // BillName billName = new BillName();
      // billName.setFirstName("Vincent");
      // billName.setLastName("Freeman");
      // MessageHolder messageHolder = port.updateBillName("username",
      // accountno, billName );
    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }

  }

  public void updateEmailAddress() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      // bsi = new BillingServiceInterface();
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      String accountno = "689555";

      ContactInfo contactInfo = new ContactInfo();
      contactInfo.setContact1Phone("2138675309");
      ContactInfoHolder contactInfoHolder = port.getContactInfo("username", accountno);
      System.out.println("Contact1Name   :: " + contactInfoHolder.getContactInfo().getContact1Name());
      System.out.println("Contact1Phone  :: " + contactInfoHolder.getContactInfo().getContact1Phone());
      ValueHolder valueHolder = port.getEmail("username", accountno);
      System.out.println("Status    :: " + valueHolder.getStatusMessage().getStatus());
      System.out.println("Message   :: " + valueHolder.getStatusMessage().getMessage());
      System.out.println("Value     :: " + valueHolder.getValue());
      System.out.println("Value2    :: " + valueHolder.getValue2());
      MessageHolder messageHolder = port.updateEmail("username", accountno, "ghamlett@telscape.net");
      printOutMessageHolder(messageHolder);

      valueHolder = port.getEmail("username", accountno);
      System.out.println("Status    :: " + valueHolder.getStatusMessage().getStatus());
      System.out.println("Message   :: " + valueHolder.getStatusMessage().getMessage());
      System.out.println("Value     :: " + valueHolder.getValue());
      System.out.println("Value2    :: " + valueHolder.getValue2());
      // MessageHolder messageHolder = port.updateBillName("username",
      // accountno, billName );
    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }
  }

  public void updateContactInfo() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation),
          new QName(namespace, servicename));
      // bsi = new BillingServiceInterface();
      BillingServiceInterfaceSoap port = bsi.getBillingServiceInterfaceSoap();

      String accountno = "689555";

      ContactInfo contactInfo = new ContactInfo();
      contactInfo.setContact1Phone("2138675309");
      ContactInfoHolder contactInfoHolder = port.getContactInfo("username", accountno);
      System.out.println("Contact1Name   :: " + contactInfoHolder.getContactInfo().getContact1Name());
      System.out.println("Contact1Phone  :: " + contactInfoHolder.getContactInfo().getContact1Phone());
      MessageHolder messageHolder = port.updateContactInfo("username", accountno, contactInfo);
      printOutMessageHolder(messageHolder);

      contactInfoHolder = port.getContactInfo("username", accountno);
      System.out.println("Contact1Name   :: " + contactInfoHolder.getContactInfo().getContact1Name());
      System.out.println("Contact1Phone  :: " + contactInfoHolder.getContactInfo().getContact1Phone());
      BillName billName = new BillName();
      billName.setFirstName("Gary");
      billName.setLastName("Hamlett");
      messageHolder = port.updateBillName("username", accountno, billName);
      printOutMessageHolder(messageHolder);
    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }
  }

  private void printOutMessageHolder(MessageHolder messageHolder) {
    if (messageHolder != null) {
      System.out.println("Status    :: " + messageHolder.getStatus());
      System.out.println("Message   :: " + messageHolder.getMessage());
      // System.out.println("Value     :: "+valueHolder.getValue());
      // System.out.println("Value 2   :: "+valueHolder.getValue2());
    }
  }

  private BillingServiceInterfaceSoap getBillingService() {

    String inputPropertyFile = "config/connection.tscpmvne.properties";
    ClassLoader cl = BillingTester.class.getClassLoader();
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    Properties props = new Properties();
    try {
      props.load(in);

      String namespace = props.getProperty("billing.namespace");
      String servicename = props.getProperty("billing.servicename");
      String wsdlLocation = props.getProperty("billing.location");

      if (port == null) {
        BillingServiceInterface bsi = new BillingServiceInterface(new URL(wsdlLocation), new QName(namespace,
            servicename));
        port = bsi.getBillingServiceInterfaceSoap();
      }
      return port;
    } catch (IOException ioe) {
      System.out.println("Error loading properties file...");
      ioe.printStackTrace();
    }
    return null;
  }
}
