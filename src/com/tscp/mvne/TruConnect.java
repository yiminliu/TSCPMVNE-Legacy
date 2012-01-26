package com.tscp.mvne;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.ws.WebServiceException;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.BillingSystem;
import com.tscp.mvne.billing.ServiceInstance;
import com.tscp.mvne.billing.api.Payment;
import com.tscp.mvne.billing.api.PaymentHolder;
import com.tscp.mvne.billing.api.UsageHolder;
import com.tscp.mvne.billing.dao.UsageDetail;
import com.tscp.mvne.billing.dao.UsageSummary;
import com.tscp.mvne.billing.exception.BillingException;
import com.tscp.mvne.contract.ContractService;
import com.tscp.mvne.contract.KenanContract;
import com.tscp.mvne.customer.Customer;
import com.tscp.mvne.customer.CustomerException;
import com.tscp.mvne.customer.DeviceException;
import com.tscp.mvne.customer.dao.CustAcctMapDAO;
import com.tscp.mvne.customer.dao.CustAddress;
import com.tscp.mvne.customer.dao.CustInfo;
import com.tscp.mvne.customer.dao.CustTopUp;
import com.tscp.mvne.customer.dao.DeviceAssociation;
import com.tscp.mvne.customer.dao.DeviceInfo;
import com.tscp.mvne.customer.dao.DeviceStatus;
import com.tscp.mvne.exception.MVNEException;
import com.tscp.mvne.jms.NotificationSender;
import com.tscp.mvne.jms.TestNotificationHandler;
import com.tscp.mvne.logger.LoggerHelper;
import com.tscp.mvne.logger.TscpmvneLogger;
import com.tscp.mvne.network.NetworkException;
import com.tscp.mvne.network.NetworkImpl;
import com.tscp.mvne.network.NetworkInfo;
import com.tscp.mvne.network.NetworkInterface;
import com.tscp.mvne.notification.EmailTemplate;
import com.tscp.mvne.notification.NotificationCategory;
import com.tscp.mvne.notification.NotificationException;
import com.tscp.mvne.notification.NotificationSystemImpl;
import com.tscp.mvne.notification.NotificationType;
import com.tscp.mvne.notification.dao.EmailNotification;
import com.tscp.mvne.notification.dao.NotificationParameter;
import com.tscp.mvne.payment.PaymentException;
import com.tscp.mvne.payment.PaymentType;
import com.tscp.mvne.payment.dao.CreditCard;
import com.tscp.mvne.payment.dao.CustPmtMap;
import com.tscp.mvne.payment.dao.PaymentInvoice;
import com.tscp.mvne.payment.dao.PaymentRecord;
import com.tscp.mvne.payment.dao.PaymentTransaction;
import com.tscp.mvne.payment.dao.PaymentUnitResponse;
import com.tscp.mvne.refund.KenanPayment;
import com.tscp.mvne.refund.RefundService;

@WebService
public class TruConnect implements TscpMvne {
  private static TscpmvneLogger logger;
  private NetworkImpl networkimpl;
  private BillingSystem billingImpl;
  private NotificationSystemImpl notificationSystemImpl;
  private ContractService contractService;
  private RefundService refundService;

  public TruConnect() {
    init();
  }

  @Override
  @WebMethod(exclude = true)
  public void init() {
    logger = new TscpmvneLogger();
    networkimpl = new NetworkImpl();
    billingImpl = new BillingSystem();
    notificationSystemImpl = new NotificationSystemImpl();
    contractService = new ContractService();
    refundService = new RefundService();
  }

  @WebMethod
  public NetworkInfo getSwapNetworkInfo(String esn, String mdn) {
    LoggerHelper logHelper = new LoggerHelper("getSwapNetworkInfo", esn, mdn);
    NetworkInfo networkInfo = networkimpl.getSwapNetworkInfo(esn, mdn);
    logHelper.logMethodReturn(networkInfo);
    return networkInfo;
  }

  @WebMethod
  public int applyContract(KenanContract contract) {
    LoggerHelper logHelper = new LoggerHelper("applyContract", contract);
    int contractId = contractService.applyContract(contract);
    logger.info("Contract " + contract.getContractType() + " applied for account "
        + contract.getAccount().getAccountno() + " on MDN " + contract.getServiceInstance().getExternalid());
    logHelper.logMethodExit();
    return contractId;
  }

  @WebMethod
  public void updateContract(KenanContract contract) {
    LoggerHelper logHelper = new LoggerHelper("updateContract", contract);
    contractService.updateContract(contract);
    logger.info("Contract " + contract.getContractType() + " updated for account "
        + contract.getAccount().getAccountno() + " on MDN " + contract.getServiceInstance().getExternalid());
    logHelper.logMethodExit();
  }

  @WebMethod
  public List<KenanContract> getContracts(Account account, ServiceInstance serviceInstance) {
    LoggerHelper logHelper = new LoggerHelper("getContracts", account, serviceInstance);
    List<KenanContract> contracts = contractService.getContracts(account, serviceInstance);
    logHelper.logMethodExit();
    return contracts;
  }

  @WebMethod
  public int applyCouponPayment(Account account, String amount, Date date) {
    LoggerHelper logHelper = new LoggerHelper("applyCouponPayment", account, amount, date);
    int trackingId = contractService.applyCouponPayment(account, amount, date);
    logHelper.logMethodExit();
    return trackingId;
  }

  @WebMethod
  public List<KenanPayment> getKenanPayments(Account account) {
    LoggerHelper logHelper = new LoggerHelper("getKenanPayments", account);
    List<KenanPayment> payments = refundService.getKenanPayments(account);
    logHelper.logMethodExit();
    return payments;
  }

  @WebMethod
  public void reverseKenanPayment(Account account, String amount, Date transDate, String trackingId) {
    LoggerHelper logHelper = new LoggerHelper("reversePayment", account, amount, transDate, trackingId);
    refundService.reversePayment(account, amount, transDate, trackingId);
    logHelper.logMethodExit();
  }

  @WebMethod
  public void applyChargeCredit(CreditCard creditCard, String amount) {
    LoggerHelper logHelper = new LoggerHelper("applyChargeCredit", creditCard, amount);
    refundService.applyChargeCredit(creditCard, amount);
    logHelper.logMethodExit();
  }

  @Override
  @WebMethod
  public NetworkInfo getNetworkInfo(String esn, String mdn) {
    LoggerHelper logHelper = new LoggerHelper("getNetworkInfo", esn, mdn);
    NetworkInfo networkInfo = networkimpl.getNetworkInfo(esn, mdn);
    logHelper.logMethodReturn(networkInfo);
    return networkInfo;
  }

  @Override
  @WebMethod
  public NetworkInfo reserveMDN() {
    LoggerHelper logHelper = new LoggerHelper("reserveMdn");
    String csa = null;
    String priceplan = null;
    List<String> soclist = null;
    NetworkInfo networkInfo = networkimpl.reserveMDN(csa, priceplan, soclist);
    logHelper.logMethodReturn(networkInfo);
    return networkInfo;
  };

  @Override
  @WebMethod
  public NetworkInfo activateService(Customer customer, NetworkInfo networkInfo) {
    LoggerHelper logHelper = new LoggerHelper("activateService", customer, networkInfo);
    networkimpl.activateMDN(networkInfo);
    // Update device information
    try {
      logger.info("Attempting to update device information to active");
      if (customer.getDeviceList() == null) {
        customer.retrieveDeviceList();
      }
      if (customer.getDeviceList() != null) {
        for (DeviceInfo deviceInfo : customer.getDeviceList()) {
          if (deviceInfo.getDeviceValue().equals(networkInfo.getEsnmeiddec())
              || deviceInfo.getDeviceValue().equals(networkInfo.getEsnmeidhex())) {
            logger.info("Found Device Information " + deviceInfo.getDeviceId() + " for Customer " + customer.getId()
                + ".");
            deviceInfo.setDeviceStatusId(DeviceStatus.ID_ACTIVE);
            deviceInfo.setEffectiveDate(new Date());
            deviceInfo.save();
          }
        }
      }
    } catch (Exception ex) {
      logger.info("Error updating device information in activation method ");
      logger.warn(ex.getMessage());
    }
    logHelper.logMethodReturn(networkInfo);
    return networkInfo;
  }

  @Override
  @WebMethod
  public void suspendService(ServiceInstance serviceInstance) {
    LoggerHelper logHelper = new LoggerHelper("suspendService", serviceInstance);
    suspendSubscriber(serviceInstance, null);
    logHelper.logMethodExit();
  }

  @Override
  @WebMethod
  public void restoreService(ServiceInstance serviceInstance) {
    LoggerHelper logHelper = new LoggerHelper("restoreService", serviceInstance);
    restoreSubscriber(serviceInstance, null);
    logHelper.logMethodExit();
  }

  @Override
  @WebMethod
  public void disconnectService(ServiceInstance serviceInstance) {
    LoggerHelper logHelper = new LoggerHelper("disconnectService", serviceInstance);
    logger.info("Calling Disconnect Service for External ID " + serviceInstance.getExternalid());
    Account account = new Account();
    logger.info("fetching account by TN");
    try {
      account.setAccountno(billingImpl.getAccountNoByTN(serviceInstance.getExternalid()));
      if (account.getAccountno() == 0) {
        throw new BillingException("Unable to get account number for External ID " + serviceInstance.getExternalid());
      }
    } catch (MVNEException mvne_ex) {
      logger.warn(mvne_ex.getMessage());
      throw mvne_ex;
    }
    logger.info("binding service instance object");
    bindServiceInstanceObject(account, serviceInstance);

    logger.info("obtaining network information for ExternalId " + serviceInstance.getExternalid());
    NetworkInfo networkinfo = getNetworkInfo(null, serviceInstance.getExternalid());
    // networkinfo.setMdn(serviceInstance.getExternalid());
    logger.info("disconnecting service from network");
    disconnectFromNetwork(networkinfo);

    logger.info("disconnecting TN from BillingSystem");
    disconnectServiceInstanceFromKenan(account, serviceInstance);
    logger.info("Done calling Disconnect Service for " + serviceInstance.getExternalid());

    logger.info("begin Device cleanup");
    // Find the device information associated with the network information
    // set that device to Released / Reactivateable
    try {
      Customer customer = new Customer();
      CustAcctMapDAO custAcctMapDAO = customer.getCustAcctMapDAOfromAccount(account.getAccountno());
      if (custAcctMapDAO != null) {
        customer.setId(custAcctMapDAO.getCust_id());
        logger.info("Retrieving device list for customer id " + customer.getId());
        if (customer.getDeviceList() == null) {
          customer.retrieveDeviceList();
        }
        if (customer.getDeviceList() != null) {
          logger.info("Customer " + customer.getId() + " has " + customer.getDeviceList().size() + " devices.");
          for (DeviceInfo oldDeviceInfo : customer.getDeviceList()) {
            if (oldDeviceInfo.getDeviceValue().equals(networkinfo.getEsnmeiddec())
                || oldDeviceInfo.getDeviceValue().equals(networkinfo.getEsnmeidhex())) {
              logger.info("old device information found...updating");
              oldDeviceInfo.setDeviceStatusId(DeviceStatus.ID_RELEASED_REACTIVATEABLE);
              oldDeviceInfo.setEffectiveDate(new Date());
              oldDeviceInfo.save();
            }
          }
        }
      }
    } catch (Exception ex) {
      logger.info("Error moving device information around");
      logger.warn(ex.getMessage());
    }
    logHelper.logMethodExit();
  }

