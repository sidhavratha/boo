package com.wm.bfd.oo.yaml;



public class CloudBean {

  private String cloudName;
  private String pctScale;
  private String dpmtOrder;
  private String priority;

  public CloudBean(String cloudName, String pctScale, String dpmtOrder, String priority) {
    this.cloudName = cloudName;
    this.pctScale = pctScale;
    this.dpmtOrder = dpmtOrder;
    this.priority = priority;
  }

  public String getPctScale() {
    return pctScale;
  }

  public void setPctScale(String pctScale) {
    this.pctScale = pctScale;
  }

  public String getDpmtOrder() {
    return dpmtOrder;
  }

  public void setDpmtOrder(String dpmtOrder) {
    this.dpmtOrder = dpmtOrder;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getCloudName() {
    return cloudName;
  }

  public void setCloudName(String cloudName) {
    this.cloudName = cloudName;
  }

}
