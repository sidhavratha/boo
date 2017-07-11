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
package com.oneops.boo.yaml.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oneops.boo.yaml.CloudBean;
import com.oneops.boo.yaml.EnvironmentBean;
import com.oneops.boo.yaml.ScaleBean;

public final class EnvironmentBeanHelper {
  /**
   * Platform.
   */
  private static final String SCALING = "scaling";
  private static final String CURRENT = "current";
  private static final String MIN = "min";
  private static final String MAX = "max";
  private static final String STEP_UP = "step_up";
  private static final String STEP_DOWN = "step_down";
  private static final String PERCENT_DEPLOY = "percent_deploy";

  private static final String CLOUDS = "clouds";
  private static final String PLATFORMS = "platforms";

  public static final String ADMINSTATUS = "adminstatus";
  public static final String PRIORITY = "priority";
  public static final String DPMT_ORDER = "dpmt_order";
  public static final String PCT_SCALE = "pct_scale";

  /**
   * Gets the scales.
   *
   * @param scaleMap the scale map
   * @return the scales
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static List<ScaleBean> getScales(Map<String, Object> scaleMap) {
    if (scaleMap == null) {
      return null;
    }
    List<ScaleBean> scales = new ArrayList<ScaleBean>();
    for (Map.Entry<String, Object> entry : scaleMap.entrySet()) {
      String platform = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Map) {
        Map<String, Map> map = (Map<String, Map>) value;
        Map<String, Map> scaleMaps = (Map<String, Map>) map.get(SCALING);
        for (Map.Entry<String, Map> scale : scaleMaps.entrySet()) {
          String component = scale.getKey();
          Map<String, String> map2 = scale.getValue();
          // Map<String, String> map2 = ((Map<String, Map>) map.get(SCALING)).get(COMPUTE);
          scales.add(new ScaleBean.ScalBeanBuilder().setComponent(component).setPlatform(platform).setCurrent(map2.get(CURRENT)).setMax(map2.get(MAX)).setMin(map2.get(MIN))
              .setStepDown(map2.get(STEP_DOWN)).setStepUp(map2.get(STEP_UP)).setPercentDeploy(map2.get(PERCENT_DEPLOY)).build());
        }
      }

    }
    return scales;
  }

  /**
   * Gets the environment.
   *
   * @param environmentsMap the environments map
   * @return the environment
   */
  @SuppressWarnings("unchecked")
  public static EnvironmentBean getEnvironment(Map<String, Object> environmentsMap) {
    Map<String, String> attris = new HashMap<String, String>();
    EnvironmentBean env = new EnvironmentBean();
    for (Map.Entry<String, Object> entry : environmentsMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (CLOUDS.equalsIgnoreCase(key)) {
        Map<String, Object> map = (Map<String, Object>) value;
        for (Map.Entry<String, Object> entry1 : map.entrySet()) {
          Map<String, String> config = (Map<String, String>) entry1.getValue();
          CloudBean cloud = new CloudBean(entry1.getKey(), config.get(PCT_SCALE), config.get(DPMT_ORDER), config.get(PRIORITY));
          env.addClouds(cloud);
        }

      } else if (PLATFORMS.equalsIgnoreCase(key)) {
        env.setPlatformsList(PlatformBeanHelper.getPlatforms((Map<String, Object>) value));
      } else if (value instanceof String) {
        attris.put(key, (String) value);
      }
    }

    env.setOthers(attris);
    return env;
  }

  
  /**
   * Gets the environment.
   *
   * @param environmentsMap the environments map
   * @return the environment
   */
  @SuppressWarnings("unchecked")
  public static List<EnvironmentBean> getEnvironments(Map<String, Object> environmentsMap) {
    List<EnvironmentBean> ebList = new ArrayList<EnvironmentBean>();
    for (Map.Entry<String, Object> entry : environmentsMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      EnvironmentBean env = new EnvironmentBean();
      Map<String, String> attris = new HashMap<String, String>();
      env.setEnvName(key);
      if (value instanceof Map) {
	      Map<String, Object> map = (Map<String, Object>) value;
    	  for (Map.Entry<String, Object> entry0 : map.entrySet()) {
    		String key0 = entry0.getKey();
    	    Object value0 = entry0.getValue();
    	    
    	    if (CLOUDS.equalsIgnoreCase(key0)) {
    	        Map<String, Object> cmap = (Map<String, Object>) value0;
    	        for (Map.Entry<String, Object> entry1 : cmap.entrySet()) {
    	          Map<String, String> config = (Map<String, String>) entry1.getValue();
    	          CloudBean cloud = new CloudBean(entry1.getKey(), config.get(PCT_SCALE), config.get(DPMT_ORDER), config.get(PRIORITY));
    	          env.addClouds(cloud);
    	        }

    	      } else if (PLATFORMS.equalsIgnoreCase(key0)) {
    	        env.setPlatformsList(PlatformBeanHelper.getPlatforms((Map<String, Object>) value0));
    	      } else if (value0 instanceof String) {
    	        attris.put(key0, (String) value0);
    	      }
    	  }
      }
      env.setOthers(attris);
      ebList.add(env);
    }

    return ebList;
  }
}
