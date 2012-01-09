package com.tscp.mvne.network;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import com.tscp.mvno.webservices.API3;
import com.tscp.mvno.webservices.API3Service;
import com.tscp.mvno.webservices.ActivateReserveSubscription;
import com.tscp.mvno.webservices.ApiActivateReserveSubscriptionResponseHolder;
import com.tscp.mvno.webservices.ApiGeneralResponseHolder;
import com.tscp.mvno.webservices.ApiPendingSubscriptionNPAResponseHolder;
import com.tscp.mvno.webservices.ApiResellerSubInquiryResponseHolder;
import com.tscp.mvno.webservices.ApiSwapESNResponseHolder;
import com.tscp.mvno.webservices.PendingSubscriptionNPA;
import com.tscp.mvno.webservices.Sali2;

public class NetworkImpl implements NetworkInterface {

  private API3Service apiservice;
  private API3 api;

  private static String inputPropertiesFile = "com/tscp/mvne/config/connection.tscpmvne.properties";;
  private static Properties props;

  String namespace = "http://webservices.mvno.tscp.com/";
  String servicename = "API3Service";
  String wsdlLocation = "http://uscaelmux23:6000/API3/API3Service?WSDL";

  public NetworkImpl() {
    init();
  }

  private void init() {
    System.out.println("tc! initializing networkImpl");
    ClassLoader cl = NetworkImpl.class.getClassLoader();
    InputStream inStream = cl.getResourceAsStream(inputPropertiesFile);
    System.out.println("retrieved " + inputPropertiesFile + " as stream");
    if (props == null) {
      props = new Properties();
    }
    try {
      props.load(inStream);
      namespace = props.getProperty("network.namespace");
      servicename = props.getProperty("network.servicename");
      wsdlLocation = props.getProperty("network.location");
      System.out.println("tc! network wsdl set to: " + wsdlLocation);
    } catch (IOException io_ex) {
      System.out.println("properties file not found..." + io_ex.getMessage());
      throw new NetworkException("Error initializing NetworkImpl...Properties file not found " + io_ex.getMessage(),
          io_ex);
    }

    try {
      apiservice = new API3Service(new URL(wsdlLocation), new QName(namespace, servicename));
    } catch (MalformedURLException url_ex) {
      url_ex.printStackTrace();
      apiservice = new API3Service();
    } catch (WebServiceException ws_ex) {
      System.out.println("WebServiceException thrown...");
      ws_ex.printStackTrace();
      if (ws_ex.getCause() != null) {
        ws_ex.getCause().printStackTrace();
        System.out.println("Thrown by :: " + ws_ex.getCause().getClass() + " :: " + ws_ex.getCause().getMessage()
            + " on " + wsdlLocation);
        if (ws_ex.getCause() instanceof java.net.ConnectException) {
          System.out.println("System is in maintenance mode...Please try your request at a later time.");
          throw new NetworkException("System is in maintenance mode...Please try your request at a later time.", ws_ex
              .getCause());
        } else if (ws_ex.getCause() instanceof FileNotFoundException) {
          System.out.println("Invalid Service endpoint " + wsdlLocation + ". ");
          throw new NetworkException("Invalid Service endpoint " + wsdlLocation + ". ", ws_ex.getCause());
        }
      }
    }
    api = apiservice.getAPI3Port();
  }

