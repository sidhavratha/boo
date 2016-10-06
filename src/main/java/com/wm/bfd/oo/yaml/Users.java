package com.wm.bfd.oo.yaml;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Users {
  private Map<String, Map<String, String>> users = new LinkedHashMap<String, Map<String, String>>();

  @JsonAnyGetter
  public Map<String, Map<String, String>> getUsers() {
    return users;
  }

  @JsonAnySetter
  public void setUsers(String user, Map<String, String> value) {
    this.users.put(user, value);
  }
}
