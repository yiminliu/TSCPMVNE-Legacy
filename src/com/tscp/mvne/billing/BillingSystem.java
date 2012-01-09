package com.tscp.mvne.billing;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.hibernate.Query;
import org.hibernate.classic.Session;

import com.tscp.mvne.billing.api.ArrayOfMessageHolder;
import com.tscp.mvne.billing.api.ArrayOfPackage;
import com.tscp.mvne.billing.api.ArrayOfPackageHolder;
import com.tscp.mvne.billing.api.ArrayOfPaymentHolder;
import com.tscp.mvne.billing.api.ArrayOfPkgComponent;
import com.tscp.mvne.billing.api.ArrayOfServiceHolder;
import com.tscp.mvne.billing.api.ArrayOfValueHolder;
import com.tscp.mvne.billing.api.BillName;
import com.tscp.mvne.billing.api.BillNameHolder;
import com.tscp.mvne.billing.api.BillingAccount;
import com.tscp.mvne.billing.api.BillingAddressHolder;
import com.tscp.mvne.billing.api.BillingService;
import com.tscp.mvne.billing.api.BillingServiceInterface;
import com.tscp.mvne.billing.api.BillingServiceInterfaceSoap;
import com.tscp.mvne.billing.api.ContactInfo;
import com.tscp.mvne.billing.api.ContactInfoHolder;
import com.tscp.mvne.billing.api.CustAddress;
import com.tscp.mvne.billing.api.CustBalanceHolder;
import com.tscp.mvne.billing.api.MessageHolder;
import com.tscp.mvne.billing.api.PackageHolder;
import com.tscp.mvne.billing.api.PaymentHolder;
import com.tscp.mvne.billing.api.PkgComponent;
import com.tscp.mvne.billing.api.ServiceHolder;
import com.tscp.mvne.billing.api.UsageHolder;
import com.tscp.mvne.billing.api.ValueHolder;
import com.tscp.mvne.billing.exception.BillingException;
import com.tscp.mvne.billing.exception.ServiceProvisionedException;
import com.tscp.mvne.hibernate.HibernateUtil;

public class BillingSystem implements BillingInterface {

  /**
   * 1 = Port 5 = CR-D 220 = Pre-Paid
   * 
   */
  private short discReason = 5;

  private static String inputPropertyFile = "com/tscp/mvne/config/truConnectDefaults.properties";
  private static String connectionPropertyFile = "com/tscp/mvne/config/connection.tscpmvne.properties";

  private static String namespace;
  private static String servicename;
  private static String wsdlLocation;

  private static Properties props;
  private static Properties connProps;

  public static String SERVICE_INSTANCE_RESTORED = "0";
  public static String SERVICE_INSTANCE_HOTLINED = "5";
  public static String SERVICE_INSTANCE_FAILED_PMT = "7";

  public static int COMPONENT_REINSTALL = 500001;

  BillingServiceInterface bsi;
  BillingServiceInterfaceSoap port;

  public BillingSystem() {
    loadDefaults();
    try {
      bsi = new BillingServiceInterface(new URL(wsdlLocation), new QName(namespace, servicename));
    } catch (MalformedURLException url_ex) {
      bsi = new BillingServiceInterface();
    }
    port = bsi.getBillingServiceInterfaceSoap();
  }

  @Override
  public int createAccount(Account account) throws BillingException {
    // default the values in Kenan to the properties file declaration
    BillingAccount billingAccount = getBillingAccountDefault();

    billingAccount.getBillName().setFirstName(account.getFirstname());
    billingAccount.getBillName().setMiddleName(account.getMiddlename());
    billingAccount.getBillName().setLastName(account.getLastname());

    billingAccount.getCustAddress().setAddress1(account.getContact_address1());
    billingAccount.getCustAddress().setAddress2(account.getContact_address2());
    billingAccount.getCustAddress().setCity(account.getContact_city());
    billingAccount.getCustAddress().setState(account.getContact_state());
    billingAccount.getCustAddress().setZip(account.getContact_zip());

    billingAccount.getBillAddress().setAddress1(account.getContact_address1());
    billingAccount.getBillAddress().setAddress2(account.getContact_address2());
    billingAccount.getBillAddress().setCity(account.getContact_city());
    billingAccount.getBillAddress().setState(account.getContact_state());
    billingAccount.getBillAddress().setZip(account.getContact_zip());

    billingAccount.setCustEmail(account.getContact_email());

    billingAccount.setCustPhone1(account.getContact_number());

    ValueHolder response = port.addAccount("username", billingAccount);
    if (response == null) {
      throw new BillingException("createAccount", "No response returned from Billing System...");
    } else {
      if (response.getValue() == null || response.getValue().trim().length() == 0) {
        if (response.getStatusMessage() != null) {
          throw new BillingException("createAccount", "Account Number has not been returned.."
              + response.getStatusMessage().getStatus() + " " + response.getStatusMessage().getMessage());
        }
        throw new BillingException("createAccount", "Account Number has not been returned..");
      } else {
        account.setAccountno(Integer.parseInt(response.getValue().trim()));
      }
    }

    return account.getAccountno();
  }