  @Override
  public NetworkInfo getNetworkInfo(String esn, String mdn) throws NetworkException {
    if ((esn == null || esn.length() == 0) && (mdn == null || mdn.length() == 0)) {
      throw new NetworkException("getNetworkInfo", "ESN or an MDN required");
    } else if (esn != null && esn.length() > 0 && mdn != null && mdn.length() > 0) {
      throw new NetworkException("getNetworkInfo", "Only an ESN or an MDN may be used.");
    } else {
      System.out.println("BEGIN => Network informational query for "
          + (esn == null ? "MDN :: " + mdn : "ESN :: " + esn));
      NetworkInfo networkinfo = new NetworkInfo();
      networkinfo.setEsnmeiddec(esn);
      networkinfo.setMdn(mdn);
      ApiResellerSubInquiryResponseHolder subscription = api.apIresellerV2SubInquiry(esn, mdn);
      if (subscription != null) {
        if (subscription.getAccessNbrAsgmList() != null && subscription.getAccessNbrAsgmList().getValue() != null
            && subscription.getAccessNbrAsgmList().getValue().size() >= 1) {
          if (subscription.getAccessNbrAsgmList().getValue().get(0).getSwitchStatusCd().equals("C")) {
            networkinfo
                .setExpirationdate(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffDt());
            networkinfo
                .setExpirationtime(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffTm());
          }
          networkinfo.setMdn(subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbr());
          networkinfo.setMsid(subscription.getAccessNbrAsgmList().getValue().get(0).getMSID());
          networkinfo.setStatus(subscription.getAccessNbrAsgmList().getValue().get(0).getSwitchStatusCd());
        }

        if (subscription.getAccessEqpAsgmList() != null && subscription.getAccessEqpAsgmList().getValue() != null
            && subscription.getAccessEqpAsgmList().getValue().size() >= 1) {
          networkinfo.setEffectivedate(subscription.getAccessEqpAsgmList().getValue().get(0).getEqpEffDt());
          networkinfo.setEffectivetime(subscription.getAccessEqpAsgmList().getValue().get(0).getEqpEffTm());
          networkinfo.setEsnmeiddec(subscription.getAccessEqpAsgmList().getValue().get(0).getESNMEIDDcmlId());
          networkinfo.setEsnmeidhex(subscription.getAccessEqpAsgmList().getValue().get(0).getESNMEIDHexId());
        }
        System.out.println("MDN      :: " + networkinfo.getMdn());
        System.out.println("MSID     :: " + networkinfo.getMsid());
        System.out.println("EffDate  :: " + networkinfo.getEffectivedate());
        System.out.println("EffTime  :: " + networkinfo.getEffectivetime());
        System.out.println("ExpDate  :: " + networkinfo.getExpirationdate());
        System.out.println("ExpTime  :: " + networkinfo.getExpirationtime());
        System.out.println("ESNDec   :: " + networkinfo.getEsnmeiddec());
        System.out.println("ESNHex   :: " + networkinfo.getEsnmeidhex());
        System.out.println("Status   :: " + networkinfo.getStatus());
        System.out.println("DONE => Network informational query for "
            + (esn == null ? "MDN :: " + mdn : "ESN :: " + esn));
        return networkinfo;
      } else {
        NetworkException networkexception = new NetworkException("getNetworkInfo", "Subscriber not found for "
            + (networkinfo.getEsnmeiddec() == null || networkinfo.getEsnmeiddec().length() == 0 ? " ESN "
                + networkinfo.getEsnmeiddec() : " MDN " + networkinfo.getMdn()));
        networkexception.setNetworkinfo(networkinfo);
        System.out.println("Subscriber not found for "
            + (networkinfo.getEsnmeiddec() == null || networkinfo.getEsnmeiddec().length() == 0 ? " ESN "
                + networkinfo.getEsnmeiddec() : " MDN " + networkinfo.getMdn()));
        System.out.println("DONE => Network informational query for "
            + (esn == null ? "MDN :: " + mdn : "ESN :: " + esn));
        throw networkexception;
      }
    }
  }

  @Override
  public NetworkInfo reserveMDN(String csa, String priceplan, List<String> soclist) throws NetworkException {
    PendingSubscriptionNPA pendingsubscription = new PendingSubscriptionNPA();

    Sali2 sali2 = new Sali2();
    if (priceplan == null) {
      sali2.setSvcCode("PRSCARD5");
    } else {
      sali2.setSvcCode(priceplan);
    }
    pendingsubscription.setPricePlans(sali2);

    if (csa == null) {
      pendingsubscription.setCSA("LAXLAX213");
    } else {
      pendingsubscription.setCSA(csa);
    }

    ApiPendingSubscriptionNPAResponseHolder subscription = api.apIreserveSubscriptionNPA(pendingsubscription);
    if (subscription != null) {
      try {
        NetworkInfo networkinfo = new NetworkInfo();
        networkinfo.setMdn(subscription.getSubNPA().getMDN());
        networkinfo.setMsid(subscription.getSubNPA().getMSID());
        return networkinfo;
      } catch (NullPointerException npe) {
        throw new NetworkException("reserveMDN", "required object "
            + (subscription.getSubNPA() == null ? subscription.getSubNPA().getMDN() == null ? subscription.getSubNPA()
                .getMSID() == null ? npe.getMessage() : " MSID " : " MDN " : " SubNPA ") + " is null...");
      }
    } else {
      throw new NetworkException("reserveMDN", "returned subscription is empty...");
    }
  }

