package com.tscp.mvne.unittest;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import com.tscp.mvne.network.NetworkImpl;
import com.tscp.mvne.network.NetworkInfo;
import com.tscp.mvno.webservices.API3;
import com.tscp.mvno.webservices.API3Service;
import com.tscp.mvno.webservices.ApiResellerSubInquiryResponseHolder;
import com.tscp.mvno.webservices.ApiValidNPAListResponseHolder;
import com.tscp.mvno.webservices.Npa;

public class SprintAPITester {

  API3 sprintApi;
  API3Service sprintApiService;

  public SprintAPITester() {
    String namespace = "http://webservices.mvno.tscp.com/";
    String servicename = "API3Service";
    String location = "http://uscaelmux23:6000/API3/API3Service?WSDL";
    try {
      sprintApiService = new API3Service(new URL(location), new QName(namespace, servicename));
    } catch (MalformedURLException url_ex) {
      System.out.println("URL Exception :: " + url_ex.getMessage());
      sprintApiService = new API3Service();
    } catch (WebServiceException ws_ex) {
      System.out.println("WebServiceException thrown...");
      // ws_ex.getCause().printStackTrace();
      System.out.println("Thrown by :: " + ws_ex.getCause().getClass());
      if (ws_ex.getCause() instanceof java.net.ConnectException) {
        System.out.println("System is in maintenance mode...Please try your request at a later time.");
      } else if (ws_ex.getCause() instanceof FileNotFoundException) {
        System.out.println("Invalid Service endpoint " + location + ". " + ws_ex.getCause().getMessage());
      }
      System.exit(-1);
    }
    sprintApi = sprintApiService.getAPI3Port();
  }

  public static void main(String[] args) {
    System.out.println("Testing Sprint API WebService...");

    SprintAPITester apiTester = new SprintAPITester();
    apiTester.retrieveValidNPAList("NEVSDG619");
    // apiTester.getSubscriberInfo(null,"2134543102");
    // apiTester.expireSubscription("2136031348");
    // apiTester.reserveMdn();
    apiTester.activateMdn();
    System.out.println("Testing completed...");
  }

  private void retrieveValidNPAList(String csa) {

    ApiValidNPAListResponseHolder npaList = sprintApi.apIgetValidNPAList(csa);

    if (npaList != null && npaList.getValidNPA() != null) {
      for (Npa npa : npaList.getValidNPA().getValidNPAList()) {
        System.out.println("Npa " + npa.getNpaValue());
      }
    }
  }

  private void getSubscriberInfo(String ESN, String MDN) {
    ApiResellerSubInquiryResponseHolder subscription = sprintApi.apIresellerV2SubInquiry(ESN, MDN);
    if (subscription != null) {
      if (subscription.getAccessEqpAsgmList() != null && subscription.getAccessEqpAsgmList().getValue() != null
          && subscription.getAccessEqpAsgmList().getValue().size() > 0) {
        System.out.println("Equipment :: " + subscription.getAccessEqpAsgmList().getValue().get(0).getESNMEIDDcmlId());
        System.out.println("Equipment :: " + subscription.getAccessEqpAsgmList().getValue().get(0).getEqpEffTm());
        System.out.println("Equipment :: " + subscription.getAccessEqpAsgmList().getValue().get(0).getEqpEffDt());
        System.out.println("Equipment :: " + subscription.getAccessEqpAsgmList().getValue().get(0).getEqpExprDt());
      }
      if (subscription.getAccessNbrAsgmList() != null) {
        System.out.println("Number :: " + subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbr());
        System.out
            .println("Number :: " + subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffDt());
        System.out
            .println("Number :: " + subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmEffTm());
        System.out.println("Number :: "
            + subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmExprDt());
        System.out.println("Number :: "
            + subscription.getAccessNbrAsgmList().getValue().get(0).getAccessNbrAsgmExprTm());
        System.out.println("Number :: " + subscription.getAccessNbrAsgmList().getValue().get(0).getMSID());
        System.out.println("Number :: " + subscription.getAccessNbrAsgmList().getValue().get(0).getSwitchStatusCd());
      }
    }
  }

  private void expireSubscription(String MDN) {
    // ApiGeneralResponseHolder response = sprintApi.apIexpireSubscription(MDN,
    // null);
    // System.out.println("Response :: "+response.getStatusMessage());
    // System.out.println("APIMessage         :: "+response.getApiResponseMessage());
    // System.out.println("ResponseMessage    :: "+response.getResponseMessage());

    NetworkImpl networkImpl = new NetworkImpl();

    NetworkInfo networkInfo = networkImpl.getNetworkInfo(null, MDN);
    // System.out.println("MDN      :: "+networkInfo.getMdn());
    // System.out.println("MSID     :: "+networkInfo.getMsid());
    // System.out.println("EffDate  :: "+networkInfo.getEffectivedate());
    // System.out.println("EffTime  :: "+networkInfo.getEffectivetime());
    // System.out.println("ExpDate  :: "+networkInfo.getExpirationdate());
    // System.out.println("ExpTime  :: "+networkInfo.getExpirationtime());
    // System.out.println("ESNDec   :: "+networkInfo.getEsnmeiddec());
    // System.out.println("ESNHex   :: "+networkInfo.getEsnmeidhex());
    // System.out.println("Status   :: "+networkInfo.getStatus());

    networkImpl.disconnectService(networkInfo);

  }

  public void reserveMdn() {
    NetworkImpl networkImpl = new NetworkImpl();
    NetworkInfo networkInfo = networkImpl.reserveMDN(null, null, null);

    if (networkInfo != null) {
      System.out.println("MDN      :: " + networkInfo.getMdn());
      System.out.println("MSID     :: " + networkInfo.getMsid());
      System.out.println("EffDate  :: " + networkInfo.getEffectivedate());
      System.out.println("EffTime  :: " + networkInfo.getEffectivetime());
      System.out.println("Status   :: " + networkInfo.getStatus());
    }

  }

  public void activateMdn() {
    NetworkImpl networkImpl = new NetworkImpl();
    NetworkInfo networkInfo = new NetworkInfo();

    networkInfo.setMdn("2138041391");
    networkInfo.setMsid("000002139261688");
    networkInfo.setEsnmeiddec("09608582996");

    networkImpl.activateMDN(networkInfo);

    networkImpl.getNetworkInfo(null, "2138041391");

  }

}
