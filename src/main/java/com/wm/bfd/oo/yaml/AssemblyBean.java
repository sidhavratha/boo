package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssemblyBean {
    @JsonProperty("name")
    private String name;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