  @WebMethod
  @Override
  public Account createBillingAccount(Customer customer, Account account) {
    LoggerHelper logHelper = new LoggerHelper("createBillingAccount", customer, account);
    if (customer == null || customer.getId() <= 0) {
      throw new WebServiceException("Please specify a customer prior to adding an account.");
    }
    int accountNumber = billingImpl.createAccount(account);
    if (accountNumber <= 0) {
      throw new WebServiceException("Error when building account...No account number returned...");
    } else {
      customer.addCustAccts(account);
    }
    logHelper.logMethodReturn(account);
    return account;
  }

  @WebMethod
  @Override
  public Account createServiceInstance(Account account, ServiceInstance serviceInstance) {
    LoggerHelper logHelper = new LoggerHelper("createServiceInstance", account, serviceInstance);
    try {
      account.setServiceinstancelist(billingImpl.getServiceInstanceList(account));
    } catch (BillingException bill_ex) {
      logger.warn("Billing Exception thrown " + bill_ex.getMessage());
      return account;
    }
    logger.info("adding service instance");
    billingImpl.addServiceInstance(account, serviceInstance);
    com.tscp.mvne.billing.Package lPackage = new com.tscp.mvne.billing.Package();
    logger.info("adding package");
    billingImpl.addPackage(account, serviceInstance, lPackage);
    com.tscp.mvne.billing.Component componentid = null;
    logger.info("adding Component");
    billingImpl.addComponent(account, serviceInstance, lPackage, componentid);

    if (account.getServiceinstancelist() == null) {
      account.setServiceinstancelist(new Vector<ServiceInstance>());
    }
    account.getServiceinstancelist().add(serviceInstance);
    if (account.getPackageList() == null) {
      account.setPackageList(new Vector<com.tscp.mvne.billing.Package>());
    }
    if (lPackage.getComponentlist() == null) {
      lPackage.setComponentlist(new Vector<com.tscp.mvne.billing.Component>());
    }
    lPackage.getComponentlist().add(componentid);
    account.getPackageList().add(lPackage);

    // Update device association for this customer
    try {
      logger.info("Updating Device Association");
      Customer customer = new Customer();
      CustAcctMapDAO custAcctMapDAO = customer.getCustAcctMapDAOfromAccount(account.getAccountno());
      if (custAcctMapDAO != null) {
        customer.setId(custAcctMapDAO.getCust_id());

        logger.info("Retrieving network information for MDN " + serviceInstance.getExternalid());
        NetworkInfo networkInfo = networkimpl.getNetworkInfo(null, serviceInstance.getExternalid());
        if (networkInfo == null) {
          throw new NetworkException("Cannot find Active device information for mdn " + serviceInstance.getExternalid());
        }

        logger.info("Retrieving customer " + customer.getId() + "'s device list");
        List<DeviceInfo> deviceInfoList = new Vector<DeviceInfo>();
        deviceInfoList = customer.retrieveDeviceList();
        if (deviceInfoList != null) {
          for (DeviceInfo deviceInfo : deviceInfoList) {
            if (deviceInfo.getDeviceValue().equals(networkInfo.getEsnmeiddec())
                || deviceInfo.getDeviceValue().equals(networkInfo.getEsnmeidhex())) {
              logger.info("found device " + deviceInfo.getDeviceId() + "...");
              logger.info("attempting to retrieve subscr_no for EXTERNAL_ID " + serviceInstance.getExternalid());
              List<ServiceInstance> serviceInstanceList = billingImpl.getServiceInstanceList(account);
              if (serviceInstanceList != null) {
                for (ServiceInstance si : serviceInstanceList) {
                  if (si.getExternalid().equals(serviceInstance.getExternalid())) {
                    logger.info("Subscriber " + si.getSubscrno() + " found");
                    logger.info("Building Device Association Mapping");
                    DeviceAssociation deviceAssociation = new DeviceAssociation();
                    deviceAssociation.setDeviceId(deviceInfo.getDeviceId());
                    deviceAssociation.setSubscrNo(si.getSubscrno());
                    logger.info("Saving device association");
                    deviceAssociation.save();

                    if (deviceInfo.getDeviceStatusId() != DeviceStatus.ID_ACTIVE) {
                      logger.info("DeviceInfo " + deviceInfo.getDeviceId() + " is not in active status...Activating");
                      deviceInfo.setDeviceStatusId(DeviceStatus.ID_ACTIVE);
                      deviceInfo.setEffectiveDate(new Date());
                      deviceInfo.save();
                    }
                    break;
                  }
                }
              }
              break;
            }
          }
        }
      } else {
        logger.info("No Customer mapping found for Account Number " + account.getAccountno());
      }
    } catch (Exception ex) {
      logger.info("Error updating Device Association in createServiceInstance method");
      logger.warn(ex.getMessage());
    }
    logHelper.logMethodReturn(account);
    return account;
  }

  @WebMethod
  public List<CustPmtMap> getCustPaymentList(int customerId, int paymentId) {
    LoggerHelper logHelper = new LoggerHelper("getCustPaymentList", customerId, paymentId);
    Customer cust = new Customer();
    cust.setId(customerId);
    List<CustPmtMap> custPaymentList = cust.getCustpmttypes(paymentId);
    logger.info("Return object");
    for (CustPmtMap custPmtMap : custPaymentList) {
      logger.info(custPmtMap.toString());
    }
    logHelper.logMethodExit();
    return custPaymentList;
  }

  @WebMethod
  public CreditCard getCreditCardDetail(int paymentId) {
    LoggerHelper logHelper = new LoggerHelper("getCreditCardDetail", paymentId);
    CreditCard creditCard = new CreditCard();
    creditCard.setPaymentid(paymentId);
    creditCard.load();
    logHelper.logMethodReturn(creditCard);
    return creditCard;
  }

  @WebMethod
  public CreditCard addCreditCard(Customer customer, CreditCard creditCard) {
    LoggerHelper logHelper = new LoggerHelper("addCreditCard", customer, creditCard);
    if (creditCard.getPaymentid() != 0) {
      throw new PaymentException("addCreditCard", "PaymentID must be 0 when adding a payment");
    }
    CreditCard insertedCreditCard = customer.insertCreditCardPaymentInformation(creditCard);
    logHelper.logMethodReturn(insertedCreditCard);
    return insertedCreditCard;
  }

  @WebMethod
  public List<CustAcctMapDAO> getCustomerAccounts(int customerId) {
    LoggerHelper logHelper = new LoggerHelper("getCustomerAccounts", customerId);
    Customer cust = new Customer();
    cust.setId(customerId);
    List<CustAcctMapDAO> custAcctList = cust.getCustaccts();
    logger.info("Mapped Accounts are :");
    for (CustAcctMapDAO custAcct : custAcctList) {
      logger.info(custAcct.toString());
    }
    logHelper.logMethodExit();
    return custAcctList;
  }

  @WebMethod
  public Account getAccountInfo(int accountNumber) {
    LoggerHelper logHelper = new LoggerHelper("getAccountInfo", accountNumber);
    Account account = billingImpl.getAccountByAccountNo(accountNumber);
    logHelper.logMethodReturn(account);
    return account;
  }

  @WebMethod
  public void updateAccountEmailAddress(Account account) {
    LoggerHelper logHelper = new LoggerHelper("updateAccountEmailAddress", account);
    if (account == null) {
      logger.info("updatingAccountEmailAddress(account) account object is null");
      throw new BillingException("Account object cannot be null.");
    }
    logger.info("Updating EmailAddress for Account " + account.getAccountno() + " to email address "
        + account.getContact_email());
    billingImpl.updateAccountEmailAddress(account);
    logHelper.logMethodExit();
  }

