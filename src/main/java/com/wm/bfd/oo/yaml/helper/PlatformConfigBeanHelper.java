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
package com.wm.bfd.oo.yaml.helper;

import com.wm.bfd.oo.yaml.PlatformConfigBean;

import java.util.HashMap;
import java.util.Map;

public final class PlatformConfigBeanHelper {

  /** The custom format. */
  private static String CUSTOM_FORMAT = "custom_format";

  /** The custom split. */
  private static String CUSTOM_SPLIT = "custom_split";

  /** The custom component. */
  private static String CUSTOM_COMPONENT = "custom_component";

  /**
   * Gets the extract beans.
   *
   * @param extra the extra
   * @return the extract beans
   */
  @SuppressWarnings("unchecked")
  public static Map<String, PlatformConfigBean> getExtractBeans(Map<String, Object> extra) {
    Map<String, PlatformConfigBean> platformConfigs = new HashMap<String, PlatformConfigBean>();
    for (Map.Entry<String, Object> entry : extra.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      System.out.println(value.getClass());
      if (value instanceof Map) {
        Map<String, String> configMap = (Map<String, String>) value;
        PlatformConfigBean config = new PlatformConfigBean(key, configMap.get(CUSTOM_FORMAT), configMap.get(CUSTOM_SPLIT), configMap.get(CUSTOM_COMPONENT));
        platformConfigs.put(key, config);
      }
    }
    return platformConfigs;
  }

}
