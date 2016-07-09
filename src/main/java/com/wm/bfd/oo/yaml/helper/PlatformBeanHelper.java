package com.wm.bfd.oo.yaml.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wm.bfd.oo.yaml.PlatformBean;

public final class PlatformBeanHelper {
  final private static Logger LOG = LoggerFactory.getLogger(PlatformBeanHelper.class);
  /**
   * Platform
   */
  final private static String DEPLOY_ORDER = "deploy_order";
  final private static String PACK = "pack";
  final private static String PACK_VERSION = "pack_version";
  final private static String VARIABLES = "variables";
  final private static String ENCRYPTED_VARIABLES = "encrypted_variables";
  final private static String COMPONENTS = "components";

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static List<PlatformBean> getPlatforms(Map<String, Object> platformsMap) {
    List<PlatformBean> platforms = new ArrayList<PlatformBean>();
    for (Map.Entry<String, Object> entry : platformsMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Map) {
        Map platformMap = (Map) value;
        int deployOrder = 0;
        if (platformMap.get(DEPLOY_ORDER) != null) {
          deployOrder = (Integer) platformMap.get(DEPLOY_ORDER);
        }
        platforms.add(new PlatformBean.PlatformBeanBuilder((String) platformMap.get(PACK),
            (String) platformMap.get(PACK_VERSION)).setName(key)
            .setComponents((Map<String, Object>) platformMap.get(COMPONENTS))
            .setSecureVariables((Map<String, String>) platformMap.get(ENCRYPTED_VARIABLES))
            .setVariables((Map<String, String>) platformMap.get(VARIABLES))
            .setDeployOrder(deployOrder).build());
      }
    }
    return platforms;
  }

}
