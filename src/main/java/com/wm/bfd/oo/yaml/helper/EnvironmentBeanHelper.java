package com.wm.bfd.oo.yaml.helper;

import com.wm.bfd.oo.yaml.CloudBean;
import com.wm.bfd.oo.yaml.EnvironmentBean;
import com.wm.bfd.oo.yaml.ScalBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  public static List<ScalBean> getScales(Map<String, Object> scaleMap) {
    if (scaleMap == null) {
      return null;
    }
    List<ScalBean> scales = new ArrayList<ScalBean>();
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
          scales.add(new ScalBean.ScalBeanBuilder().setComponent(component).setPlatform(platform).setCurrent(map2.get(CURRENT)).setMax(map2.get(MAX)).setMin(map2.get(MIN))
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

}
