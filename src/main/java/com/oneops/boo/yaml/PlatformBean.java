/*
 * Copyright 2017 Walmart, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.oneops.boo.yaml;

import java.util.List;
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
  private Map<String, Object> autoHealing;
  private ScaleBean scale;
  private List<String> links;

  public static final String REPLACE_AFTER_MINUTES = "replace_after_minutes";
  public static final String REPLACE_AFTER_REPAIRS = "replace_after_repairs";
  
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
    this.links = builder.links;
    this.autoHealing = builder.autoHealing;
    this.scale = builder.scale;
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

  public List<String> getLinks() {
	return links;
  }

  public Map<String, Object> getAutoHealing() {
	return autoHealing;
  }

  public ScaleBean getScale() {
	return scale;
  }

public static class PlatformBeanBuilder {
    private String name;
    private String pack;
    private String packVersion;

    // Optional
    private int deployOrder;
    private Map<String, String> variables;
    private Map<String, String> secureVariables;
    private List<String> links;
    private Map<String, Object> autoHealing;
    private ScaleBean scale;

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

	public PlatformBeanBuilder setLinks(List<String> links) {
		this.links = links;
		return this;
	}

	public PlatformBeanBuilder setAutoHealing(Map<String, Object> autoHealing) {
		this.autoHealing = autoHealing;
		return this;
	}

	
	public PlatformBeanBuilder setScale(Map<String, Object> scale) {
		if(scale == null || scale.size() ==0 ) {
			return this;
		}
	    for (Map.Entry<String, Object> entry : scale.entrySet()) {
	      Object value = entry.getValue();
	      String component = entry.getKey();
	      if (value instanceof Map) {
	        @SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) value;
	          this.scale = new ScaleBean.ScalBeanBuilder().setComponent(component)
	    		  .setCurrent(map.get(CURRENT))
	    		  .setMax(map.get(MAX))
	    		  .setMin(map.get(MIN))
	              .setStepDown(map.get(STEP_DOWN))
	              .setStepUp(map.get(STEP_UP))
	              .setPercentDeploy(map.get(PERCENT_DEPLOY)).build();
	      }
	    }
	  return this;
	}
	
	 private static final String CURRENT = "current";
	  private static final String MIN = "min";
	  private static final String MAX = "max";
	  private static final String STEP_UP = "step_up";
	  private static final String STEP_DOWN = "step_down";
	  private static final String PERCENT_DEPLOY = "percent_deploy";
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
