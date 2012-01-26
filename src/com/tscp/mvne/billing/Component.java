package com.tscp.mvne.billing;

import java.util.Date;

//TODO jpong: Change property names to match java convention. This will require hibernate re-mapping. This needs to be done for all ORM objects.
public class Component {
  private int component_id;
  private int component_instance_id;
  private String component_name;
  private int element_id;
  private Date active_date;
  private Date inactive_date;

  public Component() {
    // do nothing
  }

  public int getComponent_id() {
    return component_id;
  }

  public void setComponent_id(int component_id) {
    this.component_id = component_id;
  }

  public int getComponent_instance_id() {
    return component_instance_id;
  }

  public void setComponent_instance_id(int component_instance_id) {
    this.component_instance_id = component_instance_id;
  }

  public String getComponent_name() {
    return component_name;
  }

  public void setComponent_name(String component_name) {
    this.component_name = component_name;
  }

  public int getElement_id() {
    return element_id;
  }

  public void setElement_id(int element_id) {
    this.element_id = element_id;
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

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("componentId=").append(component_id).append(", ");
    sb.append("instanceId=").append(component_instance_id).append(", ");
    sb.append("componentName=").append(component_name).append(", ");
    sb.append("elementId=").append(element_id).append(", ");
    sb.append("activeDate=").append(active_date).append(", ");
    sb.append("inactiveDate=").append(inactive_date);
    return sb.toString();
  }

  public String toFormattedString() {
    // TODO
    return toString();
  }

}