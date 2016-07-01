package com.wm.bfd.oo.yaml;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "major_version" })
public class YarnBean {

    @JsonProperty("name")
    private String name;
    @SuppressWarnings("unused")
    private String pack;
    private String[] packs;
    private Map<String, String> variables;
    @JsonProperty("encrypted_variables")
    private Map<String, String> secureVariables;

    private ScalBean scale;
    @JsonProperty("components")
    private Components components;

    public Map<String, String> getSecureVariables() {
	return secureVariables;
    }

    public void setSecureVariables(Map<String, String> secureVariables) {
	this.secureVariables = secureVariables;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPack() {
	return packs[1];
    }

    public void setPack(String pack) {
	this.pack = pack;
	this.packs = pack.split("[\\W]");
    }

    public String getPackSource() {
	return packs[0];
    }

    public String getPackVersion() {
	return packs[2];
    }

    public Map<String, String> getVariables() {
	return variables;
    }

    public void setVariables(Map<String, String> variables) {
	this.variables = variables;
    }

    public ScalBean getScale() {
	return scale;
    }

    public void setScale(ScalBean scale) {
	this.scale = scale;
    }

    public Components getComponents() {
	return components;
    }

    public void setComponents(Components components) {
	this.components = components;
    }
}
