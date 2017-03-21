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
package com.oneops.boo.workflow;

import com.oneops.boo.LogUtils;
import com.oneops.boo.yaml.Constants;
import com.oneops.client.api.exception.OneOpsClientAPIException;
import com.oneops.client.api.exception.OneOpsComponentExistException;

import java.util.Map;

public class UpdateComponentTask implements Runnable {
  private BuildAllPlatforms flow;
  private String platformName;
  private String componentName;
  private String uniqueName;
  private Map<String, String> att;

  /**
   * Update the components.
   * 
   */
  public UpdateComponentTask(BuildAllPlatforms flow, String platformName, String componentName,
      String uniqueName, Map<String, String> components) {
    this.flow = flow;
    this.platformName = platformName;
    this.componentName = componentName;
    this.uniqueName = uniqueName;
    this.att = components;
  }

  @Override
  public void run() {
    LogUtils.info(Constants.UPDATE_COMPONENTS2, componentName, uniqueName, platformName);
    Map<String, String> attributes = (Map<String, String>) att;

    boolean isExist = Boolean.FALSE;
    try {
      isExist = flow.isComponentExist(platformName, uniqueName);
    } catch (OneOpsComponentExistException e1) {
      // Ignore
      isExist = Boolean.FALSE;
    } catch (OneOpsClientAPIException e) {
      e.printStackTrace();
    }
    try {
      if (isExist) {
        flow.design.updatePlatformComponent(platformName, uniqueName, attributes);
      } else {
        flow.design.addPlatformComponent(platformName, componentName, uniqueName, attributes);
      }
    } catch (OneOpsClientAPIException e) {
      e.printStackTrace();
    }
  }
}
