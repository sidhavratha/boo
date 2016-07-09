package com.wm.bfd.oo.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class Compute {
  private Map<String, String> ips;

  public Compute(Map<String, String> ips) {
    this.ips = ips;
  }

  @JsonAnyGetter
  public Map<String, String> getIps() {
    return ips;
  }
}
