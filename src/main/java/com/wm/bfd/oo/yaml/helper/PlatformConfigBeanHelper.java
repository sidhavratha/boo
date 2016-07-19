package com.wm.bfd.oo.yaml.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wm.bfd.oo.yaml.PlatformConfigBean;

public final class PlatformConfigBeanHelper {

  private static String CUSTOM_FORMAT = "custom_format";
  private static String CUSTOM_SPLIT = "custom_split";
  private static String CUSTOM_COMPONENT = "custom_component";

  @SuppressWarnings("unchecked")
  public static List<PlatformConfigBean> getExtractBeans(Map<String, Object> extra) {
    List<PlatformConfigBean> platformConfigs = new ArrayList<PlatformConfigBean>();
    for (Map.Entry<String, Object> entry : extra.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      System.out.println(value.getClass());
      if (value instanceof Map) {
        Map<String, String> configMap = (Map<String, String>) value;
        PlatformConfigBean config =
            new PlatformConfigBean(key, configMap.get(CUSTOM_FORMAT), configMap.get(CUSTOM_SPLIT),
                configMap.get(CUSTOM_COMPONENT));
        platformConfigs.add(config);
      }
    }
    return platformConfigs;
  }

}
