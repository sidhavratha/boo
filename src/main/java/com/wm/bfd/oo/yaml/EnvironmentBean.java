package com.wm.bfd.oo.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentBean {

  private Map<String, String> others = new HashMap<String, String>();
  private List<CloudBean> clouds = new ArrayList<CloudBean>();

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
}
