package com.wm.bfd.oo.yaml.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static List<ScalBean> getEnvironments(Map<String, Object> environmentsMap) {
    List<ScalBean> environments = new ArrayList<ScalBean>();
    for (Map.Entry<String, Object> entry : environmentsMap.entrySet()) {
      String platform = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Map) {
        Map<String, Map> map = (Map<String, Map>) value;
        Map<String, Integer> map2 = ((Map<String, Map>) map.get(SCALING)).get(COMPUTE);
        ScalBean scale = new ScalBean(platform, map2.get(CURRENT), map2.get(MAX), map2.get(MIN));
        environments.add(scale);
      }

    }
    return environments;
  }

}
