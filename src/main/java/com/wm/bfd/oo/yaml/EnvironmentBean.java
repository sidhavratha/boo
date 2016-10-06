package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentBean {

  private Map<String, String> others = new HashMap<String, String>();
  private List<CloudBean> clouds = new ArrayList<CloudBean>();

  @JsonIgnore
  private List<PlatformBean> platformsList;

  public List<CloudBean> getClouds() {
    return clouds;
  }

  public void addClouds(CloudBean cloud) {
    this.clouds.add(cloud);
  }

  public Map<String, String> getOthers() {
    return others;
  }

  public void setOthers(Map<String, String> others) {
    this.others = others;
  }

  public List<PlatformBean> getPlatformsList() {
    return platformsList;
  }

  public void setPlatformsList(List<PlatformBean> platformsList) {
    this.platformsList = platformsList;
  }
}
