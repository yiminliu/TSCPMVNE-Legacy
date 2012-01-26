package com.tscp.mvne.billing;

import java.util.Collection;
import java.util.Date;

public class Package {
  private int packageid;
  private int package_instance_id;
  private String package_instance_id_serv;
  private String package_name;
  private Date active_date;
  private Date inactive_date;
  private Collection<Component> componentlist;

  public Package() {
    // do nothing
  }

  public int getPackageid() {
    return packageid;
  }

  public void setPackageid(int packageid) {
    this.packageid = packageid;
  }

  public int getPackage_instance_id() {
    return package_instance_id;
  }

  public void setPackage_instance_id(int package_instance_id) {
    this.package_instance_id = package_instance_id;
  }

  public String getPackage_instance_id_serv() {
    return package_instance_id_serv;
  }

  public void setPackage_instance_id_serv(String package_instance_id_serv) {
    this.package_instance_id_serv = package_instance_id_serv;
  }

  public String getPackage_name() {
    return package_name;
  }

  public void setPackage_name(String package_name) {
    this.package_name = package_name;
  }

  public Date getActive_date() {
    return active_date;
  }

  public void setActive_date(Date active_date) {
    this.active_date = active_date;
  }

  public Date getInactive_date() {
    return inactive_date;
  }

  public void setInactive_date(Date inactive_date) {
    this.inactive_date = inactive_date;
  }

  public Collection<Component> getComponentlist() {
    return componentlist;
  }

  public void setComponentlist(Collection<Component> componentlist) {
    this.componentlist = componentlist;
  }

}