  /**
   * Create a transaction
   * 
   * Submit Payment to the payment Unit
   * 
   * Post Payment information to Kenan
   * 
   * @param sessionId
   *          - Session set by the client
   * @param account
   * @param creditCard
   * @param paymentAmount
   * @throws PaymentException
   */
  @WebMethod
  public PaymentUnitResponse makeCreditCardPayment(String sessionId, Account account, CreditCard creditCard,
      String paymentAmount) {
    LoggerHelper logHelper = new LoggerHelper("makeCreditCardPayment", sessionId, account, creditCard, paymentAmount);
    if (creditCard == null) {
      logger
          .warn("SessionId " + sessionId + ":: CreditCard Information must be present to submit a CreditCard Payment");
      throw new PaymentException("makeCreditCardPayment",
          "CreditCard Information must be present to submit a CreditCard Payment");
    }
    if (paymentAmount == null || paymentAmount.indexOf(".") == 0) {
      logger.warn("Invalid payment format. Payment format needs to be \"xxx.xx\" ");
      throw new PaymentException("makeCreditCardPayment",
          "Invalid payment format. Payment format needs to be \"xxx.xx\" ");
    }
    if (account == null || account.getAccountno() <= 0) {
      logger.warn("Invalid Account Object. Account must be specified when making payments...");
      throw new BillingException("makeCreditCardPayment",
          "Invalid Account Object. Account must be specified when making payments...");
    }
    creditCard.validate();
    logger.info("Account "
        + account.getAccountno()
        + " is attempting to make a "
        + paymentAmount
        + " payment against Credit Card ending in "
        + creditCard.getCreditCardNumber().subSequence(creditCard.getCreditCardNumber().length() - 4,
            creditCard.getCreditCardNumber().length()));

    logger.info("Creating Transaction...");
    // Create Transaction
    PaymentTransaction pmttransaction = new PaymentTransaction();
    pmttransaction.setSessionId(sessionId);
    pmttransaction.setPmtId(creditCard.getPaymentid());
    pmttransaction.setPaymentAmount(paymentAmount);
    pmttransaction.setAccountNo(account.getAccountno());

    pmttransaction.savePaymentTransaction();
    logger.info("Transaction " + pmttransaction.getTransId() + " has been entered and is beginning");

    // Submit to payment unit
    logger.info("submitting creditcard payment");
    PaymentUnitResponse response = creditCard.submitPayment(pmttransaction);
    if (response != null) {
      logger.info(response.toString());
    } else {
      response = new PaymentUnitResponse();
      response.setConfcode("-1");
      response.setTransid("000000");
      response.setConfdescr("No response returned from payment unit");
      response.setAuthcode("System generated error input");
    }

    // update transaction
    pmttransaction.setPaymentUnitConfirmation(response.getConfirmationString());
    pmttransaction.setPaymentUnitMessage(response.getConfdescr() + " AuthCode::" + response.getAuthcode());
    pmttransaction.setPaymentUnitDate(new Date());
    String paymentMethod = "unknown";
    String paymentSource = "";
    if (creditCard.getCreditCardNumber().substring(0, 1).equals(CreditCard.CREDITCARD_AMEX)) {
      paymentMethod = "AmericanExpress";
      // paymentSource = "3XXX-XXXXXX-X";
    } else if (creditCard.getCreditCardNumber().substring(0, 1).equals(CreditCard.CREDITCARD_VISA)) {
      paymentMethod = "Visa";
      // paymentSource = "4XXX-XXXX-XXXX-";
    } else if (creditCard.getCreditCardNumber().substring(0, 1).equals(CreditCard.CREDITCARD_MASTERCARD)) {
      paymentMethod = "MasterCard";
      // paymentSource = "6XXX-XXXX-XXXX-";
    } else if (creditCard.getCreditCardNumber().substring(0, 1).equals(CreditCard.CREDITCARD_DISCOVER)) {
      paymentMethod = "Discover";
      // paymentSource = "6XXX-XXXX-XXXX-";
    }
    paymentSource += creditCard.getCreditCardNumber().substring(creditCard.getCreditCardNumber().length() - 4,
        creditCard.getCreditCardNumber().length());
    pmttransaction.setPaymentMethod(paymentMethod);
    pmttransaction.setPaymentSource(paymentSource);
    pmttransaction.savePaymentTransaction();

    if (response.getConfcode().equals(PaymentUnitResponse.SUCCESSFUL_TRANSACTION)) {
      // Submit to Kenan
      logger.info("adding payment information to Kenan...");
      billingImpl.addPayment(account, paymentAmount.replace(".", ""));

      // get the tracking ID from Kenan, it should be the last entry in the list
      // of payments
      logger.info("retrieving payment list from Kenan");
      List<PaymentHolder> paymentList = billingImpl.getCompletePaymentHistory(account);
      if (paymentList != null && paymentList.size() > 0) {
        try {
          Payment payment = null;
          for (PaymentHolder paymentHolder : paymentList) {
            if (payment == null) {
              payment = paymentHolder.getPayment();
            }
            if (payment.getTrackingId() <= paymentHolder.getPayment().getTrackingId()) {
              payment = paymentHolder.getPayment();
            }
          }
          logger.info("Latest Billing Tracking ID found to be " + payment.getTrackingId());
          pmttransaction.setBillingTrackingId(payment.getTrackingId());
          // PaymentHolder paymentHolder =
          // paymentList.get(paymentList.size()-1);
          // logger.info("Latest Billing Tracking ID found to be "+paymentHolder.getPayment().getTrackingId());
          // pmttransaction.setBillingTrackingId(paymentHolder.getPayment().getTrackingId());
        } catch (ArrayIndexOutOfBoundsException index_ex) {
          logger.warn(index_ex.getMessage() + "...error retrieving payment item index at [" + (paymentList.size() - 1)
              + "]");
          pmttransaction.setBillingTrackingId(-1);
        } catch (NullPointerException np_ex) {
          logger.warn(np_ex.getMessage() + "...payment is null.");
          pmttransaction.setBillingTrackingId(-1);
        }

      }

      // update transaction
      pmttransaction.setBillingUnitDate(new Date());
      pmttransaction.savePaymentTransaction();
      logger.info("Transaction information saved and payment completed for Account " + account.getAccountno() + ".");
    } else {
      logger.warn("Error posting credit card payment. :: " + response.getConfdescr() + " " + response.getAuthcode());
      throw new PaymentException("makeCreditCardPayment", "Error posting credit card payment. :: "
          + response.getConfdescr() + " " + response.getAuthcode());
    }
    logHelper.logMethodExit();
    return response;
  }

  /**
   * 
   * @param customer
   * @return
   */
  @WebMethod
  public List<PaymentRecord> getPaymentHistory(Customer customer) {
    return customer.getPaymentHistory();
  }

  /**
   * 
   * @param customer
   * @param serviceInstance
   * @return
   */
  @WebMethod
  public UsageSummary getUsageSummary(Customer customer, ServiceInstance serviceInstance) {
    LoggerHelper logHelper = new LoggerHelper("getUsageSummary", serviceInstance);
    UsageSummary usage = new UsageSummary();
    try {
      List<CustAcctMapDAO> accountList = customer.getCustaccts();
      boolean validRequest = false;
      if (customer == null || serviceInstance == null) {
        logger.warn("Exception being raised due to lack of customer or serviceInstance objects.");
        throw new CustomerException("Error! Check required input parameters...");
      } else {
        if (serviceInstance.getExternalid() == null || serviceInstance.getExternalid().trim().length() <= 0) {
          throw new BillingException("Error! Check that a valid ServiceInstance object is supplied....");
        }
      }
      if (accountList != null) {
        logger.info("CustAcctMap has been found to have " + accountList.size() + " elements");
        for (CustAcctMapDAO custAcct : accountList) {
          Account account = new Account();
          account.setAccountno(custAcct.getAccount_no());
          List<ServiceInstance> serviceInstanceList = billingImpl.getServiceInstanceList(account);
          for (ServiceInstance si : serviceInstanceList) {
            if (si.getExternalid().equals(serviceInstance.getExternalid())) {
              logger.info("request is valid");
              validRequest = true;
            }
          }
        }
      } else {
        throw new CustomerException("Error! No accounts associated with CustID " + customer.getId());
      }
      if (!validRequest) {
        throw new CustomerException("Error! Customer " + customer.getId() + " is not associated with ServiceInstance "
            + serviceInstance.getExternalid());
      }
      UsageHolder usageHolder = billingImpl.getUnbilledUsageSummary(serviceInstance);
      if (usageHolder != null) {
        if (usageHolder.getStatusMessage() != null
            && usageHolder.getStatusMessage().getStatus().equalsIgnoreCase("SUCCESS")) {
          usage.load(usageHolder.getUsage());
          logger.info("Usage object loaded");
          logger.info(usage.toString());
        } else if (usageHolder.getStatusMessage().getStatus().equals("")) {
          usage.setExternalid(serviceInstance.getExternalid());
          System.out.println("Empty Status...");
        } else {
          throw new BillingException("Error getting usage for " + serviceInstance.getExternalid() + ". "
              + usageHolder.getStatusMessage().getStatus() + "::" + usageHolder.getStatusMessage().getMessage());
        }
      }
      // logger.exiting("TruConnect", "getUsageSummary");
    } catch (MVNEException ex) {
      logger.warn(ex.getMessage());
      throw ex;
    }
    logHelper.logMethodExit();
    return usage;
  }

  /**
   * 
   * @param customer
   * @param paymentId
   * @return
   */
  @WebMethod
  public List<CustPmtMap> deleteCreditCardPaymentMethod(Customer customer, int paymentId) {
    LoggerHelper logHelper = new LoggerHelper("deleteCreditCardPaymentMethod", customer, paymentId);
    if (paymentId == 0) {
      throw new PaymentException("deleteCreditCardPaymentMethod", "PaymentID cannot be 0 when deleting a payment");
    }
    customer.deletePayment(paymentId);
    logHelper.logMethodExit();
    return customer.getCustpmttypes(0);
  }

  /**
   * 
   * @param customer
   * @param creditCard
   * @return
   */
  @WebMethod
  public List<CustPmtMap> updateCreditCardPaymentMethod(Customer customer, CreditCard creditCard) {
    LoggerHelper logHelper = new LoggerHelper("updateCreditCardPaymentMethod", customer, creditCard);
    if (creditCard.getPaymentid() == 0) {
      throw new PaymentException("addCreditCard", "PaymentID cannot be 0 when updating a payment");
    }
    customer.updateCreditCardPaymentInformation(creditCard);
    logger.info("Updating service instance information");
    try {
      paymentUpdatedRoutine(customer);
    } catch (Exception ex) {
      logger.info("Error updating Service Instance information for customer " + customer.getId());
      logger.warn(ex.getMessage());
    }
    logHelper.logMethodExit();
    return customer.getCustpmttypes(creditCard.getPaymentid());
  }

  private void bindServiceInstanceObject(Account account, ServiceInstance serviceInstance) {
    List<ServiceInstance> serviceInstanceList = billingImpl.getServiceInstanceList(account);
    if (serviceInstanceList != null) {
      for (ServiceInstance si : serviceInstanceList) {
        if (si.getExternalid().equals(serviceInstance.getExternalid())) {
          serviceInstance.setExternalidtype(si.getExternalidtype());
        }
      }
    } else {
      throw new WebServiceException("Active ExternalIDs not found for Account Number " + account.getAccountno());
    }

  }

  /**
   * 
   * @param customer
   *          - Customer.id must be populated otherwise exception will be thrown
   * @return
   */
  @WebMethod
  public CustTopUp getCustTopUpAmount(Customer customer, Account account) {
    LoggerHelper logHelper = new LoggerHelper("getCustTopUpAmount", customer, account);
    CustTopUp topUp = customer.getTopupAmount(account);
    logHelper.logMethodReturn(topUp);
    return topUp;
  }

  /**
   * 
   * @param customer
   *          - Cust Id must be populated
   * @param topUpAmount
   *          - topup amount must be "xxx.xx" format
   * @return
   */
  @WebMethod
  public CustTopUp setCustTopUpAmount(Customer customer, String topUpAmount, Account account) {
    LoggerHelper logHelper = new LoggerHelper("setCustTopUpAmount", customer, topUpAmount, account);
    CustTopUp topUp = customer.setTopupAmount(account, topUpAmount);
    logHelper.logMethodReturn(topUp);
    return topUp;
  }

