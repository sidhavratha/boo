package com.wm.bfd.oo.yaml.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wm.bfd.oo.yaml.CloudBean;
import com.wm.bfd.oo.yaml.EnvironmentBean;
import com.wm.bfd.oo.yaml.ScalBean;

public final class EnvironmentBeanHelper {
  final private static Logger LOG = LoggerFactory.getLogger(EnvironmentBeanHelper.class);
  /**
   * Platform
   */
  final private static String SCALING = "scaling";
  final private static String COMPUTE = "compute";
  final private static String CURRENT = "current";
  final private static String MIN = "min";
  final private static String MAX = "max";
  final private static String STEP_UP = "step_up";
  final private static String STEP_DOWN = "step_down";
  final private static String PERCENT_DEPLOY = "percent_deploy";

  final public static String PRIORITY = "priority";
  final public static String DPMT_ORDER = "dpmt_order";
  final public static String PCT_SCALE = "pct_scale";

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static List<ScalBean> getScales(Map<String, Object> scaleMap) {
    List<ScalBean> scales = new ArrayList<ScalBean>();
    for (Map.Entry<String, Object> entry : scaleMap.entrySet()) {
      String platform = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Map) {
        Map<String, Map> map = (Map<String, Map>) value;
        Map<String, Map> s = (Map<String, Map>) map.get(SCALING);
        for (Map.Entry<String, Map> scale : s.entrySet()) {
          String component = scale.getKey();
          Map<String, String> map2 = scale.getValue();
          // Map<String, String> map2 = ((Map<String, Map>) map.get(SCALING)).get(COMPUTE);
          scales.add(new ScalBean.ScalBeanBuilder().setComponent(component).setPlatform(platform)
              .setCurrent(map2.get(CURRENT)).setMax(map2.get(MAX)).setMin(map2.get(MIN))
              .setStepDown(map2.get(STEP_DOWN)).setStepUp(map2.get(STEP_UP))
              .setPercentDeploy(map2.get(PERCENT_DEPLOY)).build());
        }
      }

    }
    return scales;
  }

  @SuppressWarnings("unchecked")
  public static EnvironmentBean getEnvironment(Map<String, Object> environmentsMap) {
    Map<String, String> attris = new HashMap<String, String>();
    EnvironmentBean env = new EnvironmentBean();

    for (Map.Entry<String, Object> entry : environmentsMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Map) {
        Map<String, Object> map = (Map<String, Object>) value;
        for (Map.Entry<String, Object> entry1 : map.entrySet()) {
          Map<String, String> m = (Map<String, String>) entry1.getValue();
          CloudBean cloud =
              new CloudBean(entry1.getKey(), m.get(PCT_SCALE), m.get(DPMT_ORDER), m.get(PRIORITY));
          env.addClouds(cloud);
        }

      } else if (value instanceof String) {
        attris.put(key, (String) value);
      }
    }
    env.setOthers(attris);
    return env;
  }

}
