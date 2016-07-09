package com.wm.bfd.oo.workflow;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.path.json.JsonPath;
import com.oo.api.OOInstance;
import com.oo.api.exception.OneOpsClientAPIException;
import com.oo.api.resource.model.RedundancyConfig;
import com.wm.bfd.oo.ClientConfig;
import com.wm.bfd.oo.yaml.Constants;
import com.wm.bfd.oo.yaml.PlatformBean;
import com.wm.bfd.oo.yaml.ScalBean;

public class BuildAllPlatforms extends AbstractWorkflow {
  final private static Logger LOG = LoggerFactory.getLogger(BuildAllPlatforms.class);
  final private static String NAME = "ciName"; // Get component name.

  public BuildAllPlatforms(OOInstance instance, ClientConfig config)
      throws OneOpsClientAPIException {
    super(instance, config);
  }

  public boolean isPlatformExist(String platformName) {
    JsonPath response = null;
    try {
      response = design.getPlatform(platformName);
    } catch (OneOpsClientAPIException e) {
      String msg = String.format("The platform %s is not exist! %s", platformName, e.getMessage());
      System.err.println(msg);
    }
    return response == null ? false : true;
  }

  @SuppressWarnings("unchecked")
  public boolean createPlatforms() throws OneOpsClientAPIException {
    List<PlatformBean> platforms = this.config.getYaml().getPlatformsList();
    Collections.sort(platforms);
    this.bar.update(20, 100);
    for (PlatformBean platform : platforms) {
      LOG.info("Creating platform {}", platform.getName());
      this.createPlatform(platform);
      if (platform.getComponents() == null)
        continue;
      for (Map.Entry<String, Object> entry : platform.getComponents().entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        if (value instanceof Map) {
          this.updateComponentVariables(platform.getName(), key, (Map<String, String>) value);
        } else {
          LOG.info("Unknow type {}.", value.getClass());
        }
      }
    }
    return true;
  }

  private boolean createPlatform(PlatformBean platform) throws OneOpsClientAPIException {
    boolean isExist = this.isPlatformExist(platform.getName());
    if (!isExist) {
      JsonPath response =
          design.createPlatform(platform.getName(), platform.getPack(), platform.getPackVersion(),
              platform.getPackSource(), Constants.DESCRIPTION, Constants.DESCRIPTION);
      if (response != null)
        design.commitDesign();
    } else {
      LOG.warn("Platform exist, skip create platform " + platform.getName());
    }
    return true;

  }

  private boolean isComponentExist(String platformName, String componentName) {
    JsonPath j = null;
    try {
      j = design.getPlatformComponent(platformName, componentName);
    } catch (OneOpsClientAPIException e) {
      // e.printStackTrace();
      return false;
    }
    return (j == null ? false : true);
  }

  private boolean updateComponentVariables(String platformName, String componentName,
      Map<String, String> attributes) throws OneOpsClientAPIException {
    String uniqueName = componentName;
    if (componentName.endsWith(Constants.USER)) {
      attributes.put(Constants.DESCRIPTIONS, Constants.DESCRIPTION);
      uniqueName = attributes.get(NAME);
      attributes.remove(NAME);
    }
    LOG.info("Update component {}, attributes {}", componentName, attributes);

    if (this.isComponentExist(platformName, componentName)) {
      design.updatePlatformComponent(platformName, componentName, attributes);
    } else {
      design.addPlatformComponent(platformName, componentName, uniqueName, attributes);
    }
    design.commitDesign();
    this.bar.update(45, 100);
    return true;
  }

  public boolean updateScaling() throws OneOpsClientAPIException {
    List<ScalBean> scales = this.config.getYaml().getEnvList();
    for (ScalBean scale : scales) {
      RedundancyConfig config = new RedundancyConfig();
      config.setCurrent(scale.getCurrent());
      config.setMin(scale.getMin());
      config.setMax(scale.getMax());
      LOG.info("Updating the compute size in {} - {}", envName, scale.getPlatform());
      transition.updatePlatformRedundancyConfig(envName, scale.getPlatform(), config);
    }
    transition.commitEnvironment(envName, null, Constants.DESCRIPTION);
    return true;
  }

}
