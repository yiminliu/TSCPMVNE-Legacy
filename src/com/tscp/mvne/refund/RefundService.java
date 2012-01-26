package com.tscp.mvne.refund;

import java.util.Date;
import java.util.List;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.payment.dao.CreditCard;

public class RefundService {

  public List<KenanPayment> getKenanPayments(Account account) throws RefundException {
    return KenanPaymentDao.getKenanPayments(account);
  }

  public void reversePayment(Account account, String amount, Date transDate, String trackingId) throws RefundException {
    KenanPaymentDao.reversePayment(account, amount, transDate, trackingId);
  }

  public void applyChargeCredit(CreditCard creditCard, String amount) throws RefundException {
    KenanPaymentDao.applyChargeCredit(creditCard, amount);
  }

}
