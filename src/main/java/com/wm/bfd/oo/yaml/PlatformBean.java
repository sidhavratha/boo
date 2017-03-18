/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wm.bfd.oo.yaml;

import java.util.Map;

public class PlatformBean implements Comparable<Object> {

  private int deployOrder;
  private String name;
  private String pack;
  private String packVersion;
  private String[] packs;
  private Map<String, String> variables;
  private Map<String, String> secureVariables;
  private Map<String, Object> components;

  /**
   * Instantiates a new platform bean.
   *
   * @param builder the builder
   */
  public PlatformBean(PlatformBeanBuilder builder) {
    this.name = builder.name;
    this.pack = builder.pack;
    this.packVersion = builder.packVersion;
    this.variables = builder.variables;
    this.secureVariables = builder.secureVariables;
    this.components = builder.components;
    this.deployOrder = builder.deployOrder;
    if (pack != null) {
      this.packs = pack.split("[\\/\\s]");
    }
  }

  public Map<String, String> getSecureVariables() {
    return secureVariables;
  }

  public String getName() {
    return name;
  }

  public String getPack() {
    return packs[1];
  }

  public String getPackSource() {
    return packs[0];
  }

  public String getPackVersion() {
    return packVersion;
  }

  public Map<String, String> getVariables() {
    return variables;
  }

  public int getDeployOrder() {
    return deployOrder;
  }

  public String[] getPacks() {
    return packs;
  }

  public Map<String, Object> getComponents() {
    return components;
  }

  public static class PlatformBeanBuilder {
    private String name;
    private String pack;
    private String packVersion;

    // Optional
    private int deployOrder;
    private Map<String, String> variables;
    private Map<String, String> secureVariables;

    public PlatformBeanBuilder setVariables(Map<String, String> variables) {
      this.variables = variables;
      return this;
    }

    public PlatformBeanBuilder setSecureVariables(Map<String, String> secureVariables) {
      this.secureVariables = secureVariables;
      return this;
    }

    public PlatformBeanBuilder setComponents(Map<String, Object> components) {
      this.components = components;
      return this;
    }

    private Map<String, Object> components;

    public PlatformBeanBuilder(String pack, String packVersion) {
      this.pack = pack;
      this.packVersion = packVersion;
    }

    public PlatformBean build() {
      return new PlatformBean(this);
    }

    public PlatformBeanBuilder setDeployOrder(int deployOrder) {
      this.deployOrder = deployOrder;
      return this;
    }

    public PlatformBeanBuilder setName(String name) {
      this.name = name;
      return this;
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Object object) {
    return this.deployOrder - ((PlatformBean) object).deployOrder;
  }
}
