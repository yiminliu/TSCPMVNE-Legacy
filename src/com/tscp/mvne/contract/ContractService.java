package com.tscp.mvne.contract;

import java.util.Date;
import java.util.List;

import com.tscp.mvne.billing.Account;
import com.tscp.mvne.billing.ServiceInstance;
import com.tscp.mvne.contract.dao.KenanContractDao;
import com.tscp.mvne.contract.exception.ContractException;

public class ContractService {

  public int applyContract(KenanContract contract) throws ContractException {
    if (contract.validate()) {
      return KenanContractDao.insertContract(contract);
    } else {
      throw new ContractException("applyContract", "Contract not built properly");
    }
  }

  public void updateContract(KenanContract contract) throws ContractException {
    if (contract.validate()) {
      KenanContractDao.updateContract(contract);
    } else {
      throw new ContractException("updateContract", "Contract not built properly");
    }
  }

  public List<KenanContract> getContracts(Account account, ServiceInstance serviceInstance) throws ContractException {
    return KenanContractDao.getContracts(account, serviceInstance);
  }

  public int applyCouponPayment(Account account, String amount, Date date) throws ContractException {
    return KenanContractDao.applyCouponPayment(account, amount, date);
  }
}
