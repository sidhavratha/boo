package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssemblyBean {
  @JsonProperty("name")
  private String name;

  @JsonProperty("auto_gen")
  private Boolean autoGen;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getAutoGen() {
    return autoGen == null ? Boolean.FALSE : autoGen;
  }

  public void setAutoGen(Boolean autoGen) {
    this.autoGen = autoGen;
  }
}
