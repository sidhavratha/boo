package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BooBean {
  @JsonProperty("oneops_host")
  private String host;
  @JsonProperty("organization")
  private String org;
  @JsonProperty("api_key")
  private String apikey;
  private String email;
  @JsonProperty("cloud")
  private Map<String, Object> cloud;

  @JsonProperty("ip_output")
  private String ipOutput;

  @JsonProperty("environment_name")
  private String envName;

  @JsonProperty("description")
  private String description;

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  @JsonProperty("enable_delivery")
  private boolean enable;

  @JsonProperty("custom")
  private Map<String, String> custom;

  public String getEnvName() {
    return envName;
  }

  public void setEnvName(String envName) {
    this.envName = envName;
  }

  public String getIpOutput() {
    return ipOutput;
  }

  public void setIpOutput(String ipOutput) {
    this.ipOutput = ipOutput;
  }

  public String getHost() {
    if (host != null && host.length() != 0 && host.charAt(host.length() - 1) != '/') {
      return host + "/";
    }
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getOrg() {
    return org;
  }

  public void setOrg(String org) {
    this.org = org;
  }

  public String getApikey() {
    return apikey;
  }

  public void setApikey(String apikey) {
    this.apikey = apikey;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Map<String, Object> getCloud() {
    return cloud;
  }

  public void setCloud(Map<String, Object> cloud) {
    this.cloud = cloud;
  }

  public Map<String, String> getCustom() {
    return custom;
  }

  public void setCustom(Map<String, String> custom) {
    this.custom = custom;
  }
}
