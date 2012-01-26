package com.tscp.mvne.network;

import java.util.List;

import com.tscp.mvno.webservices.AccessEqpAsgmInfo;
import com.tscp.mvno.webservices.AccessEqpAsgmSeqHolder;
import com.tscp.mvno.webservices.AccessNbrAsgmSeqHolder;
import com.tscp.mvno.webservices.ApiResellerSubInquiryResponseHolder;

public class NetworkInfoUtil {

  public static boolean checkEsn(String esn) {
    return !isEmpty(esn);
  }

  public static boolean checkMdn(String mdn) {
    return !isEmpty(mdn);
  }

  public static boolean isEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }

  public static boolean isEmpty(List list) {
    return list == null || list.isEmpty();
  }

  public static void setNetworkInfo(NetworkInfo networkInfo, ApiResellerSubInquiryResponseHolder subscription)
      throws NetworkException {
    AccessNbrAsgmSeqHolder accessNumberList = subscription.getAccessNbrAsgmList();
    if (accessNumberList != null && !isEmpty(accessNumberList.getValue())) {
      String switchStatus = accessNumberList.getValue().get(0).getSwitchStatusCd();
      if (switchStatus.equalsIgnoreCase("C")) {
        networkInfo.setExpirationdate(accessNumberList.getValue().get(0).getAccessNbrAsgmEffDt());
        networkInfo.setExpirationtime(accessNumberList.getValue().get(0).getAccessNbrAsgmEffTm());
      }
      networkInfo.setMdn(accessNumberList.getValue().get(0).getAccessNbr());
      networkInfo.setMsid(accessNumberList.getValue().get(0).getMSID());
      networkInfo.setStatus(accessNumberList.getValue().get(0).getSwitchStatusCd());
    }
    AccessEqpAsgmSeqHolder equipmentList = subscription.getAccessEqpAsgmList();
    boolean equipFound = false;
    String equipESN;
    if (equipmentList != null && !isEmpty(equipmentList.getValue())) {
      for (AccessEqpAsgmInfo equip : equipmentList.getValue()) {
        equipESN = equip.getESNMEIDDcmlId();
        if (equipESN != null && equipESN.equals(networkInfo.getEsnmeiddec())) {
          networkInfo.setEffectivedate(equip.getEqpEffDt());
          networkInfo.setEffectivetime(equip.getEqpEffTm());
          networkInfo.setEsnmeiddec(equip.getESNMEIDDcmlId());
          networkInfo.setEsnmeidhex(equip.getESNMEIDHexId());
          equipFound = true;
          break;
        }
      }
    }
    if (!equipFound) {
      throw new NetworkException("setNetworkInfo", "Could not find matching ESN in equipment list.");
    }
  }

}
