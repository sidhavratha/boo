/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.oneops.boo.yaml;


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
