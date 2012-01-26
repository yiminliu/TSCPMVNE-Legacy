package com.tscp.mvne.billing;

import java.util.List;

import com.tscp.mvne.billing.exception.BillingException;

public interface BillingInterface {

  public int createAccount(Account account) throws BillingException;

  public void updateAccount(Account account) throws BillingException;

  public void addServiceInstance(Account account, ServiceInstance serviceinstance) throws BillingException;

  public void addPackage(Account account, ServiceInstance serviceinstance, Package iPackage) throws BillingException;

  public void addComponent(Account account, ServiceInstance serviceinstance, Package iPackage, Component componentid)
      throws BillingException;

  public void deleteServiceInstance(Account account, ServiceInstance serviceinstance) throws BillingException;

  public Account getAccountByAccountNo(int account_no) throws BillingException;

  public List<ServiceInstance> getServiceInstanceList(Account account) throws BillingException;

  public List<Package> getPackageList(Account account, ServiceInstance serviceinstance) throws BillingException;

  public List<Component> getComponentList(Account account, ServiceInstance serviceinstance, Package packageinstance)
      throws BillingException;

}