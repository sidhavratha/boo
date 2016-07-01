package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;


public class BooBean {
    @JsonProperty("oneops_host")
    private String host;
    @JsonProperty("organization")
    private String org;
    @JsonProperty("api_key")
    private String apikey;
    private String email;
    @JsonProperty("cloud_id")
    private String cloudId;
    
    @JsonProperty("ip_output")
    private String ipOutput;
    
    @JsonProperty("environment_name")
    private String envName;

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

    public String getCloudId() {
	return cloudId;
    }

    public void setCloudId(String cloudId) {
	this.cloudId = cloudId;
    }
}
