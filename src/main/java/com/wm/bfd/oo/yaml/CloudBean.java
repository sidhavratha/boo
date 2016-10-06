package com.wm.bfd.oo.yaml;


public class CloudBean {

  /** The cloud name. */
  private String cloudName;

  /** The pct scale. */
  private String pctScale;

  /** The dpmt order. */
  private String dpmtOrder;

  /** The priority. */
  private String priority;

  /**
   * Instantiates a new cloud bean.
   *
   * @param cloudName the cloud name
   * @param pctScale the pct scale
   * @param dpmtOrder the dpmt order
   * @param priority the priority
   */
  public CloudBean(String cloudName, String pctScale, String dpmtOrder, String priority) {
    this.cloudName = cloudName;
    this.pctScale = pctScale;
    this.dpmtOrder = dpmtOrder;
    this.priority = priority;
  }

  /**
   * Gets the pct scale.
   *
   * @return the pct scale
   */
  public String getPctScale() {
    return pctScale;
  }

  /**
   * Sets the pct scale.
   *
   * @param pctScale the new pct scale
   */
  public void setPctScale(String pctScale) {
    this.pctScale = pctScale;
  }

  /**
   * Gets the dpmt order.
   *
   * @return the dpmt order
   */
  public String getDpmtOrder() {
    return dpmtOrder;
  }

  /**
   * Sets the dpmt order.
   *
   * @param dpmtOrder the new dpmt order
   */
  public void setDpmtOrder(String dpmtOrder) {
    this.dpmtOrder = dpmtOrder;
  }

  /**
   * Gets the priority.
   *
   * @return the priority
   */
  public String getPriority() {
    return priority;
  }

  /**
   * Sets the priority.
   *
   * @param priority the new priority
   */
  public void setPriority(String priority) {
    this.priority = priority;
  }

  /**
   * Gets the cloud name.
   *
   * @return the cloud name
   */
  public String getCloudName() {
    return cloudName;
  }

  /**
   * Sets the cloud name.
   *
   * @param cloudName the new cloud name
   */
  public void setCloudName(String cloudName) {
    this.cloudName = cloudName;
  }

}
