package com.wm.bfd.oo.yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wm.bfd.oo.yaml.helper.EnvironmentBeanHelper;
import com.wm.bfd.oo.yaml.helper.PlatformBeanHelper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Yaml {
  private AssemblyBean assembly;
  private BooBean boo;
  private Map<String, Object> environments;
  private Map<String, Object> others = new HashMap<String, Object>();

  // Build
  @JsonIgnore
  private List<PlatformBean> platformsList;

  @JsonIgnore
  private List<ScalBean> envList;

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

  @JsonIgnore
  public List<PlatformBean> getPlatformsList() {
    if (platformsList == null)
      platformsList = PlatformBeanHelper.getPlatforms(this.getPlatforms());
    return platformsList;
  }

  @JsonIgnore
  public List<ScalBean> getEnvList() {
    if (envList == null)
      envList = EnvironmentBeanHelper.getEnvironments(this.getEnvironments());
    return envList;
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public Map<String, Object> getExtract() {
    return (Map<String, Object>) this.others.get(Constants.EXTRACT);
  }

  @SuppressWarnings("unchecked")
  @JsonIgnore
  public Map<String, Object> getGlobalVariables() {
    return (Map<String, Object>) this.others.get(Constants.VARIABLES);
  }

  public Map<String, Object> getEnvironments() {
    return environments;
  }

  public void setEnvironments(Map<String, Object> environments) {
    this.environments = environments;
  }
}