  /**
   * 
   * @param sessionId
   *          - String - identifier sent by clients
   * @param customer
   *          - Customer object requiring cust id value.
   * @param paymentId
   *          - int - payment id to be used for payment processing
   * @param account
   *          - Account object used to apply payment towards Kenan/Billing
   *          System
   * @param paymentAmount
   *          - String - Amount to be applied in this transaction format
   * @return
   */
  @WebMethod
  public PaymentUnitResponse submitPaymentByPaymentId(String sessionId, Customer customer, int paymentId,
      Account account, String paymentAmount) {
    LoggerHelper logHelper = new LoggerHelper("submitPaymentByPaymentId", sessionId, customer, paymentId, account,
        paymentAmount);
    if (customer == null || customer.getId() <= 0) {
      logger.warn("SessionId " + sessionId + "::Customer cannot be empty");
      throw new CustomerException("Customer cannot be empty");
    }
    if (paymentId == 0) {
      logger.warn("SessionId " + sessionId + ":: Payment Information must be present to submit a Payment");
      throw new PaymentException("submitPaymentByPaymentId", "Payment Information must be present to submit a Payment");
    }
    if (paymentAmount == null || paymentAmount.indexOf(".") == 0) {
      logger.warn("Invalid payment format. Payment format needs to be \"xxx.xx\" ");
      throw new PaymentException("submitPaymentByPaymentId",
          "Invalid payment format. Payment format needs to be \"xxx.xx\" ");
    } else {
      if (Double.parseDouble(paymentAmount) % 10 != 0) {
        throw new PaymentException("submitPaymentByPaymentId",
            "Invalid payment amount.  Amount must be a multiple of 10");
      }
    }
    if (account == null || account.getAccountno() <= 0) {
      logger.warn("Invalid Account Object. Account must be specified when making payments...");
      logger.info("Attempting to grab customer's account from CustAcctMap object");
      List<CustAcctMapDAO> custAcctList = customer.getCustaccts();
      if (custAcctList == null) {
        logger.warn("No accounts found for CustomerId " + customer.getId());
        throw new BillingException("submitPaymentByPaymentId",
            "Invalid Account Object. Account must be specified when making payments...");
      } else if (custAcctList.size() != 1) {
        logger.warn("Too many accounts found for Customer " + customer.getId());
        throw new BillingException("submitPaymentByPaymentId",
            "Invalid Account Object. Account must be specified when making payments...");
      } else {
        for (CustAcctMapDAO custAcctMapDAO : custAcctList) {
          account = new Account();
          account.setAccountno(custAcctMapDAO.getAccount_no());
        }
      }
    }
    logger.info("Customer " + customer.getId() + " and Account " + account.getAccountno() + " is attempting to make a "
        + paymentAmount + " payment against paymentid " + paymentId);

    boolean validTransaction = false;
    String paymentMethod = "unknown";
    String paymentSource = "";

    logger.info("Retrieving payment list");
    List<CustPmtMap> custPaymentList = customer.getCustpmttypes(0);
    for (CustPmtMap custPmt : custPaymentList) {
      if (custPmt.getPaymentid() == paymentId) {
        validTransaction = true;
        logger.info("Transaction is valid...CustPmt.getPaymentType() is " + custPmt.getPaymenttype());
        if (custPmt.getPaymenttype().equals(PaymentType.CreditCard.toString())) {
          CreditCard creditcard = getCreditCardDetail(paymentId);
          if (creditcard.getCreditCardNumber().substring(0, 1).equals(CreditCard.CREDITCARD_AMEX)) {
            paymentMethod = "AmericanExpress";
            // paymentSource = "3XXX-XXXXXX-X";
          } else if (creditcard.getCreditCardNumber().substring(0, 1).equals(CreditCard.CREDITCARD_VISA)) {
            paymentMethod = "Visa";
            // paymentSource = "4XXX-XXXX-XXXX-";
          } else if (creditcard.getCreditCardNumber().substring(0, 1).equals(CreditCard.CREDITCARD_MASTERCARD)) {
            paymentMethod = "MasterCard";
            // paymentSource = "6XXX-XXXX-XXXX-";
          } else if (creditcard.getCreditCardNumber().substring(0, 1).equals(CreditCard.CREDITCARD_DISCOVER)) {
            paymentMethod = "Discover";
            // paymentSource = "6XXX-XXXX-XXXX-";
          }
          paymentSource += creditcard.getCreditCardNumber().substring(creditcard.getCreditCardNumber().length() - 4,
              creditcard.getCreditCardNumber().length());
        }
      }
    }
    if (!validTransaction) {
      logger.warn("Customer " + customer.getId() + " is not authorized to make payments from Id " + paymentId);
      throw new CustomerException("submitPaymentByPaymentId", "Customer " + customer.getId()
          + " is not authorized to make payments from paymentId " + paymentId);
    }

    logger.info("Creating Transaction...");
    // Create Transaction
    PaymentTransaction pmttransaction = new PaymentTransaction();
    pmttransaction.setSessionId(sessionId);
    pmttransaction.setPmtId(paymentId);
    pmttransaction.setPaymentAmount(paymentAmount);
    pmttransaction.setAccountNo(account.getAccountno());

    pmttransaction.savePaymentTransaction();
    logger.info("Transaction " + pmttransaction.getTransId() + " has been entered and is beginning");

    // Submit to payment unit
    logger.info("submitting payment information against payment id " + paymentId);
    PaymentUnitResponse response = customer.submitPayment(pmttransaction, paymentId);
    if (response != null) {
      logger.info(response.toString());
    } else {
      response = new PaymentUnitResponse();
      response.setConfcode("-1");
      response.setConfdescr("No response returned from payment unit");
      response.setAuthcode("System generated error input");
    }

    // update transaction
    pmttransaction.setPaymentUnitConfirmation(response.getConfirmationString());
    pmttransaction.setPaymentUnitMessage(response.getConfdescr() + " AuthCode::" + response.getAuthcode());
    pmttransaction.setPaymentUnitDate(new Date());
    pmttransaction.setPaymentMethod(paymentMethod);
    pmttransaction.setPaymentSource(paymentSource);
    pmttransaction.savePaymentTransaction();

    if (response.getConfcode().equals(PaymentUnitResponse.SUCCESSFUL_TRANSACTION)) {
      // Submit to Kenan
      logger.info("adding payment information to Kenan...");
      billingImpl.addPayment(account, paymentAmount.replace(".", ""));

      // get the tracking ID from Kenan, it should be the first entry in the
      // list of payments
      List<PaymentHolder> paymentList = billingImpl.getCompletePaymentHistory(account);
      if (paymentList != null && paymentList.size() > 0) {
        Payment payment = null;
        try {
          for (PaymentHolder paymentHolder : paymentList) {
            if (payment == null) {
              payment = paymentHolder.getPayment();
            }
            if (payment.getTrackingId() <= paymentHolder.getPayment().getTrackingId()) {
              payment = paymentHolder.getPayment();
            }
          }
          logger.info("Latest Billing Tracking ID found to be " + payment.getTrackingId());
          pmttransaction.setBillingTrackingId(payment.getTrackingId());
          // PaymentHolder paymentHolder =
          // paymentList.get(paymentList.size()-1);
          // logger.info("Latest Billing Tracking ID found to be "+paymentHolder.getPayment().getTrackingId());
          // pmttransaction.setBillingTrackingId(paymentHolder.getPayment().getTrackingId());
        } catch (ArrayIndexOutOfBoundsException index_ex) {
          logger.warn(index_ex.getMessage() + "...error retrieving payment item index at [" + (paymentList.size() - 1)
              + "]");
          pmttransaction.setBillingTrackingId(-1);
        } catch (NullPointerException np_ex) {
          logger.warn(np_ex.getMessage() + "...payment is null.");
          pmttransaction.setBillingTrackingId(-1);
        }
      }

      // update transaction
      logger.info("Saving Transaction with Billing System Response");
      pmttransaction.setBillingUnitDate(new Date());
      pmttransaction.savePaymentTransaction();

      // send pmt notification
      logger.info("Sending Top-Up notification");
      sendPaymentNotification(customer, account, pmttransaction);

      // get device information
      logger.info("getting device information for update");
      DeviceInfo deviceInfo = null;
      List<DeviceInfo> deviceInfoList = customer.retrieveDeviceList(account.getAccountno());
      if (deviceInfoList != null && deviceInfoList.size() > 0) {

      }

      // update service instances with new cleared threshold value
      logger.info("Updating service instances for account " + account.getAccountno());
      Account loadedAccount = billingImpl.getAccountByAccountNo(account.getAccountno());
      for (ServiceInstance serviceInstance : loadedAccount.getServiceinstancelist()) {
        // logger.info("Restoring all services on Account  threshold value for ServiceInstance "+serviceInstance.getExternalid()+" to "+BillingSystem.SERVICE_INSTANCE_RESTORED);
        // billingImpl.updateServiceInstanceStatus(serviceInstance,
        // BillingSystem.SERVICE_INSTANCE_RESTORED);
        for (DeviceInfo tempDeviceInfo : deviceInfoList) {
          logger.info("Iterating through deviceInfoList");
          List<DeviceAssociation> deviceAssociationList = customer.retrieveDeviceAssociationList(tempDeviceInfo
              .getDeviceId());
          if (deviceAssociationList != null && deviceAssociationList.size() > 0) {
            logger.info("iterating through deviceAssocaitionList");
            for (DeviceAssociation deviceAssociation : deviceAssociationList) {
              if (deviceAssociation.getAccountNo() == account.getAccountno()
                  && deviceAssociation.getInactiveDate() == null
                  && deviceAssociation.getExternalId().equals(serviceInstance.getExternalid())) {
                logger.info("Device association found...setting device object to device id "
                    + tempDeviceInfo.getDeviceId());
                deviceInfo = tempDeviceInfo;
                break;
              }
            }
          }
          if (deviceInfo != null) {
            break;
          }
        }
        restoreSubscriber(serviceInstance, deviceInfo);
        deviceInfo = null;
      }
      logger.info("Transaction information saved and payment completed for Account " + account.getAccountno() + ".");

    } else {
      logger.warn("Error posting credit card payment. :: " + response.getConfdescr() + " " + response.getAuthcode());

      if (pmttransaction.getSessionId().contains("AUTO") || pmttransaction.getSessionId().contains("auto")) {

        // send payment failed notification
        logger.info("Sending Payment Failed notification");
        sendPaymentFailedNotification(customer, account, pmttransaction);

        // get device information
        logger.info("getting device information for update");
        DeviceInfo deviceInfo = null;
        List<DeviceInfo> deviceInfoList = customer.retrieveDeviceList(account.getAccountno());

        // suspend services
        logger.info("Loading account information from Billing System...");
        Account loadedAccount = billingImpl.getAccountByAccountNo(account.getAccountno());
        logger.info("Updating service instances for account " + account.getAccountno());
        for (ServiceInstance serviceInstance : loadedAccount.getServiceinstancelist()) {
          logger.info("Updating threshold value and Network status for ServiceInstance "
              + serviceInstance.getExternalid() + " to " + BillingSystem.SERVICE_INSTANCE_HOTLINED);
          // billingImpl.updateServiceInstanceStatus(serviceInstance,
          // BillingSystem.SERVICE_INSTANCE_FAILED_PMT);
          if (deviceInfoList != null && deviceInfoList.size() > 0) {
            for (DeviceInfo tempDeviceInfo : deviceInfoList) {
              logger.info("Iterating through deviceInfoList");
              List<DeviceAssociation> deviceAssociationList = customer.retrieveDeviceAssociationList(tempDeviceInfo
                  .getDeviceId());
              if (deviceAssociationList != null && deviceAssociationList.size() > 0) {
                logger.info("iterating through deviceAssocaitionList");
                for (DeviceAssociation deviceAssociation : deviceAssociationList) {
                  if (deviceAssociation.getAccountNo() == account.getAccountno()
                      && deviceAssociation.getInactiveDate() == null
                      && deviceAssociation.getExternalId().equals(serviceInstance.getExternalid())) {
                    logger.info("Device association found...setting device object to device id "
                        + tempDeviceInfo.getDeviceId());
                    deviceInfo = tempDeviceInfo;
                    break;
                  }
                }
              }
              if (deviceInfo != null) {
                break;
              }
            }
          }
          suspendSubscriber(serviceInstance, deviceInfo);
          deviceInfo = null;
        }
      } else {
        logger.info("No active services on this account...No need to send failed payment notification");
      }
      throw new PaymentException("submitPaymentByPaymentId", "Error posting payment. :: " + response.getConfdescr()
          + " " + response.getAuthcode());
    }
    logHelper.logMethodExit();
    return response;

  }

