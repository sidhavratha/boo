package com.wm.bfd.oo.yaml;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;


public class CustomBean {
  
  private Map<String, String> config;

  @JsonAnyGetter
  public Map<String, String> getConfig() {
    return config;
  }

  @JsonAnySetter
  public void setConfig(String key, String value) {
    this.config.put(key, value);
  }

}