  @Override
  public void activateMDN(NetworkInfo networkinfo) throws NetworkException {
    if (networkinfo == null) {
      throw new NetworkException("activateMDN", "Network Information must be provided for service activation");
    } else {
      if ((networkinfo.getEsnmeiddec() == null || networkinfo.getEsnmeiddec().trim().length() == 0)
          && (networkinfo.getEsnmeidhex() == null || networkinfo.getEsnmeidhex().trim().length() == 0)) {
        throw new NetworkException("activateMDN", "Please specify an ESN or MEID to activate on...");
      }
      if (networkinfo.getMdn() == null || networkinfo.getMdn().trim().length() == 0) {
        throw new NetworkException("activateMDN", "Please specify an MDN to activate on...");
      }
      if (networkinfo.getMsid() == null || networkinfo.getMsid().trim().length() == 0) {
        throw new NetworkException("activateMDN", "Please specify the MSID associate with MDN " + networkinfo.getMdn()
            + "...");
      }
    }

    ActivateReserveSubscription activatereservesubscription = new ActivateReserveSubscription();

    // TODO this value isn't used anywhere, should we use this or the value from
    // networkInfo? NetworkInfo may always have both hex and dec populated
    String ESN = "";
    if (networkinfo.getEsnmeiddec() == null || networkinfo.getEsnmeiddec().trim().length() == 0) {
      ESN = networkinfo.getEsnmeidhex();
    } else {
      ESN = networkinfo.getEsnmeiddec();
    }
    activatereservesubscription.setESN(networkinfo.getEsnmeiddec());
    activatereservesubscription.setMDN(networkinfo.getMdn());
    activatereservesubscription.setMSID(networkinfo.getMsid());

    ApiActivateReserveSubscriptionResponseHolder response = api.apIactivatePendingSubscription(activatereservesubscription);
    if (response == null) {
      throw new NetworkException("activateMDN", "No response returned from Network Element...");
    } else {
      if (!response.getStatusMessage().equals("SUCCEED")) {
        NetworkException networkexception = new NetworkException("activateMDN", "Error activating Device "
            + networkinfo.getEsnmeiddec() + " against MDN " + networkinfo.getMdn() + " using MSID "
            + networkinfo.getMsid() + "..." + response.getResponseMessage());
        networkexception.setNetworkinfo(networkinfo);
        throw networkexception;
      }
    }

  }

  @Override
  public void suspendService(NetworkInfo networkinfo) throws NetworkException {
    if (networkinfo == null) {
      throw new NetworkException("suspendService", "Please provide a network element to be suspended...");
    } else {
      if (networkinfo.getMdn() == null || networkinfo.getMdn().trim().length() == 0) {
        throw new NetworkException("suspendService", "Please provide an MDN to be suspended...");
      }
    }
    /**
     * this value can be HTL to hotline but we're just going for full suspend.
     */
    String suspendcode = null;
    ApiGeneralResponseHolder response = api.apIsuspendSubscription(networkinfo.getMdn(), suspendcode);
    if (response == null) {
      throw new NetworkException("suspendService", "No response returned from Network Interface...");
    } else {
      if (!response.getStatusMessage().equals("SUCCEED")) {
        NetworkException networkexception = new NetworkException("suspendService", "Failure to suspend "
            + networkinfo.getMdn() + "...Message returned from Network Interface was " + response.getResponseMessage());
        networkexception.setNetworkinfo(networkinfo);
        throw networkexception;
      }
    }

  }

  @Override
  public void restoreService(NetworkInfo networkinfo) throws NetworkException {
    if (networkinfo == null) {
      throw new NetworkException("restoreService", "Please provide a network element to be restored...");
    } else {
      if (networkinfo.getMdn() == null || networkinfo.getMdn().trim().length() == 0) {
        throw new NetworkException("restoreService", "Please provide an MDN to be restored...");
      }
    }

    ApiGeneralResponseHolder response = api.apIrestoreSubscription(networkinfo.getMdn());
    if (response == null) {
      throw new NetworkException("suspendService", "No response returned from Network Interface...");
    } else {
      if (!response.getStatusMessage().equals("SUCCEED")) {
        NetworkException networkexception = new NetworkException("restoreService", "Failure to restore "
            + networkinfo.getMdn() + "...Message returned from Network Interface was " + response.getResponseMessage());
        networkexception.setNetworkinfo(networkinfo);
        throw networkexception;
      }
    }

  }

