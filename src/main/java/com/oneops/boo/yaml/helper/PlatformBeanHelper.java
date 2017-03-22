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
import java.util.List;
import java.util.Map;

import com.oneops.boo.yaml.PlatformBean;

public final class PlatformBeanHelper {

  /** The Constant DEPLOY_ORDER. */
  private static final String DEPLOY_ORDER = "deploy_order";

  /** The Constant PACK. */
  private static final String PACK = "pack";

  /** The Constant PACK_VERSION. */
  private static final String PACK_VERSION = "pack_version";

  /** The Constant VARIABLES. */
  private static final String VARIABLES = "variables";

  /** The Constant ENCRYPTED_VARIABLES. */
  private static final String ENCRYPTED_VARIABLES = "encrypted_variables";

  /** The Constant COMPONENTS. */
  private static final String COMPONENTS = "components";

  /**
   * Gets the platforms.
   *
   * @param platformsMap the platforms map
   * @return the platforms
   */
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
