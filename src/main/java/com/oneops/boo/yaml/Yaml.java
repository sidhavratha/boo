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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.oneops.boo.yaml.helper.EnvironmentBeanHelper;
import com.oneops.boo.yaml.helper.PlatformBeanHelper;
import com.oneops.boo.yaml.helper.PlatformConfigBeanHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Yaml {
  private AssemblyBean assembly;
  private BooBean boo;
  private Map<String, Object> environment;
  private Map<String, Object> scale;
  private Map<String, Object> extract;
  private Map<String, Object> others = new HashMap<String, Object>();

  // Build
  @JsonIgnore
  private List<PlatformBean> platformsList;

  @JsonIgnore
  private List<ScaleBean> envList;

  @JsonIgnore
  private EnvironmentBean environmentBean;

  public AssemblyBean getAssembly() {
    return assembly;
  }

  public void setAssembly(AssemblyBean assembly) {
    this.assembly = assembly;
  }

  public BooBean getBoo() {
    return boo;
  }

  public void setBoo(BooBean boo) {
    this.boo = boo;
  }

  @JsonAnyGetter
  public Map<String, Object> getOthers() {
    return others;
  }

  @JsonAnySetter
  public void setOthers(String key, Map<String, Object> value) {
    this.others.put(key, value);
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public Map<String, Object> getPlatforms() {
    return (Map<String, Object>) this.others.get(Constants.PLATFORMS);
  }

  /**
   * Gets the platforms list.
   *
   * @return the platforms list
   */
  @JsonIgnore
  public List<PlatformBean> getPlatformsList() {
    if (platformsList == null) {
      platformsList = PlatformBeanHelper.getPlatforms(this.getPlatforms());
    }
    return platformsList;
  }

  /**
   * Gets the scales.
   *
   * @return the scales
   */
  @JsonIgnore
  public List<ScaleBean> getScales() {
    if (envList == null) {
      envList = EnvironmentBeanHelper.getScales(this.getScale());
    }
    return envList;
  }

  public Map<String, Object> getExtract() {
    // return (Map<String, Object>) this.others.get(Constants.EXTRACT);
    return extract;
  }

  @JsonIgnore
  public Map<String, PlatformConfigBean> getExtractBean() {
    return PlatformConfigBeanHelper.getExtractBeans(this.extract);
  }

  @JsonIgnore
  public EnvironmentBean getEnvironmentBean() {
    return EnvironmentBeanHelper.getEnvironment(this.environment);
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public Map<String, Object> getGlobalVariables() {
    return (Map<String, Object>) this.others.get(Constants.VARIABLES);
  }

  public Map<String, Object> getEnvironment() {
    return environment;
  }

  public void setEnvironment(Map<String, Object> environments) {
    this.environment = environments;
  }

  public Map<String, Object> getScale() {
    return scale;
  }

  public void setScale(Map<String, Object> scale) {
    this.scale = scale;
  }

  public void setExtract(Map<String, Object> extract) {
    this.extract = extract;
  }
}