  @Override
  public void updateAccount(Account account) throws BillingException {
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("updateAccount", "Please Specify and account to update...");
    }
    if (account.getContact_email() != null) {
      MessageHolder message = port.updateEmail("username", Integer.toString(account.getAccountno()), account
          .getContact_email());
      if (message == null) {
        throw new BillingException("updateAccount", "Error updating account " + account.getAccountno()
            + "...No response returned from billing system.");
      } else {
        if (!message.getStatus().equals("Success")) {
          throw new BillingException("updateAccount", "Error updating email address for account "
              + account.getAccountno() + ". Returned message is " + message.getMessage());
        }
      }
    }
  }

  @Override
  public void addServiceInstance(Account account, ServiceInstance serviceinstance) throws BillingException {
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("addServiceInstance", "Error adding service to unknown Account");
    }
    if (serviceinstance == null || serviceinstance.getExternalid() == null
        || serviceinstance.getExternalid().trim().length() <= 0) {
      throw new BillingException("addServiceInstance", "Please include a service to be added...");
    }
    if (account.getFirstname() == null || account.getFirstname().trim().length() == 0) {
      bindAccountObject(account);
    }
    boolean contains = false;
    // if( account.getServiceinstancelist().contains(arg0))
    for (ServiceInstance si : account.getServiceinstancelist()) {
      if (si.getExternalid().equals(serviceinstance.getExternalid().trim())) {
        contains = true;
        break;
      }
    }
    if (!contains) {
      BillingService billingService = getBillingServiceDefaults();
      billingService.setAccountNo(Integer.toString(account.getAccountno()));

      billingService.getServiceName().setFirstName(account.getFirstname());
      billingService.getServiceName().setMiddleName(account.getMiddlename());
      billingService.getServiceName().setLastName(account.getLastname());

      billingService.getServiceAddr().setAddress1(account.getContact_address1());
      billingService.getServiceAddr().setAddress2(account.getContact_address2());
      billingService.getServiceAddr().setCity(account.getContact_city());
      billingService.getServiceAddr().setState(account.getContact_state());
      billingService.getServiceAddr().setZip(account.getContact_zip());

      billingService.setExternalId(serviceinstance.getExternalid().trim());

      MessageHolder message = port.addService("username", billingService);
      if (message == null) {
        throw new BillingException("addServiceInstance", "No response returned from foreign billing system.");
      } else {
        if (!message.getStatus().equals("Success")) {
          throw new BillingException("addServiceInstance", "Error adding ServiceInstance "
              + serviceinstance.getExternalid() + " to account " + account.getAccountno() + "..."
              + message.getMessage());
        }
      }
    } else {
      System.out.println("Service is already associated with this account...Skipping add...");
      throw new ServiceProvisionedException("Service is already associated with this account...Skipping add...");
    }
  }

  @Override
  public void addPackage(Account account, ServiceInstance serviceinstance, Package iPackage) throws BillingException {
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("addServiceInstance", "Error adding service to unknown Account");
    }
    if (serviceinstance == null || serviceinstance.getExternalid() == null
        || serviceinstance.getExternalid().trim().length() <= 0) {
      throw new BillingException("addServiceInstance", "Please include a service to be added...");
    }
    com.tscp.mvne.billing.api.Package kenanPackage = getPackageDefaults();

    kenanPackage.setAccountNo(Integer.toString(account.getAccountno()));

    if (iPackage == null) {
      iPackage = new Package();
      iPackage.setPackageid(0);
    }
    if (iPackage != null && iPackage.getPackageid() > 0) {
      kenanPackage.setPackageId(iPackage.getPackageid());
    } else {
      iPackage.setPackageid(kenanPackage.getPackageId());
    }

    ArrayOfPackage packages = new ArrayOfPackage();
    packages.getPackage().add(kenanPackage);

    ArrayOfValueHolder valueHolder = port.addPackage("username", packages);
    if (valueHolder == null) {
      throw new BillingException("addPackage", "No response returned from billing system");
    }
    if (valueHolder.getValueHolder() != null && valueHolder.getValueHolder().size() > 0) {
      for (ValueHolder value : valueHolder.getValueHolder()) {
        if (value.getStatusMessage().getStatus().equals("Success")) {
          System.out.println("Added Package " + iPackage.getPackageid() + " :: Kenan returned ID -> "
              + value.getValue().trim() + " with IDServ -> " + value.getValue2());
          iPackage.setPackage_instance_id(Integer.parseInt(value.getValue().trim()));
          iPackage.setPackage_instance_id_serv(value.getValue2().trim());
        } else {
          throw new BillingException("addPackage", "Error adding package " + kenanPackage.getPackageId()
              + " to Account " + account.getAccountno() + ". Returned message is "
              + value.getStatusMessage().getMessage());
        }
      }
    }

  }

  @Override
  public void addComponent(Account account, ServiceInstance serviceinstance, Package iPackage, Component componentid)
      throws BillingException {
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("addServiceInstance", "Error adding service to unknown Account");
    }
    if (serviceinstance == null || serviceinstance.getExternalid() == null
        || serviceinstance.getExternalid().trim().length() <= 0) {
      throw new BillingException("addServiceInstance", "Please include a service to be added...");
    }
    if (iPackage == null || iPackage.getPackage_instance_id() == 0) {
      throw new BillingException("addComponent", "Component must be added to a valid Package");
    }
    PkgComponent pkgComponent = getComponentDefaults();
    if (componentid == null) {
      componentid = new Component();
      componentid.setComponent_id(0);
    }
    if (componentid != null && componentid.getComponent_id() > 0) {
      pkgComponent.setComponentId(componentid.getComponent_id());
    } else {
      componentid.setComponent_id(pkgComponent.getComponentId());
    }
    pkgComponent.setExternalId(serviceinstance.getExternalid());
    if (iPackage != null && iPackage.getPackageid() > 0) {
      pkgComponent.setPackageId(iPackage.getPackageid());
    }
    pkgComponent.setPackageInstanceId(iPackage.getPackage_instance_id());
    pkgComponent.setPackageInstanceIdServ(Short.parseShort(iPackage.getPackage_instance_id_serv()));

    ArrayOfPkgComponent componentList = new ArrayOfPkgComponent();
    componentList.getPkgComponent().add(pkgComponent);

    ArrayOfMessageHolder messageHolder = port.addComponent("username", componentList);
    if (messageHolder == null) {
      throw new BillingException("addPackage", "No response returned from billing system");
    }
    if (messageHolder.getMessageHolder() != null && messageHolder.getMessageHolder().size() > 0) {
      for (MessageHolder message : messageHolder.getMessageHolder()) {
        if (message.getStatus().equals("Success")) {
        } else {
          throw new BillingException("addPackage", "Error adding component " + pkgComponent.getComponentId()
              + " to Account " + account.getAccountno() + ". Returned message is " + message.getMessage());
        }
      }
    }
  }

  @Override
  public void deleteServiceInstance(Account account, ServiceInstance serviceinstance) throws BillingException {
    System.out.println("Disconnecting Service on Account " + account.getAccountno() + " and ServiceInstance "
        + serviceinstance.getExternalid());
    if (account == null || account.getAccountno() == 0) {
      throw new BillingException("Please specify an account to delete this service against");
    }
    if (serviceinstance == null || serviceinstance.getExternalid() == null
        || serviceinstance.getExternalid().trim().length() == 0) {
      throw new BillingException("deleteServiceInstance", "Please specify a service to be disconnected...");
    }
    MessageHolder message = port.disconnectServicePackages("username", Integer.toString(account.getAccountno()),
        serviceinstance.getExternalid(), serviceinstance.getExternalidtype(), sysdate(), discReason);
    // MessageHolder message = port.disconnectService("username",
    // serviceinstance.getExternalid(), serviceinstance.getExternalidtype(),
    // sysdate(), discReason);
    if (message == null) {
      throw new BillingException("deleteServiceInstance", "No response returned from foreign billing system.");
    } else {
      System.out.println("Status :: " + message.getStatus());
      System.out.println("Msg    :: " + message.getMessage());
      if (!message.getStatus().equals("Success")) {
        throw new BillingException("deleteServiceInstance", "Error deleting ServiceInstance "
            + serviceinstance.getExternalid() + " to account " + account.getAccountno() + "..." + message.getMessage());
      }
    }
    System.out.println("Done Disconnecting Service on Account " + account.getAccountno() + " and ServiceInstance "
        + serviceinstance.getExternalid());
  }

  public int getAccountNoByTN(String TN) {
    System.out.println("Get Account by TN :: " + TN);
    ValueHolder value = port.getAccountNo("usernmae", TN);
    if (value != null) {
      System.out.println("Status    :: " + value.getStatusMessage().getStatus());
      System.out.println("Message   :: " + value.getStatusMessage().getMessage());
      System.out.println("Value     :: " + value.getValue());
      System.out.println("Value2    :: " + value.getValue2());
      if (value.getValue() != null) {
        return Integer.parseInt(value.getValue());
      }
    }
    return 0;

  }

  @Override
  public Account getAccountByAccountNo(int account_no) throws BillingException {
    System.out.println("tc! getting accountInfo with wsdl " + wsdlLocation);
    BillName billName = getBillName(account_no);
    CustAddress custAddress = getCustAddress(account_no);

    Account lAccount = new Account();
    lAccount.setAccountno(account_no);
    lAccount.setFirstname(billName.getFirstName());
    lAccount.setLastname(billName.getLastName());

    lAccount.setBalance(getBalance(account_no));

    lAccount.setContact_email(getEmail(account_no));
    lAccount.setContact_number(getContactNumber(account_no));

    lAccount.setContact_address1(custAddress.getAddress1());
    lAccount.setContact_address2(custAddress.getAddress2());
    lAccount.setContact_city(custAddress.getCity());
    lAccount.setContact_state(custAddress.getState());
    lAccount.setContact_zip(custAddress.getZip());
    lAccount.setPackageList(getPackageList(lAccount, null));
    lAccount.setServiceinstancelist(getServiceInstanceList(lAccount));
    return lAccount;
  }

  @Override
  public List<ServiceInstance> getServiceInstanceList(Account account) throws BillingException {
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("getServiceInstanceList", "Account information must be populated.");
    }
    try {
      ArrayOfServiceHolder serviceHolderList = port.getActiveService("username", Integer.toString(account
          .getAccountno()));
      if (serviceHolderList != null) {
        Vector<ServiceInstance> serviceInstanceList = new Vector<ServiceInstance>();
        for (ServiceHolder serviceHolder : serviceHolderList.getServiceHolder()) {
          ServiceInstance serviceInstance = new ServiceInstance();
          serviceInstance.setExternalid(serviceHolder.getService().getExternalId());
          serviceInstance.setExternalidtype(serviceHolder.getService().getExternalIdType());
          serviceInstance.setSubscrno(Integer.parseInt(serviceHolder.getService().getSubscrNo()));
          serviceInstanceList.add(serviceInstance);
        }
        return serviceInstanceList;
      }
    } catch (WebServiceException ws_ex) {
      System.out.println("WS Exception thrown when calling getActiveService(\"username\"," + account.getAccountno()
          + ")...." + ws_ex.getMessage());
      throw new BillingException("Error retrieving Service Instance information:" + ws_ex.getMessage());
    }

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_active_services");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", account.getAccountno());

    @SuppressWarnings("unchecked")
    List<ServiceInstance> serviceInstanceList = (List<ServiceInstance>) q.list();

    for (ServiceInstance si : serviceInstanceList) {
      System.out.println("ExternalId  :: " + si.getExternalid());
      System.out.println("SubscrNo    :: " + si.getSubscrno());
    }

    session.getTransaction().rollback();
    return serviceInstanceList;
  }

  @Override
  public List<Package> getPackageList(Account account, ServiceInstance serviceinstance) throws BillingException {
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("getPackageList", "Account information not populated...");
    }
    try {
      ArrayOfPackageHolder arrayOfPackages = port.getListActivePackages("username", Integer.toString(account
          .getAccountno()));
      if (arrayOfPackages != null) {
        Vector<Package> packageList = new Vector<Package>();
        for (PackageHolder packageHolder : arrayOfPackages.getPackageHolder()) {
          Package tscpPackage = new Package();
          tscpPackage.setActive_date(packageHolder.getPackage().getActiveDate().toGregorianCalendar().getTime());
          tscpPackage.setInactive_date(packageHolder.getPackage().getDiscDate().toGregorianCalendar().getTime());
          tscpPackage.setPackage_instance_id(packageHolder.getPackage().getPackageInstanceId());
          tscpPackage
              .setPackage_instance_id_serv(Short.toString(packageHolder.getPackage().getPackageInstanceIdServ()));
          tscpPackage.setPackage_name(packageHolder.getPackage().getPackageName());
          tscpPackage.setPackageid(packageHolder.getPackage().getPackageId());
          // tscpPackage.setComponentlist(getComponentList(account,null,tscpPackage));
          packageList.add(tscpPackage);
        }
        return packageList;
      }
    } catch (WebServiceException ws_ex) {
      System.out.println("WS Exception thrown when calling getListActivePackages(\"username\","
          + account.getAccountno() + ")...." + ws_ex.getMessage());
      throw new BillingException("Error retrieving Package information:" + ws_ex.getMessage());
    }

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_active_packages");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", account.getAccountno());

    @SuppressWarnings("unchecked")
    List<com.tscp.mvne.billing.Package> packageList = q.list();

    for (com.tscp.mvne.billing.Package acctPackage : packageList) {
      System.out.println("PackageId   :: " + acctPackage.getPackageid());
      System.out.println("PackageName :: " + acctPackage.getPackage_name());
      System.out.println("PackageInstanceId :: " + acctPackage.getPackage_instance_id());
    }

    session.getTransaction().rollback();
    return packageList;
  }

  @Override
  public List<Component> getComponentList(Account account, ServiceInstance serviceinstance, Package packageinstance)
      throws BillingException {
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("getComponentList", "Account information not populated...");
    }
    if (serviceinstance == null || serviceinstance.getExternalid() == null
        || serviceinstance.getExternalid().trim().length() == 0) {
      throw new BillingException("getComponentList", "Service information not populated.");
    }

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_active_components");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", account.getAccountno());
    q.setParameter("in_external_id", serviceinstance.getExternalid());

    @SuppressWarnings("unchecked")
    List<Component> componentList = q.list();
    // for ( Component component : componentList ) {
    // System.out.println("Component_id     :: "+component.getComponent_id());
    // System.out.println("Component_Name   :: "+component.getComponent_name());
    // System.out.println("Comp_Instance_id :: "+component.getComponent_instance_id());
    // System.out.println("Active_Date :: "+component.getActive_date());
    // }

    session.getTransaction().rollback();
    return componentList;
  }

  public void addPayment(Account account, String paymentAmount) throws BillingException {

    String externalId = Integer.toString(account.getAccountno());
    int externalIdType = 1;
    String amount = paymentAmount;
    XMLGregorianCalendar transDate = sysdate();
    int transType = Integer.parseInt(props.getProperty("payment.trans_type"));
    String submitBy = "tcweb";

    MessageHolder message = port.addPayment("api", externalId, externalIdType, amount, transDate, transType, submitBy);
    if (message != null) {
      System.out.println("Status  :: " + message.getStatus());
      System.out.println("Message :: " + message.getMessage());
      if (!message.getStatus().equals("Success")) {
        throw new BillingException("addPayment", "Error adding Payment $" + Double.parseDouble(paymentAmount) / 100
            + " to Account " + account.getAccountno() + ". Return Message is :: " + message.getMessage());
      }
    } else {
      throw new BillingException("addPayment", "No Response from the Billing Unit...");
    }

  }

  public void updateServiceInstanceStatus(ServiceInstance serviceInstance, String newThreshold) throws BillingException {
    if (serviceInstance == null || serviceInstance.getExternalid() == null
        || serviceInstance.getExternalid().trim().length() == 0) {
      throw new BillingException("Valid service instance required");
    }
    if (serviceInstance.getExternalidtype() == 0) {
      throw new BillingException("Invalid External ID Type value...");
    }
    if (newThreshold != null
        && (newThreshold.trim().length() == 0 || (!newThreshold.equals("0") && !newThreshold.equals("5") && !newThreshold
            .equals("7")))) {
      throw new BillingException("Invalid Threshold Value");
    }
    MessageHolder messageHolder = port.updateThreshold("username", serviceInstance.getExternalid(), serviceInstance
        .getExternalidtype(), newThreshold);
    if (messageHolder == null) {
      throw new BillingException("No response from billing system");
    } else {
      if (!messageHolder.getStatus().equals("Success")) {
        throw new BillingException("Billing System error: " + messageHolder.getMessage());
      } else {
        System.out.println("Service " + serviceInstance.getExternalid() + " with external_id_type "
            + serviceInstance.getExternalidtype() + " has been updated with new threshold value of " + newThreshold);
      }
    }
  }

  public List<PaymentHolder> getCompletePaymentHistory(Account account) {
    ArrayOfPaymentHolder paymentHolderList = port.getCompletePaymentHistory("username", Integer.toString(account
        .getAccountno()));
    return paymentHolderList.getPaymentHolder();
  }

  public UsageHolder getUnbilledUsageSummary(ServiceInstance serviceInstance) {
    UsageHolder usageHolder = port.getUnbilledDataMBs("username", serviceInstance.getExternalid());
    return usageHolder;
  }

  public void updateAccountEmailAddress(Account account) {
    if (account.getAccountno() <= 0) {
      throw new BillingException("Account number must be populated.");
    }
    if (account.getContact_email() == null || account.getContact_email().trim().length() == 0) {
      throw new BillingException("Email address cannot be empty.");
    }
    MessageHolder messageHolder = port.updateEmail("system", Integer.toString(account.getAccountno()), account
        .getContact_email());
    if (messageHolder != null) {
      System.out.println("Status    :: " + messageHolder.getStatus());
      System.out.println("Message   :: " + messageHolder.getMessage());
    } else {
      throw new BillingException("No response from billing system.");
    }
  }

  private static void loadDefaults() {
    System.out.println("Loading billing system configuration");
    ClassLoader cl = BillingSystem.class.getClassLoader();
    System.out.println("TC! Loading Properties file...");
    InputStream in = cl.getResourceAsStream(inputPropertyFile);
    System.out.println("retrieved " + inputPropertyFile + " as stream");
    props = new Properties();
    try {
      props.load(in);

      System.out.println("tc! loaded properties file with package: " + props.getProperty("package.package_id"));

      BillingSystem.SERVICE_INSTANCE_FAILED_PMT = props.getProperty("service.failed_pmt",
          BillingSystem.SERVICE_INSTANCE_FAILED_PMT);
      BillingSystem.SERVICE_INSTANCE_HOTLINED = props.getProperty("service.hotlined",
          BillingSystem.SERVICE_INSTANCE_HOTLINED);
      BillingSystem.SERVICE_INSTANCE_RESTORED = props.getProperty("service.restored",
          BillingSystem.SERVICE_INSTANCE_RESTORED);
      BillingSystem.COMPONENT_REINSTALL = Integer.parseInt(props.getProperty("component.reinstall.component", Integer
          .toString(BillingSystem.COMPONENT_REINSTALL)));

      // System.out.println("Account Categroy :: "+props.getProperty("account.account_category"));
      // System.out.println("Currency Code :: "+props.getProperty("account.currency_code"));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    in = cl.getResourceAsStream(connectionPropertyFile);
    System.out.println("retrieved " + connectionPropertyFile + " as stream");
    connProps = new Properties();
    try {
      connProps.load(in);

      System.out.println("tc! loaded properties file with wsdl: " + connProps.getProperty("billing.location"));
      namespace = connProps.getProperty("billing.namespace");
      servicename = connProps.getProperty("billing.servicename");
      wsdlLocation = connProps.getProperty("billing.location");

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  private BillingAccount getBillingAccountDefault() throws BillingException {
    if (props == null) {
      throw new BillingException("getBillingAccountDefault",
          "Default file is not bound to application...account creation not possible");
    } else {

      BillingAccount billingAccount = new BillingAccount();

      billingAccount.setAccountCategory(Short.parseShort(props.getProperty("account.account_category")));
      billingAccount.setBillDispMethod(Short.parseShort(props.getProperty("account.bill_disp_meth")));
      billingAccount.setBillFormatOpt(Integer.parseInt(props.getProperty("account.bill_fmt_opt")));

      BillName billname = new BillName();
      billname.setFirstName("Shell");
      billname.setMiddleName("");
      billname.setLastName("Account");
      billingAccount.setBillName(billname);

      billingAccount.setBillPeriod(props.getProperty("account.bill_period"));
      billingAccount.setCCardIdServ(Short.parseShort(props.getProperty("account.default_ccard_id_serv")));
      billingAccount.setCollectionIndicator(Short.parseShort(props.getProperty("account.collection_indicator")));

      ContactInfo contactinfo = new ContactInfo();
      contactinfo.setContact1Name("");
      contactinfo.setContact1Phone("");
      billingAccount.setContactInfo(contactinfo);

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
      billingAccount.setCustAddress(custAddress);

      // BillAddress billingAddress = new BillingAddress();
      billingAccount.setBillAddress(custAddress);

      billingAccount.setCustEmail("tscwebgeek@telscape.net");
      billingAccount.setCustFaxNo("");
      billingAccount.setCustPhone1("2133880022");
      billingAccount.setCustPhone2("");

      billingAccount.setExrateClass(Short.parseShort(props.getProperty("account.exrate_class")));
      billingAccount.setExternalAccountNoType(Short.parseShort(props.getProperty("account.account_type")));
      billingAccount.setInsertGrpId(Short.parseShort(props.getProperty("account.insert_grp_id")));

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
        billingAccount.setSysDate(value);
        billingAccount.setAccountDateActive(value);
      } catch (DatatypeConfigurationException dce) {
        dce.printStackTrace();
      }
      return billingAccount;
    }
  }

  private BillingService getBillingServiceDefaults() {
    if (props == null) {
      throw new BillingException("getBillingServiceDefaults",
          "Default file is not bound to application...service default creation not possible");
    }
    try {
      XMLGregorianCalendar sysdate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles")));

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
      custAddress.setAddress1("");
      custAddress.setAddress2("");
      custAddress.setAddress3("");
      custAddress.setCity("");
      custAddress.setState("");
      custAddress.setZip("");
      custAddress.setCountryCode(countrycode);
      custAddress.setFranchiseTaxCode(servicefranchisetaxcode);
      custAddress.setCounty("");
      BillName billName = new BillName();
      billName.setFirstName("");
      billName.setMiddleName("");
      billName.setLastName("");

      BillingService billingService = new BillingService();
      billingService.setAccountDateActive(sysdate);
      billingService.setAccountNo("");
      billingService.setCurrencyCode(currencycode);
      billingService.setEMFConfigId(emfconfigid);
      billingService.setExrateClass(exrateclass);
      billingService.setExternalAccountNoType(externalaccountnotype);
      billingService.setExternalId("");
      billingService.setExternalIdType(externalidtype);
      billingService.setRateClassDefault(rateclassdefault);
      billingService.setSalesChannelId(saleschannelid);
      billingService.setServiceAddr(custAddress);
      billingService.setServiceName(billName);
      billingService.setServiceStartDate(sysdate);
      billingService.setSysDate(sysdate);

      return billingService;
    } catch (DatatypeConfigurationException dce) {

    }
    return null;
  }

  private com.tscp.mvne.billing.api.Package getPackageDefaults() {
    if (props == null) {
      throw new BillingException("getPackageDefaults",
          "Default file is not bound to application...package default creation not possible");
    }
    try {
      XMLGregorianCalendar sysdate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles")));

      int packageid = Integer.parseInt(props.getProperty("package.package_id"));
      short externalidtype = Short.parseShort(props.getProperty("package.external_id_type"));

      com.tscp.mvne.billing.api.Package kenanPackage = new com.tscp.mvne.billing.api.Package();
      kenanPackage.setPackageId(packageid);
      kenanPackage.setExternalIdType(externalidtype);
      kenanPackage.setActiveDate(sysdate);
      kenanPackage.setAccountNo("");

      return kenanPackage;
    } catch (DatatypeConfigurationException dce) {
      dce.printStackTrace();
    }
    return null;
  }

  private PkgComponent getComponentDefaults() {
    if (props == null) {
      throw new BillingException("getComponentDefaults",
          "Default file is not bound to application...component default creation not possible");
    }
    try {

      XMLGregorianCalendar sysdate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles")));

      int componentid = Integer.parseInt(props.getProperty("component.component_id"));
      int packageid = Integer.parseInt(props.getProperty("component.package_id"));
      short externalidtype = Short.parseShort(props.getProperty("component.external_id_type"));
      short packageinstanceidserv = 4;

      PkgComponent pkgComponent = new PkgComponent();
      pkgComponent.setComponentId(componentid);
      pkgComponent.setPackageInstanceId(0);
      pkgComponent.setPackageInstanceIdServ(packageinstanceidserv);
      pkgComponent.setComponentActiveDate(sysdate);
      pkgComponent.setExternalId("");
      pkgComponent.setExternalIdType(externalidtype);
      pkgComponent.setPackageId(packageid);

      return pkgComponent;
    } catch (DatatypeConfigurationException dce) {
      dce.printStackTrace();
    }
    return null;
  }

  private String getEmail(int acccountno) {
    ValueHolder valueHolder = port.getEmail("system", Integer.toString(acccountno));
    if (valueHolder != null) {
      return valueHolder.getValue();
    }
    return null;
  }

  private String getContactNumber(int accountno) {
    try {
      ContactInfoHolder contactInfo = port.getContactInfo("system", Integer.toString(accountno));
      return contactInfo.getContactInfo().getContact1Phone();
    } catch (NullPointerException npe) {
      // do nothing
    }
    return null;
  }

  private CustAddress getCustAddress(int accountno) {
    CustAddress retValue = new CustAddress();

    BillingAddressHolder billAddressHolder = port.getBillingAddress("username", Integer.toString(accountno));
    if (billAddressHolder != null) {
      return billAddressHolder.getBillAddress();
    }

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_billing_address");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", accountno);

    @SuppressWarnings("unchecked")
    List<CustAddress> custAddressList = q.list();
    for (CustAddress custAddress : custAddressList) {
      retValue = custAddress;
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
    session.getTransaction().rollback();
    return retValue;
  }

  private BillName getBillName(int accountno) {
    BillName retValue = new BillName();

    BillNameHolder billNameHolder = port.getCustomerName("username", Integer.toString(accountno));
    if (billNameHolder != null) {
      return billNameHolder.getBillName();
    }

    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();

    Query q = session.getNamedQuery("get_cust_name");
    q.setParameter("in_username", "username");
    q.setParameter("in_account_no", accountno);

    @SuppressWarnings("unchecked")
    List<BillName> billNameList = q.list();

    for (BillName billName : billNameList) {
      System.out.println("BillName :: " + billName.getFirstName() + " " + billName.getLastName());
      retValue = billName;
    }
    session.getTransaction().rollback();
    return retValue;
  }

  private void bindAccountObject(Account account) {
    BillName billName = getBillName(account.getAccountno());
    account.setFirstname(billName.getFirstName());
    account.setMiddlename(billName.getMiddleName());
    account.setLastname(billName.getLastName());

    CustAddress custAddress = getCustAddress(account.getAccountno());
    account.setContact_address1(custAddress.getAddress1());
    account.setContact_address2(custAddress.getAddress2());
    account.setContact_city(custAddress.getCity());
    account.setContact_state(custAddress.getState());
    account.setContact_zip(custAddress.getZip());
  }

  private XMLGregorianCalendar sysdate() {
    try {
      XMLGregorianCalendar sysdate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
          new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles")));
      return sysdate;
    } catch (DatatypeConfigurationException dce) {
      dce.printStackTrace();
    }
    return null;
  }

  private String getBalance(int accountno) {
    String retValue = "0.00";
    CustBalanceHolder valueHolder = port.getCurrentBalance("system", Integer.toString(accountno));
    try {
      if (valueHolder != null) {
        // if( valueHolder.getCustBalance().getRealBalance()*-1 > 0 ) {
        DecimalFormat df = new DecimalFormat("0.00");
        // retValue =
        // df.getCurrency().getSymbol()+df.format((valueHolder.getCustBalance().getRealBalance()*-1));
        retValue = df.format((valueHolder.getCustBalance().getRealBalance() * -1));
        // }
      }
    } catch (WebServiceException ws_ex) {
      retValue = "0.00";
    } catch (NullPointerException np_ex) {
      retValue = "0.00";
    }
    return retValue;
  }
}
