package com.wm.bfd.oo.workflow;

import com.wm.bfd.oo.LogUtils;
import com.wm.bfd.oo.yaml.Constants;

import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.exception.OneOpsComponentExistException;

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
  public UpdateComponentTask(BuildAllPlatforms flow, String platformName, String componentName, String uniqueName, Map<String, String> components) {
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