  @WebMethod
  public CustAcctMapDAO getCustFromAccount(int accountno) {
    Customer customer = new Customer();
    return customer.getCustAcctMapDAOfromAccount(accountno);
  }

  @WebMethod
  public List<CustPmtMap> updateCustPaymentMap(CustPmtMap custPmtMap) {
    if (custPmtMap == null) {
      throw new PaymentException("CustPmtMap must be specified");
    }
    if (custPmtMap.getCustid() <= 0) {
      throw new CustomerException("Please specify customer id...");
    }
    if (custPmtMap.getPaymentid() <= 0) {
      throw new PaymentException("Please specify a payment id...Blanket update is not supported...");
    }
    Customer customer = new Customer();
    customer.setId(custPmtMap.getCustid());
    List<CustPmtMap> paymentTypes = customer.getCustpmttypes(0);
    boolean validTransaction = false;
    for (CustPmtMap tempCustPmtMap : paymentTypes) {
      if (tempCustPmtMap.getPaymentid() == custPmtMap.getPaymentid()) {
        validTransaction = true;
        break;
      }
    }
    if (!validTransaction) {
      throw new CustomerException("Customer is not authorized to make requested change");
    }
    custPmtMap.update();
    return customer.getCustpmttypes(0);
  }

  @WebMethod
  public List<DeviceInfo> getDeviceList(Customer customer) {
    LoggerHelper logHelper = new LoggerHelper("getDeviceList", customer);
    if (customer == null || customer.getId() <= 0) {
      throw new CustomerException("Customer object must be provided");
    }
    logHelper.logMethodExit();
    return customer.retrieveDeviceList();
  }

  @WebMethod
  public DeviceInfo addDeviceInfoObject(Customer customer, DeviceInfo deviceInfo) {
    LoggerHelper logHelper = new LoggerHelper("addDeviceInfoObject", customer, deviceInfo);
    if (customer == null) {
      throw new CustomerException("Customer information must be populated");
    }
    if (deviceInfo == null) {
      throw new DeviceException("Device Information must be populated");
    } else {
      if (deviceInfo.getDeviceId() != 0) {
        throw new DeviceException("Cannot add a Device if the ID is already established");
      }
      if (deviceInfo.getAccountNo() <= 0) {
        throw new DeviceException("Account Number cannot be empty");
      }
    }
    if (customer.getId() != deviceInfo.getCustId()) {
      throw new CustomerException("Cannot save a device to a different customer");
    }
    deviceInfo.save();
    logHelper.logMethodReturn(deviceInfo);
    return deviceInfo;
  }

  @WebMethod
  public List<DeviceInfo> deleteDeviceInfoObject(Customer customer, DeviceInfo deviceInfo) {
    LoggerHelper logHelper = new LoggerHelper("deleteDeviceInfoObject", customer, deviceInfo);
    if (customer == null) {
      throw new CustomerException("Customer Information must be provided");
    }
    if (deviceInfo == null) {
      throw new DeviceException("Device information must be provided");
    } else if (deviceInfo.getDeviceId() <= 0) {
      throw new DeviceException("Invalid Device Id");
    }
    deviceInfo.delete();
    logHelper.logMethodExit();
    return customer.retrieveDeviceList();

  }

  @WebMethod
  public void updateDeviceInfoObject(Customer customer, DeviceInfo deviceInfo) {
    LoggerHelper logHelper = new LoggerHelper("updateDeviceInfoObject", customer, deviceInfo);
    if (customer == null) {
      throw new CustomerException("Customer information must be populated");
    }
    if (deviceInfo == null) {
      throw new DeviceException("Device Information must be populated");
    } else {
      if (deviceInfo.getDeviceId() == 0) {
        throw new DeviceException("Cannot update a Device if the ID is not established");
      }
    }
    if (customer.getId() != deviceInfo.getCustId()) {
      throw new CustomerException("Cannot save a device to a different customer");
    }
    deviceInfo.save();
    logHelper.logMethodExit();
  }

  @WebMethod
  public NetworkInfo swapDevice(Customer customer, NetworkInfo oldNetworkInfo, DeviceInfo newDevice) {
    LoggerHelper logHelper = new LoggerHelper("swapDevice", customer, oldNetworkInfo, newDevice);
    if (customer == null || customer.getId() <= 0) {
      throw new CustomerException("Customer object must be provided");
    }
    if (oldNetworkInfo == null || oldNetworkInfo.getMdn() == null || oldNetworkInfo.getMdn().trim().isEmpty()) {
      throw new NetworkException("Existing Network Information must be provided");
    }
    if (newDevice == null || newDevice.getDeviceValue() == null || newDevice.getDeviceValue().trim().isEmpty()) {
      throw new DeviceException("Device Information must be specified");
    } else if (newDevice.getDeviceId() <= 0) {
      throw new DeviceException("Device Id must be specified");
    }
    NetworkInfo newNetworkInfo = null;
    try {
      // String oldEsn = "";
      String oldMdn = oldNetworkInfo.getMdn();
      oldNetworkInfo = getNetworkInfo(null, oldMdn);
      newNetworkInfo = getNetworkInfo(newDevice.getDeviceValue(), null);

      if (newNetworkInfo != null) {
        if (newNetworkInfo.getEsnmeiddec().equals(newDevice.getDeviceValue())
            || newNetworkInfo.getEsnmeidhex().equals(newDevice.getDeviceValue())) {
          if (newNetworkInfo.getStatus() != null) {
            if (newNetworkInfo.getStatus().equals(NetworkInterface.DEVICE_STATUS_ACTIVE)
                || newNetworkInfo.getStatus().equals(NetworkInterface.DEVICE_STATUS_SUSPENDED)) {
              throw new NetworkException("Device is currently assigned");
            }
          }
        }
      } else {
        newNetworkInfo = new NetworkInfo();
        switch (newDevice.getDeviceValue().trim().length()) {
        case NetworkInterface.ESN_HEX_LENGTH:
        case NetworkInterface.MEID_HEX_LENGTH:
          newNetworkInfo.setEsnmeidhex(newDevice.getDeviceValue());
          break;
        case NetworkInterface.ESN_DEC_LENGTH:
        case NetworkInterface.MEID_DEC_LENGTH:
          newNetworkInfo.setEsnmeiddec(newDevice.getDeviceValue());
          break;
        default:
          throw new NetworkException("Invalid New Device length");
        }
        newNetworkInfo = getNetworkInfo(newDevice.getDeviceValue(), null);
      }

      try {
        // Send the swap request to the network
        logger.info("Sending swap request for MDN " + oldNetworkInfo.getMdn() + " to DEVICE "
            + newDevice.getDeviceValue());
        networkimpl.swapESN(oldNetworkInfo, newNetworkInfo);

        // Save deviceInfo
        logger.info("Saving new device information");
        newDevice.save();

      } catch (NetworkException network_ex) {
        throw network_ex;
      } catch (Exception ex) {
        logger.warn(ex.getMessage());
      }
    } catch (MVNEException ex) {
      logger.warn(ex.getMessage());
      throw ex;
    }
    logHelper.logMethodReturn(newNetworkInfo);
    return newNetworkInfo;
  }

  @WebMethod
  public List<UsageDetail> getCustomerChargeHistory(Customer customer, int accountNo, String mdn) {
    return customer.getChargeHistory(accountNo, mdn);
  }

  @WebMethod
  public void sendWelcomeNotification(Customer customer, Account account) {
    if (customer == null || customer.getId() <= 0) {
      throw new CustomerException("invalid customer object");
    }
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("account object is invalid.");
    }
    if (account.getFirstname() == null || account.getFirstname().trim().isEmpty() || account.getLastname() == null
        || account.getLastname().trim().isEmpty() || account.getContact_email() == null
        || account.getContact_email().trim().isEmpty()) {
      account = billingImpl.getAccountByAccountNo(account.getAccountno());
    }
    if (account.getFirstname() == null || account.getFirstname().trim().isEmpty() || account.getLastname() == null
        || account.getLastname().trim().isEmpty() || account.getContact_email() == null
        || account.getContact_email().trim().isEmpty()) {
      throw new BillingException("account information is incorrect for account number " + account.getAccountno());
    }

    Vector<NotificationParameter> notificationParameterList = new Vector<NotificationParameter>();
    NotificationParameter np = null;

    np = new NotificationParameter("firstName", account.getFirstname());
    notificationParameterList.add(np);

    np = new NotificationParameter("lastName", account.getLastname());
    notificationParameterList.add(np);

