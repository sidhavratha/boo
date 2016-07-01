package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "major_version", "components" })
public class ZookeeperBean {
    @JsonProperty("name")
    private String name;

    private String pack;

    private String[] packs;

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
}