  @Override
  public void disconnectService(NetworkInfo networkinfo) throws NetworkException {
    if (networkinfo == null) {
      throw new NetworkException("disconnectService", "Please provide a network element");
    }
    if (networkinfo.getMdn() == null || networkinfo.getMdn().trim().length() != 10) {
      throw new NetworkException("disconnectService", "Please provide a 10 digit MDN to disconnect");
    }
    String expirationdate = null;
    NetworkInfo currentNetworkStatus = getNetworkInfo(null, networkinfo.getMdn());
    if (currentNetworkStatus != null && currentNetworkStatus.getExpirationdate() == null) {
      ApiGeneralResponseHolder response = api.apIexpireSubscription(networkinfo.getMdn(), expirationdate);
      if (response == null) {
        throw new NetworkException("disconnectService", "Error retrieving response information from network element...");
      } else {
        if (!response.getStatusMessage().equals("SUCCEED")) {
          NetworkException networkexception = new NetworkException("disconnectService",
              "Error returned when disconnecting service for MDN " + networkinfo.getMdn() + " :: "
                  + response.getApiResponseMessage() + " :: " + response.getStatusMessage() + " :: "
                  + response.getResponseMessage());
          networkexception.setNetworkinfo(networkinfo);
          throw networkexception;
        }
      }
    } else {
      if (currentNetworkStatus == null) {
        throw new NetworkException("MDN " + networkinfo.getMdn() + " is not found on the network.");
      } else {
        System.out.println("MDN " + networkinfo.getMdn() + " is already in disconnected status...");
      }
    }
  }

  @Override
  public void swapESN(NetworkInfo oldNetworkInfo, NetworkInfo newNetworkInfo) throws NetworkException {
    // throw new
    // NetworkException("swapESN","This method is not yet available...");
    if (oldNetworkInfo == null || ((oldNetworkInfo.getMdn() == null || oldNetworkInfo.getMdn().trim().isEmpty()))) {
      throw new NetworkException("Old network information MDN must be provided");
    }
    if (newNetworkInfo == null
        || ((newNetworkInfo.getEsnmeiddec() == null || newNetworkInfo.getEsnmeiddec().trim().isEmpty()) && (newNetworkInfo
            .getEsnmeidhex() == null || newNetworkInfo.getEsnmeidhex().trim().isEmpty()))) {
      throw new NetworkException("New network information must be provided");
    }
    String newEsn = "";
    if (newNetworkInfo.getEsnmeiddec() != null) {
      newEsn = newNetworkInfo.getEsnmeiddec();
      if (newEsn.length() != NetworkInterface.ESN_DEC_LENGTH && newEsn.length() != NetworkInterface.MEID_DEC_LENGTH) {
        throw new NetworkException("Dec ESN is not of a valid length");
      }
    }
    if (newNetworkInfo.getEsnmeidhex() != null) {
      newEsn = newNetworkInfo.getEsnmeidhex();
      if (newEsn.length() != NetworkInterface.ESN_HEX_LENGTH && newEsn.length() != NetworkInterface.MEID_HEX_LENGTH) {
        throw new NetworkException("Hex ESN is not of a valid length");
      }
    }
    ApiSwapESNResponseHolder responseHolder = api.apIswapESN(oldNetworkInfo.getMdn(), newEsn);
    if (responseHolder != null) {
      if (!responseHolder.getStatusMessage().equals("SUCCEED")) {
        throw new NetworkException("Error swapping to Device " + newEsn + " for MDN " + oldNetworkInfo.getMdn()
            + "... " + responseHolder.getResponseMessage());
      } else {
        newNetworkInfo.setMdn(oldNetworkInfo.getMdn());
        newNetworkInfo.setMsid(responseHolder.getMSID());
      }
    } else {
      throw new NetworkException("No response from gateway...");
    }
  }

}