    np = new NotificationParameter("email", account.getContact_email());
    notificationParameterList.add(np);

    np = new NotificationParameter("truconnectManageSite", NotificationSystemImpl.truconnectManageSite);
    notificationParameterList.add(np);

    EmailNotification email = new EmailNotification();

    Vector<InternetAddress> toList = new Vector<InternetAddress>();
    try {
      InternetAddress to = new InternetAddress(account.getContact_email(), account.getFirstname() + " "
          + account.getLastname());
      toList.add(to);
    } catch (UnsupportedEncodingException ue_ex) {

    }
    email.setToList(toList);
    email.setFrom(notificationSystemImpl.getFrom());
    email.setBccList(notificationSystemImpl.getBccList());
    email.setSubject("Your new TruConnect Account");
    email.setNotificationParameters(notificationParameterList);
    email.setTemplate(EmailTemplate.welcome);
    email.setNotificationCategory(NotificationCategory.INFO);
    email.setNotificationType(NotificationType.EMAIL);
    email.setCustId(customer.getId());
    NotificationSender notificationSender = new NotificationSender(email);
    notificationSender.send();
  }

  @WebMethod
  public void sendProcessErrorNotification(Customer customer, Account account, NetworkInfo networkInfo, String mdn,
      String action, String subject, String to, String message) {
    // EmailNotification email = new EmailNotification();
    // email.setFrom(notificationSystemImpl.getFrom());
    // email.setNotificationCategory(NotificationCategory.WARNING);
    // email.setNotificationType(NotificationType.EMAIL);
    // email.setTemplate(EmailTemplate.truConnectProcessFailure);
    // email.setSubject(subject);
    // email.setCustId(customer.getId());
    // email.setNotificationId(0);
    // email.setTo(to);
    //
    // Vector<NotificationParameter> notificationParameterList = new
    // Vector<NotificationParameter>();
    // NotificationParameter notificationParameter = new
    // NotificationParameter();
    // notificationParameter.setKey("error");
    // notificationParameter.setValue(message);
    // notificationParameterList.add(notificationParameter);
    //
    // notificationParameter = new NotificationParameter();
    // notificationParameter.setKey("userName");
    // notificationParameter.setValue(account.getFirstname()+" "+account.getLastname());
    // notificationParameterList.add(notificationParameter);
    //
    // notificationParameter = new NotificationParameter();
    // notificationParameter.setKey("custId");
    // notificationParameter.setValue(Integer.toString(customer.getId()));
    // notificationParameterList.add(notificationParameter);
    //
    // notificationParameter = new NotificationParameter();
    // notificationParameter.setKey("accountno");
    // notificationParameter.setValue(Integer.toString(account.getAccountno()));
    // notificationParameterList.add(notificationParameter);
    //
    // notificationParameter = new NotificationParameter();
    // notificationParameter.setKey("mdn");
    // notificationParameter.setValue(mdn==null?"Not Bound":mdn);
    // notificationParameterList.add(notificationParameter);
    //
    // notificationParameter = new NotificationParameter();
    // notificationParameter.setKey("esn");
    // if( networkInfo != null ) {
    // notificationParameter.setValue(networkInfo.getEsnmeiddec()==null?"Not Bound":networkInfo.getEsnmeiddec());
    // } else {
    // notificationParameter.setValue("Not Bound");
    // }
    // notificationParameterList.add(notificationParameter);
    //
    // notificationParameter = new NotificationParameter();
    // notificationParameter.setKey("action");
    // notificationParameter.setValue(action);
    // notificationParameterList.add(notificationParameter);
    //
    // email.setNotificationParameters(notificationParameterList);
    //
    // NotificationSender notificationSender = new NotificationSender(email);
    // notificationSender.run();
    // try {
    // notificationSender.join();
    // } catch( InterruptedException int_ex ) {
    //
    // }
  }

  @WebMethod
  public void clearJMSQueue() {
    logger.info("Attempting to clear the JMS Queue");
    TestNotificationHandler tnh = new TestNotificationHandler();
    tnh.run();
    logger.info("clearJMSQueue done being called...");
  }

  @WebMethod
  public void sendNotification(Customer customer, EmailNotification notification) {
    if (customer == null || customer.getId() <= 0) {
      throw new CustomerException("Customer must be provided");
    }
    if (notification == null) {
      throw new NotificationException("Notification must be provided");
    } else {
      if (notification.getTemplate() == null) {
        throw new NotificationException("Template must be specified");
      }
    }

    Vector<InternetAddress> toList = new Vector<InternetAddress>();
    try {
      InternetAddress to = new InternetAddress(notification.getTo());
      toList.add(to);
    } catch (AddressException ue_ex) {

    }
    notification.setBccList(notificationSystemImpl.getBccList());
    notification.setFrom(notificationSystemImpl.getFrom());
    NotificationSender notificationSender = new NotificationSender(notification);
    notificationSender.send();
  }

  private void sendPaymentFailedNotification(Customer customer, Account account, PaymentTransaction paymentTransaction) {
    logger.info("Preparing Failed Notification message");
    if (account.getFirstname() == null || account.getLastname() == null || account.getContact_email() == null) {
      logger.info("Binding account information");
      account = billingImpl.getAccountByAccountNo(account.getAccountno());
    }
    if (account.getContact_email() == null || account.getContact_email().trim().isEmpty()) {
      account.setContact_email("support@truconnect.com");
    }

    String customerName = account.getFirstname() + " " + account.getLastname();
    NotificationParameter notificationParameter = null;
    Vector<NotificationParameter> notificationParametersList = new Vector<NotificationParameter>();
    Vector<InternetAddress> toList = new Vector<InternetAddress>();
    try {
      InternetAddress to = new InternetAddress(account.getContact_email(), customerName);
      toList.add(to);
    } catch (UnsupportedEncodingException encoding_ex) {
      logger.warn(encoding_ex.getMessage());
    }
    notificationParameter = new NotificationParameter("firstName", account.getFirstname());
    notificationParametersList.add(notificationParameter);
    notificationParameter = new NotificationParameter("lastName", account.getLastname());
    notificationParametersList.add(notificationParameter);

    EmailNotification emailNotification = new EmailNotification();
    emailNotification.setNotificationCategory(NotificationCategory.WARNING);
    emailNotification.setNotificationType(NotificationType.EMAIL);
    emailNotification.setTemplate(EmailTemplate.paymentFailed);
    emailNotification.setToList(toList);
    emailNotification.setFrom(notificationSystemImpl.getFrom());
    emailNotification.setBccList(notificationSystemImpl.getBccList());
    emailNotification.setSubject("Top-Up Payment processing failure");
    emailNotification.setNotificationParameters(notificationParametersList);
    emailNotification.setCustId(customer.getId());
    logger.info("Sending " + emailNotification.getTemplate() + " email");
    NotificationSender notificationSender = new NotificationSender(emailNotification);
    notificationSender.send();
  }

  private void sendPaymentNotification(Customer customer, Account account, PaymentTransaction paymentTransaction) {
    assert customer != null && customer.getId() > 0 : "Customer invalid";
    assert account != null && account.getAccountno() > 0 : "Account invalid";
    logger.info("retrieving top up amount");
    CustTopUp custTopUp = customer.getTopupAmount(account);

    if (account.getFirstname() == null || account.getLastname() == null || account.getContact_email() == null) {
      logger.info("Binding account information");
      account = billingImpl.getAccountByAccountNo(account.getAccountno());
    }
    assert account.getContact_email() != null : "Email is blank";

    DeviceInfo deviceInfo = null;
    try {
      logger.info("binding device information");
      logger.info("getting device information for CustomerId " + customer.getId() + " and account number "
          + account.getAccountno());
      List<DeviceInfo> deviceInfoList = customer.retrieveDeviceList(account.getAccountno());
      if (deviceInfoList != null) {
        logger.info("Customer has " + deviceInfoList.size() + " devices...binding to the first one...");
        for (DeviceInfo tempDeviceInfo : deviceInfoList) {
          deviceInfo = tempDeviceInfo;
          logger.info(deviceInfo.toString());
          break;
        }
      }
      if (deviceInfo == null) {
        throw new NullPointerException("Device information not found");
      }
    } catch (Exception ex) {
      logger.warn("Error Binding device information...Using Account number instead...");
      logger.warn(ex.getMessage());
      deviceInfo = new DeviceInfo();
      deviceInfo.setDeviceLabel(account.getFirstname() + "'s Account " + account.getAccountno());
    }

    assert paymentTransaction != null : "Unable to send notification without a valid transaction for Customer "
        + customer.getId() + ".";

    logger.info("Binding payment information");
    // if( customer.custpmttypes != null )
    CreditCard creditCard = getCreditCardDetail(paymentTransaction.getPmtId());
    assert creditCard != null : "PaymentInformation could not be found";

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

    logger.info("loading parameters");

    Vector<NotificationParameter> notificationParameterList = new Vector<NotificationParameter>();
    NotificationParameter np = new NotificationParameter();
    // firstname
    np.setKey("firstName");
    np.setValue(account.getFirstname());
    notificationParameterList.add(np);
    // lastname
    np = new NotificationParameter("lastName", account.getLastname());
    notificationParameterList.add(np);
    // balance
    String accountBalance = account.getBalance() == null ? "0.00" : account.getBalance();
    double balance = Double.parseDouble(accountBalance) + Double.parseDouble(paymentTransaction.getPaymentAmount());
    np = new NotificationParameter("balance", NumberFormat.getCurrencyInstance().format(balance));
    notificationParameterList.add(np);

    // truconnectManagesite
    np = new NotificationParameter("truconnectManageSite", NotificationSystemImpl.truconnectManageSite);
    notificationParameterList.add(np);

    // recentActivitySite

    // billAddress1
    String billAddress1 = creditCard.getAddress1() == null ? " " : creditCard.getAddress1();
    np = new NotificationParameter("billAddress1", billAddress1);
    notificationParameterList.add(np);
    // billAddress2
    String billAddress2 = creditCard.getAddress2() == null ? " " : creditCard.getAddress2();
    np = new NotificationParameter("billAddress2", billAddress2);
    notificationParameterList.add(np);
    // billCity
    String billCity = creditCard.getCity() == null ? " " : creditCard.getCity();
    np = new NotificationParameter("billCity", billCity);
    notificationParameterList.add(np);
    // billState
    np = new NotificationParameter("billState", creditCard.getState());
    notificationParameterList.add(np);
    // billZip
    np = new NotificationParameter("billZip", creditCard.getZip());
    notificationParameterList.add(np);

    // pmtDate
    np = new NotificationParameter("pmtDate", sdf.format(paymentTransaction.getBillingUnitDate()));
    notificationParameterList.add(np);
    // invoiceNumber --Currently BillTrackingId
    np = new NotificationParameter("invoiceNumber", Integer.toString(paymentTransaction.getBillingTrackingId()));
    notificationParameterList.add(np);

    // deviceLabel
    np = new NotificationParameter("deviceLabel", deviceInfo.getDeviceLabel());
    notificationParameterList.add(np);

    // pmt source
    String source = "*-" + paymentTransaction.getPaymentSource();
    if (paymentTransaction.getPaymentMethod().equals("AmericanExpress")) {
      source = "-*" + paymentTransaction.getPaymentSource();
    }
    source = paymentTransaction.getPaymentMethod() + " " + source;
    np = new NotificationParameter("pmtSource", source);
    notificationParameterList.add(np);
    // quantity
    double quantity = 1.0;
    DecimalFormat df = new DecimalFormat("0");
    quantity = Double.parseDouble(paymentTransaction.getPaymentAmount())
        / Double.parseDouble(custTopUp.getTopupAmount());
    np = new NotificationParameter("quantity", df.format(quantity));
    notificationParameterList.add(np);
    // topupAmount
    np = new NotificationParameter("topupAmount", NumberFormat.getCurrencyInstance().format(
        Double.parseDouble(custTopUp.getTopupAmount())));
    notificationParameterList.add(np);
    // total = quantity*topupAmount
    np = new NotificationParameter("total", NumberFormat.getCurrencyInstance().format(
        Double.parseDouble(custTopUp.getTopupAmount()) * quantity));
    notificationParameterList.add(np);

    // subTotal = sum(items)
    Double subTotal = Double.parseDouble(custTopUp.getTopupAmount()) * quantity;
    np = new NotificationParameter("subTotal", NumberFormat.getCurrencyInstance().format(subTotal));
    notificationParameterList.add(np);
    // taxRate = 0
    double taxRate = 0;
    np = new NotificationParameter("taxRate", taxRate == 0 ? "0.00" : Double.toString(taxRate));
    notificationParameterList.add(np);

    // taxedAmount = taxRate * subTotal
    double taxedAmount = subTotal * taxRate;
    np = new NotificationParameter("taxedAmount", NumberFormat.getCurrencyInstance().format(taxedAmount));
    notificationParameterList.add(np);

    // totalAmountWithTax = taxedAmount + subTotal
    double totalAmountWithTax = taxedAmount + subTotal;
    np = new NotificationParameter("totalAmountWithTax", NumberFormat.getCurrencyInstance().format(totalAmountWithTax));
    notificationParameterList.add(np);

    logger.info("Parameters loaded...Preparing email for consignment");

    EmailNotification email = new EmailNotification();

    Vector<InternetAddress> toList = new Vector<InternetAddress>();
    try {
      InternetAddress to = new InternetAddress(account.getContact_email(), account.getFirstname() + " "
          + account.getLastname());
      toList.add(to);
    } catch (UnsupportedEncodingException ue_ex) {

    }
    email.setToList(toList);
    email.setFrom(notificationSystemImpl.getFrom());
    email.setBccList(notificationSystemImpl.getBccList());
    email.setSubject("Thank you for your payment");
    email.setNotificationParameters(notificationParameterList);
    if (billAddress2 == null || billAddress2.trim().isEmpty()) {
      email.setTemplate(EmailTemplate.topup2);
    } else {
      email.setTemplate(EmailTemplate.topup);
    }
    email.setNotificationCategory(NotificationCategory.INFO);
    email.setNotificationType(NotificationType.EMAIL);
    email.setCustId(customer.getId());
    NotificationSender notificationSender = new NotificationSender(email);
    notificationSender.send();

    if (email.getNotificationId() != 0) {
      logger.info("Saving pmt invoice mapping");
      PaymentInvoice paymentInvoice = new PaymentInvoice();
      paymentInvoice.setNotificationId(email.getNotificationId());
      paymentInvoice.setTransId(paymentTransaction.getTransId());
      paymentInvoice.save();
    }
  }

  @WebMethod
  public NetworkInfo reinstallCustomerDevice(Customer customer, DeviceInfo deviceInfo) {
    LoggerHelper logHelper = new LoggerHelper("reinstallCustomerDevice", customer, deviceInfo);
    NetworkInfo networkInfo = new NetworkInfo();
    int accountNo = 0;
    String esn = "";
    String externalId = ""; // Old ExternalId associated with the DeviceInfo
    boolean chargeMRC = false;
    Date lastActiveDate = null;
    // Date lastMRCDate = null;
    Date now = new Date();
    try {
      if (customer == null || customer.getId() <= 0) {
        throw new CustomerException("invalid Customer object");
      }

      if (deviceInfo == null || deviceInfo.getDeviceId() <= 0) {
        throw new DeviceException("Device Information must be provided");
      }

      logger.info("Retrieving device information");
      List<DeviceInfo> deviceInfoList = customer.retrieveDeviceList(deviceInfo.getDeviceId(), 0);
      if (deviceInfoList != null && deviceInfoList.size() > 0) {
        for (DeviceInfo tempDeviceInfo : deviceInfoList) {
          accountNo = tempDeviceInfo.getAccountNo();
          esn = tempDeviceInfo.getDeviceValue();
          deviceInfo = tempDeviceInfo;
        }
      } else {
        throw new DeviceException("Device Information cannot be located..does it belong to customer "
            + customer.getId());
      }

      logger.info("Verifying device...");
      networkInfo = getNetworkInfo(esn, null);
      if (networkInfo != null) {
        if (networkInfo.getEsnmeiddec().equals(deviceInfo.getDeviceValue())
            || networkInfo.getEsnmeidhex().equals(deviceInfo.getDeviceValue())) {
          if (networkInfo.getStatus() != null
              && (networkInfo.getStatus().equals(NetworkInterface.DEVICE_STATUS_ACTIVE) || networkInfo.getStatus()
                  .equals(NetworkInterface.DEVICE_STATUS_SUSPENDED))) {
            throw new NetworkException("Device is currently bound to another subscriber...");
          }
        }
      }
      logger.info("Device is not already in use");

      logger.info("make sure Kenan Account isn't closed");
      Account account = getAccountInfo(accountNo);
      if (account != null) {
        if (account.getInactive_date() != null) {
          SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
          throw new BillingException("Account " + accountNo + " has been closed as of "
              + sdf.format(account.getInactive_date()));
        }
      } else {
        throw new BillingException("Billing account " + accountNo + " could not be located.");
      }
      logger.info("Account " + accountNo + " is active");

      logger.info("Fetch old device information");
      List<DeviceAssociation> deviceAssociationList = customer.retrieveDeviceAssociationList(deviceInfo.getDeviceId());
      if (deviceAssociationList != null && deviceAssociationList.size() > 0) {
        for (DeviceAssociation deviceAssociation : deviceAssociationList) {
          logger.info("Setting the externalId and lastActiveDate " + deviceAssociation.getInactiveDate());
          externalId = deviceAssociation.getExternalId();
          lastActiveDate = deviceAssociation.getInactiveDate();
          break;
        }
        logger.info("Deactivated device's external id was " + externalId + "...using that as point of referenece");
      } else {
        logger.info("No old device associations could be found for customer " + customer.getId() + " and device id "
            + deviceInfo.getDeviceId());
      }

      logger.info("Calculating whether to charge MRC");
      List<UsageDetail> usageDetailList = getCustomerChargeHistory(customer, accountNo, externalId);
      if (usageDetailList != null && usageDetailList.size() > 0) {
        logger.info("looking for latest access fee payment");
        for (UsageDetail usageDetail : usageDetailList) {
          if (usageDetail.getUsageType().equals("Access Fee")) {
            logger.info("Found an End Date for MRC of " + usageDetail.getEndTime());
            lastActiveDate = usageDetail.getEndTime();
            break;
          }
        }
        logger.info("Last access fee payment was made on " + lastActiveDate);
        if (lastActiveDate.getTime() > now.getTime()) {
          logger.info("MRC Charge won't be necessary");
        } else {
          logger.info("MRC will be charged");
          chargeMRC = true;
        }
      } else {
        logger.info("Charge history could not be found for customer " + customer.getId() + ", accountNo "
            + deviceInfo.getAccountNo() + ", externalId " + externalId);
      }

      logger.info("Reserving MDN");
      networkInfo = reserveMDN();
      switch (deviceInfo.getDeviceValue().length()) {
      case NetworkInterface.ESN_DEC_LENGTH:
      case NetworkInterface.MEID_DEC_LENGTH:
        networkInfo.setEsnmeiddec(deviceInfo.getDeviceValue());
        break;
      case NetworkInterface.ESN_HEX_LENGTH:
      case NetworkInterface.MEID_HEX_LENGTH:
        networkInfo.setEsnmeidhex(deviceInfo.getDeviceValue());
        break;
      default:
        throw new DeviceException("Device Value is not of a valid length");
      }

      logger.info("Activate MDN");
      activateService(customer, networkInfo);

      ServiceInstance serviceInstance = new ServiceInstance();
      serviceInstance.setExternalid(networkInfo.getMdn());

      logger.info("Build Kenan Service Instance");
      if (!chargeMRC) {
        createReinstallServiceInstance(account, serviceInstance, deviceInfo);
      } else {
        createServiceInstance(account, serviceInstance);
      }

    } catch (MVNEException mvne_ex) {
      logger.warn(mvne_ex.getMessage());
      logHelper.logMethodExit();
      throw mvne_ex;
    }
    logHelper.logMethodReturn(networkInfo);
    return networkInfo;
  }

  private void createReinstallServiceInstance(Account account, ServiceInstance serviceInstance, DeviceInfo deviceInfo) {
    LoggerHelper logHelper = new LoggerHelper("createReinstallServiceInstance", account, serviceInstance, deviceInfo);
    try {
      account.setServiceinstancelist(billingImpl.getServiceInstanceList(account));
    } catch (BillingException bill_ex) {
      logger.warn("Billing Exception thrown " + bill_ex.getMessage());
    }
    logger.info("adding service instance");
    billingImpl.addServiceInstance(account, serviceInstance);
    com.tscp.mvne.billing.Package lPackage = new com.tscp.mvne.billing.Package();
    logger.info("adding package");
    billingImpl.addPackage(account, serviceInstance, lPackage);
    com.tscp.mvne.billing.Component componentid = new com.tscp.mvne.billing.Component();
    componentid.setComponent_id(BillingSystem.COMPONENT_REINSTALL);
    logger.info("adding Component");
    billingImpl.addComponent(account, serviceInstance, lPackage, componentid);

    // Update device association for this customer
    try {
      logger.info("Adding new association");
      // logger.info("Updating Device Association");
      logger.info("attempting to retrieve subscr_no for EXTERNAL_ID " + serviceInstance.getExternalid());
      List<ServiceInstance> serviceInstanceList = billingImpl.getServiceInstanceList(account);
      if (serviceInstanceList != null) {
        for (ServiceInstance tempServiceInstance : serviceInstanceList) {
          if (tempServiceInstance.getExternalid().equals(serviceInstance.getExternalid())) {
            logger.info("Subscriber " + tempServiceInstance.getSubscrno() + " found");
            logger.info("Building Device Association Mapping");
            DeviceAssociation deviceAssociation = new DeviceAssociation();
            deviceAssociation.setDeviceId(deviceInfo.getDeviceId());
            deviceAssociation.setSubscrNo(tempServiceInstance.getSubscrno());
            logger.info("Saving device association");
            deviceAssociation.save();
            //
            if (deviceInfo.getDeviceStatusId() != DeviceStatus.ID_ACTIVE) {
              logger.info("DeviceInfo " + deviceInfo.getDeviceId() + " is not in active status...Activating");
              deviceInfo.setDeviceStatusId(DeviceStatus.ID_ACTIVE);
              deviceInfo.setEffectiveDate(new Date());
              deviceInfo.save();
            }
            break;
          }
        }
      }
    } catch (Exception ex) {
      logger.info("Error updating Device Association in createReinstallServiceInstance method");
      logger.warn(ex.getMessage());
    }
    logHelper.logMethodExit();
  }

  private void paymentUpdatedRoutine(Customer customer) {
    if (customer == null || customer.getId() <= 0) {
      throw new CustomerException("invalid customer object");
    }

    List<CustAcctMapDAO> custAcctMapDAOList = customer.getCustaccts();
    logger.info("Retrieve account list from CUST_ACCT_MAP");
    if (custAcctMapDAOList != null && custAcctMapDAOList.size() > 0) {
      // billingImpl.get
      for (CustAcctMapDAO custAcctMapDAO : custAcctMapDAOList) {
        Account account = new Account();
        account.setAccountno(custAcctMapDAO.getAccount_no());
        logger.info("update all services associated with this customer to be status 0 in the threshold");
        logger.info("Updating service instances for account " + account.getAccountno());
        Account loadedAccount = billingImpl.getAccountByAccountNo(account.getAccountno());
        for (ServiceInstance serviceInstance : loadedAccount.getServiceinstancelist()) {
          logger.info("Updating threshold value for ServiceInstance " + serviceInstance.getExternalid() + " to "
              + BillingSystem.SERVICE_INSTANCE_RESTORED);
          billingImpl.updateServiceInstanceStatus(serviceInstance, BillingSystem.SERVICE_INSTANCE_RESTORED);
        }
      }
      // throw new
      // PaymentException("submitPaymentByPaymentId","Error posting payment. :: "+response.getConfdescr()+" "+response.getAuthcode());

    }

  }

  private void suspendSubscriber(ServiceInstance serviceInstance, DeviceInfo deviceInfo) {
    logger.info("Suspending subscriber Network, Billing and Device if present");
    Account account = new Account();
    try {
      account.setAccountno(billingImpl.getAccountNoByTN(serviceInstance.getExternalid()));
      if (account.getAccountno() == 0) {
        throw new WebServiceException("Unable to get account number for External ID " + serviceInstance.getExternalid());
      }
    } catch (MVNEException mvne_ex) {
      logger.warn(mvne_ex.getMessage(), mvne_ex);
    }
    bindServiceInstanceObject(account, serviceInstance);

    logger.info("suspending network element");
    NetworkInfo networkInfo = getNetworkInfo(null, serviceInstance.getExternalid());
    if (networkInfo == null) {
      logger.warn("Network Info returned null when querying for MDN " + serviceInstance.getExternalid());
      networkInfo = new NetworkInfo();
      networkInfo.setMdn(serviceInstance.getExternalid());
    } else {
      if (networkInfo.getStatus() != null) {
        if (networkInfo.getStatus().equals(NetworkInterface.DEVICE_STATUS_SUSPENDED)) {
          logger.info("Device " + networkInfo.getEsnmeiddec() + " is already suspended on the Network...skipping");
        } else if (networkInfo.getStatus().equals(NetworkInterface.DEVICE_STATUS_ACTIVE)) {
          logger.info("Suspending Service on the Network");
          networkimpl.suspendService(networkInfo);
        } else {
          throw new NetworkException("MDN is not in a suspendable state!");
        }
      } else {
        logger.warn("Invalid NetworkInfo Object");
        logger.info(networkInfo.toString());
        throw new NetworkException("No status found for MDN " + serviceInstance.getExternalid());
      }
    }

    logger.info("Updating Billing System with Hotlined Status " + BillingSystem.SERVICE_INSTANCE_HOTLINED);
    billingImpl.updateServiceInstanceStatus(serviceInstance, BillingSystem.SERVICE_INSTANCE_HOTLINED);

    if (deviceInfo != null) {
      logger.info("updating deviceInfo[" + deviceInfo.getDeviceId() + "] to RX - "
          + DeviceStatus.DESC_RELEASED_SYSTEM_REACTIVATE);
      deviceInfo.setDeviceStatusId(DeviceStatus.ID_RELEASED_SYSTEM_REACTIVATE);
      deviceInfo.save();
    }
    logger.info("Done suspending subscriber");
  }

  private void restoreSubscriber(ServiceInstance serviceInstance, DeviceInfo deviceInfo) {
    logger.info("Restoring subscriber Network, Billing and Device if present");
    Account account = new Account();
    try {
      account.setAccountno(billingImpl.getAccountNoByTN(serviceInstance.getExternalid()));
      if (account.getAccountno() == 0) {
        throw new WebServiceException("Unable to get account number for External ID " + serviceInstance.getExternalid());
      }
    } catch (MVNEException mvne_ex) {
      logger.warn(mvne_ex.getMessage(), mvne_ex);
      throw mvne_ex;
    }
    bindServiceInstanceObject(account, serviceInstance);

    logger.info("restoring network element");
    NetworkInfo networkInfo = getNetworkInfo(null, serviceInstance.getExternalid());
    if (networkInfo == null) {
      networkInfo = new NetworkInfo();
      networkInfo.setMdn(serviceInstance.getExternalid());
    }
    if (networkInfo.getStatus() != null && networkInfo.getStatus().equals(NetworkInterface.DEVICE_STATUS_ACTIVE)) {
      logger.info("MDN " + serviceInstance.getExternalid() + " is already in a restored state");
    } else {
      logger.info("Restoring service on the network");
      networkimpl.restoreService(networkInfo);
    }

    logger.info("Updating Billing System with restored flag...");
    billingImpl.updateServiceInstanceStatus(serviceInstance, BillingSystem.SERVICE_INSTANCE_RESTORED);

    if (deviceInfo != null) {
      logger.info("updating deviceInfo[" + deviceInfo.getDeviceId() + "] to AC - " + DeviceStatus.DESC_ACTIVE);
      deviceInfo.setDeviceStatusId(DeviceStatus.ID_ACTIVE);
      deviceInfo.save();
    }
    logger.info("Done restoring subscriber");
  }

  @WebMethod
  public void disconnectFromNetwork(NetworkInfo networkInfo) {
    logger.info("Disconnecting from Network MDN " + networkInfo.getMdn());
    networkimpl.disconnectService(networkInfo);
  }

  @WebMethod
  public void disconnectServiceInstanceFromKenan(Account account, ServiceInstance serviceInstance) {
    billingImpl.deleteServiceInstance(account, serviceInstance);
  }

  @WebMethod
  public List<CustAddress> getCustAddressList(Customer customer, int addressId) {
    if (customer == null) {
      throw new CustomerException("Customer object must be specified");
    }
    if (addressId < 0) {
      throw new CustomerException("Invalid AddressId Value");
    }
    return customer.getCustAddressList(addressId);
  }

  @WebMethod
  public List<CustAddress> addCustAddress(Customer customer, CustAddress custAddress) {
    if (customer == null) {
      throw new CustomerException("Invalid customer object");
    }
    if (custAddress == null || custAddress.getAddressId() != 0) {
      throw new CustomerException("Invalid customer address object ");
    }
    if (custAddress.getCustId() != customer.getId()) {
      throw new CustomerException("Invalid action...cannot save address for this customer");
    }
    custAddress.save();
    return customer.getCustAddressList(custAddress.getAddressId());
  }

  @WebMethod
  public List<CustAddress> updateCustAddress(Customer customer, CustAddress custAddress) {
    if (customer == null) {
      throw new CustomerException("Invalid customer object");
    }
    if (custAddress == null || custAddress.getAddressId() <= 0) {
      throw new CustomerException("Invalid customer address object ");
    }
    if (custAddress.getCustId() != customer.getId()) {
      throw new CustomerException("Invalid action...cannot save address for this customer");
    }
    custAddress.save();
    return customer.getCustAddressList(custAddress.getAddressId());
  }

  @WebMethod
  public List<CustAddress> deleteCustAddress(Customer customer, CustAddress custAddress) {
    if (customer == null) {
      throw new CustomerException("Invalid customer object");
    }
    if (custAddress == null || custAddress.getAddressId() <= 0) {
      throw new CustomerException("Invalid customer address object ");
    }
    if (custAddress.getCustId() != customer.getId()) {
      throw new CustomerException("Invalid action...cannot save address for this customer");
    }

    custAddress.delete();
    return customer.getCustAddressList(custAddress.getAddressId());
  }

  @WebMethod
  public void deleteCustAcctMapReference(Customer customer, Account account) {
    if (customer == null) {
      throw new CustomerException("Invalid customer object");
    }
    if (account == null || account.getAccountno() <= 0) {
      throw new BillingException("Invalid Account object");
    }
    customer.deleteCustAccts(account);
  }

  @WebMethod
  public PaymentInvoice getCustomerInvoice(Customer customer, int transId) {
    if (customer == null) {
      throw new CustomerException("Invalid customer object");
    }
    return customer.getPaymentInvoice(transId);
  }

  @WebMethod
  public CustInfo getCustInfo(Customer customer) {
    if (customer == null || customer.getId() <= 0) {
      throw new CustomerException("Invalid Customer object");
    }
    return customer.getCustInfo();
  }

}
